package com.example.companycoreserver.service;

import com.example.companycoreserver.entity.Task;
import com.example.companycoreserver.entity.TaskAssignment;
import com.example.companycoreserver.entity.User;
import com.example.companycoreserver.entity.Enum.TaskStatus;
import com.example.companycoreserver.entity.Enum.TaskType;
import com.example.companycoreserver.entity.Enum.AssignmentRole;
import com.example.companycoreserver.entity.Enum.AssignmentStatus;
import com.example.companycoreserver.repository.TaskRepository;
import com.example.companycoreserver.repository.TaskAssignmentRepository;
import com.example.companycoreserver.repository.UserRepository;
import com.example.companycoreserver.dto.TaskRequest;
import com.example.companycoreserver.dto.TaskResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskAssignmentRepository taskAssignmentRepository;
    private final UserRepository userRepository;

    @Autowired
    public TaskService(TaskRepository taskRepository,
                       TaskAssignmentRepository taskAssignmentRepository,
                       UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.taskAssignmentRepository = taskAssignmentRepository;
        this.userRepository = userRepository;
    }

    // ========================================
    // 🎯 작업 관리 기능
    // ========================================

    /**
     * 작업 생성 (할당까지 한번에 처리) - 주 담당자 개념 완전 제거
     */
    public TaskResponse createTask(TaskRequest request, Long createdByUserId) {
        // 1. 사용자 존재 확인
        validateUserExists(request.getAssignedBy(), "할당자");

        // 2. Task 생성 (주 담당자 개념 제거)
        Task task = new Task(
                request.getAssignedBy(),
                request.getTaskType(),
                request.getTitle(),
                request.getDescription(),
                TaskStatus.TODO,
                createdByUserId
        );

        task.setStartDate(request.getStartDate());
        task.setEndDate(request.getEndDate());
        Task savedTask = taskRepository.save(task);

        // 3. 할당자들 처리 (모든 할당자를 동등하게 처리)
        if (request.getAssigneeIds() != null && !request.getAssigneeIds().isEmpty()) {
            for (int i = 0; i < request.getAssigneeIds().size(); i++) {
                Long userId = request.getAssigneeIds().get(i);
                validateUserExists(userId, "할당자");

                AssignmentRole role = (request.getAssigneeRoles() != null && i < request.getAssigneeRoles().size())
                        ? request.getAssigneeRoles().get(i)
                        : AssignmentRole.ASSIGNEE; // 기본값

                createAssignment(savedTask.getTaskId(), userId, role, request.getAssignedBy());
            }
        }

        return convertToTaskResponse(savedTask);
    }

    /**
     * 작업 수정
     */
    public TaskResponse updateTask(Long taskId, TaskRequest request, Long updatedByUserId) {
        Task task = getTaskById(taskId);
        validateUpdatePermission(task, updatedByUserId);

        // 필드 업데이트 (null이 아닌 것만)
        updateTaskFields(task, request);
        Task updatedTask = taskRepository.save(task);

        return convertToTaskResponse(updatedTask);
    }

    /**
     * 작업 상태 변경 (할당 상태도 함께 업데이트)
     */
    public TaskResponse updateTaskStatus(Long taskId, TaskStatus newStatus, Long updatedByUserId) {
        Task task = getTaskById(taskId);
        validateUpdatePermission(task, updatedByUserId);

        task.setStatus(newStatus);

        // 상태에 따른 추가 처리
        switch (newStatus) {
            case IN_PROGRESS:
                task.markAsInProgress();
                break;
            case DONE:
                task.markAsDone();
                completeAllAssignments(taskId);
                break;
            case CANCELLED:
                task.markAsCancelled();
                cancelAllAssignments(taskId);
                break;
        }

        Task updatedTask = taskRepository.save(task);
        return convertToTaskResponse(updatedTask);
    }

    /**
     * 작업 삭제 (할당 정보도 함께 삭제)
     */
    public void deleteTask(Long taskId, Long deletedByUserId) {
        Task task = getTaskById(taskId);
        validateDeletePermission(task, deletedByUserId);

        // 할당 정보 먼저 삭제
        taskAssignmentRepository.deleteByTaskId(taskId);
        // 작업 삭제
        taskRepository.deleteById(taskId);
    }

    // ========================================
    // 🎯 할당 관리 기능
    // ========================================

    /**
     * 작업에 사용자 할당
     */
    public TaskResponse assignUserToTask(Long taskId, Long userId, AssignmentRole role, Long assignedByUserId) {
        validateTaskExists(taskId);
        validateUserExists(userId, "할당 대상자");

        // 중복 할당 확인
        if (isUserAlreadyAssigned(taskId, userId)) {
            throw new IllegalArgumentException("이미 해당 작업에 할당된 사용자입니다.");
        }

        createAssignment(taskId, userId, role, assignedByUserId);

        // 업데이트된 작업 정보 반환
        Task task = getTaskById(taskId);
        return convertToTaskResponse(task);
    }

    /**
     * 할당 해제
     */
    public TaskResponse unassignUser(Long taskId, Long userId, Long unassignedByUserId) {
        TaskAssignment assignment = getActiveAssignment(taskId, userId);
        validateUnassignPermission(assignment, unassignedByUserId);

        assignment.updateStatus(AssignmentStatus.CANCELED);
        taskAssignmentRepository.save(assignment);

        // 업데이트된 작업 정보 반환
        Task task = getTaskById(taskId);
        return convertToTaskResponse(task);
    }

    /**
     * 할당 역할 변경
     */
    public TaskResponse updateAssignmentRole(Long taskId, Long userId, AssignmentRole newRole, Long updatedByUserId) {
        TaskAssignment assignment = getActiveAssignment(taskId, userId);
        validateAssignmentUpdatePermission(assignment, updatedByUserId);

        assignment.setRole(newRole);
        taskAssignmentRepository.save(assignment);

        // 업데이트된 작업 정보 반환
        Task task = getTaskById(taskId);
        return convertToTaskResponse(task);
    }

    // ========================================
    // 🎯 조회 기능 - Repository 메서드 활용
    // ========================================

    /**
     * 작업 상세 조회 (할당 정보 포함)
     */
    @Transactional(readOnly = true)
    public TaskResponse getTask(Long taskId) {
        Task task = taskRepository.findTaskWithAssignments(taskId)
                .orElseThrow(() -> new IllegalArgumentException("작업을 찾을 수 없습니다: " + taskId));
        return convertToTaskResponse(task);
    }

    /**
     * 사용자의 작업 목록 조회
     */
    @Transactional(readOnly = true)
    public List<TaskResponse> getUserTasks(Long userId, TaskStatus status) {
        List<Task> tasks;

        if (status != null) {
            // 특정 상태의 작업만 조회
            tasks = taskRepository.findUserTasksByStatus(userId, status);
        } else {
            // 모든 활성 할당 작업 조회
            tasks = taskRepository.findTasksAssignedToUser(userId);
        }

        return tasks.stream()
                .map(this::convertToTaskResponse)
                .collect(Collectors.toList());
    }

    /**
     * 역할별 사용자 작업 조회
     */
    @Transactional(readOnly = true)
    public List<TaskResponse> getUserTasksByRole(Long userId, AssignmentRole role) {
        List<Task> tasks = taskRepository.findTasksAssignedToUserByRole(userId, role);

        return tasks.stream()
                .map(this::convertToTaskResponse)
                .collect(Collectors.toList());
    }

    /**
     * 담당자 작업 조회 (ASSIGNEE 역할)
     */
    @Transactional(readOnly = true)
    public List<TaskResponse> getAssigneeTasks(Long userId) {
        return getUserTasksByRole(userId, AssignmentRole.ASSIGNEE);
    }

    /**
     * 리뷰 작업 조회 (REVIEWER 역할)
     */
    @Transactional(readOnly = true)
    public List<TaskResponse> getReviewTasks(Long userId) {
        return getUserTasksByRole(userId, AssignmentRole.REVIEWER);
    }

    /**
     * 관찰 작업 조회 (OBSERVER 역할)
     */
    @Transactional(readOnly = true)
    public List<TaskResponse> getObservedTasks(Long userId) {
        return getUserTasksByRole(userId, AssignmentRole.OBSERVER);
    }

    /**
     * 작업 검색
     */
    @Transactional(readOnly = true)
    public List<TaskResponse> searchTasks(String keyword, Long userId) {
        List<Task> tasks;
        if (userId != null) {
            tasks = taskRepository.searchUserTasksByKeyword(userId, keyword);
        } else {
            tasks = taskRepository.searchTasksByKeyword(keyword);
        }

        return tasks.stream()
                .map(this::convertToTaskResponse)
                .collect(Collectors.toList());
    }

    /**
     * 부서별 작업 조회
     */
    @Transactional(readOnly = true)
    public List<TaskResponse> getTasksByDepartment(String department) {
        List<Task> tasks = taskRepository.findTasksByDepartment(department);

        return tasks.stream()
                .map(this::convertToTaskResponse)
                .collect(Collectors.toList());
    }

    /**
     * 대시보드용 통합 정보
     */
    @Transactional(readOnly = true)
    public DashboardDto getDashboardData(Long userId) {
        // 작업 통계
        TaskStatsDto taskStats = getTaskStats(userId);

        // 할당 통계
        AssignmentStatsDto assignmentStats = getAssignmentStats(userId);

        // 최근 작업들
        List<TaskResponse> recentTasks = getRecentTasks(userId, 5);

        // 오늘 마감 작업들
        List<TaskResponse> todayDeadlines = getTodayDeadlineTasks(userId);

        // 연체 작업들
        List<TaskResponse> overdueTasks = getOverdueTasks(userId);

        return new DashboardDto(taskStats, assignmentStats, recentTasks, todayDeadlines, overdueTasks);
    }

    // ========================================
    // 🎯 통계 기능
    // ========================================

    @Transactional(readOnly = true)
    public TaskStatsDto getTaskStats(Long userId) {
        long todoCount = taskRepository.countUserTasksByStatus(userId, TaskStatus.TODO);
        long inProgressCount = taskRepository.countUserTasksByStatus(userId, TaskStatus.IN_PROGRESS);
        long doneCount = taskRepository.countUserTasksByStatus(userId, TaskStatus.DONE);
        long totalCount = taskRepository.countUserTasks(userId);

        return new TaskStatsDto(todoCount, inProgressCount, doneCount, totalCount);
    }

    @Transactional(readOnly = true)
    public AssignmentStatsDto getAssignmentStats(Long userId) {
        long activeCount = taskAssignmentRepository.countByUserIdAndStatus(userId, AssignmentStatus.ACTIVE);
        long completedCount = taskAssignmentRepository.countByUserIdAndStatus(userId, AssignmentStatus.COMPLETED);
        long assigneeCount = taskAssignmentRepository
                .countByUserIdAndRoleAndStatus(userId, AssignmentRole.ASSIGNEE, AssignmentStatus.ACTIVE);
        long reviewerCount = taskAssignmentRepository
                .countByUserIdAndRoleAndStatus(userId, AssignmentRole.REVIEWER, AssignmentStatus.ACTIVE);
        long observerCount = taskAssignmentRepository
                .countByUserIdAndRoleAndStatus(userId, AssignmentRole.OBSERVER, AssignmentStatus.ACTIVE);

        return new AssignmentStatsDto(activeCount, completedCount, assigneeCount, reviewerCount, observerCount);
    }

    // ========================================
    // 🛠️ Private Helper Methods
    // ========================================

    private TaskAssignment createAssignment(Long taskId, Long userId, AssignmentRole role, Long assignedBy) {
        TaskAssignment assignment = new TaskAssignment(taskId, userId, role, assignedBy);
        return taskAssignmentRepository.save(assignment);
    }

    private Task getTaskById(Long taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("작업을 찾을 수 없습니다: " + taskId));
    }

    private TaskAssignment getActiveAssignment(Long taskId, Long userId) {
        return taskAssignmentRepository.findByTaskIdAndUserIdAndStatus(taskId, userId, AssignmentStatus.ACTIVE)
                .orElseThrow(() -> new IllegalArgumentException("활성 할당 정보를 찾을 수 없습니다."));
    }

    private boolean isUserAlreadyAssigned(Long taskId, Long userId) {
        return taskAssignmentRepository.existsByTaskIdAndUserIdAndStatus(taskId, userId, AssignmentStatus.ACTIVE);
    }

    private void validateTaskExists(Long taskId) {
        if (!taskRepository.existsById(taskId)) {
            throw new IllegalArgumentException("작업이 존재하지 않습니다: " + taskId);
        }
    }

    private void validateUserExists(Long userId, String userType) {
        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException(userType + "가 존재하지 않습니다: " + userId);
        }
    }

    private void validateUpdatePermission(Task task, Long userId) {
        if (!hasTaskPermission(task, userId)) {
            throw new IllegalArgumentException("작업을 수정할 권한이 없습니다.");
        }
    }

    private void validateDeletePermission(Task task, Long userId) {
        if (!task.getCreatedBy().equals(userId) && !task.getAssignedBy().equals(userId)) {
            throw new IllegalArgumentException("작업을 삭제할 권한이 없습니다.");
        }
    }

    private void validateUnassignPermission(TaskAssignment assignment, Long userId) {
        if (!assignment.getAssignedBy().equals(userId) && !assignment.getUserId().equals(userId)) {
            throw new IllegalArgumentException("할당을 해제할 권한이 없습니다.");
        }
    }

    private void validateAssignmentUpdatePermission(TaskAssignment assignment, Long userId) {
        if (!assignment.getAssignedBy().equals(userId)) {
            throw new IllegalArgumentException("할당 정보를 수정할 권한이 없습니다.");
        }
    }

    private boolean hasTaskPermission(Task task, Long userId) {
        return task.getCreatedBy().equals(userId) ||
                task.getAssignedBy().equals(userId) ||
                taskAssignmentRepository.existsByTaskIdAndUserIdAndStatus(task.getTaskId(), userId, AssignmentStatus.ACTIVE);
    }

    private void updateTaskFields(Task task, TaskRequest request) {
        if (request.getTitle() != null) task.setTitle(request.getTitle());
        if (request.getDescription() != null) task.setDescription(request.getDescription());
        if (request.getStartDate() != null) task.setStartDate(request.getStartDate());
        if (request.getEndDate() != null) task.setEndDate(request.getEndDate());
        if (request.getTaskType() != null) task.setTaskType(request.getTaskType());
    }

    private void completeAllAssignments(Long taskId) {
        updateAllAssignmentsStatus(taskId, AssignmentStatus.COMPLETED);
    }

    private void cancelAllAssignments(Long taskId) {
        updateAllAssignmentsStatus(taskId, AssignmentStatus.CANCELED);
    }

    private void updateAllAssignmentsStatus(Long taskId, AssignmentStatus status) {
        List<TaskAssignment> activeAssignments = taskAssignmentRepository
                .findByTaskIdAndStatus(taskId, AssignmentStatus.ACTIVE);

        activeAssignments.forEach(assignment -> {
            assignment.updateStatus(status);
            taskAssignmentRepository.save(assignment);
        });
    }

    // ========================================
    // 🔄 DTO 변환 메서드 - 주 담당자 개념 완전 제거
    // ========================================

    private TaskResponse convertToTaskResponse(Task task) {
        TaskResponse response = new TaskResponse();

        // Task 기본 정보 설정
        response.setTaskId(task.getTaskId());
        response.setTitle(task.getTitle());
        response.setDescription(task.getDescription());
        response.setStartDate(task.getStartDate());
        response.setEndDate(task.getEndDate());
        response.setStatus(task.getStatus());
        response.setTaskType(task.getTaskType());
        response.setCreatedAt(task.getCreatedAt());
        response.setUpdatedAt(task.getUpdatedAt());
        response.setAssignedBy(task.getAssignedBy());
        response.setCreatedBy(task.getCreatedBy());

        // 사용자 이름들 설정
        response.setAssignedByName(getUserName(task.getAssignedBy()));
        response.setCreatedByName(getUserName(task.getCreatedBy()));

        // 할당자 정보 설정 - 모든 할당자를 동등하게 처리
        List<TaskAssignment> assignments = taskAssignmentRepository
                .findByTaskIdAndStatus(task.getTaskId(), AssignmentStatus.ACTIVE);

        List<TaskResponse.AssigneeInfo> assignees = assignments.stream()
                .map(assignment -> new TaskResponse.AssigneeInfo(
                        assignment.getUserId(),
                        getUserName(assignment.getUserId()),
                        assignment.getRole()
                ))
                .collect(Collectors.toList());

        response.setAssignees(assignees);

        return response;
    }

    private String getUserName(Long userId) {
        return userRepository.findById(userId)
                .map(User::getUsername)
                .orElse("Unknown");
    }

    // ========================================
    // 📅 날짜 기반 조회 메서드들
    // ========================================

    @Transactional(readOnly = true)
    public List<TaskResponse> getRecentTasks(Long userId, int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        List<TaskAssignment> recentAssignments = taskAssignmentRepository
                .findRecentAssignmentsByUser(userId, pageable);

        return recentAssignments.stream()
                .map(assignment -> convertToTaskResponse(assignment.getTask()))
                .distinct()
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> getTodayDeadlineTasks(Long userId) {
        LocalDate today = LocalDate.now();
        List<Task> tasks = taskRepository.findUrgentTasks(userId, today, today);

        return tasks.stream()
                .map(this::convertToTaskResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> getOverdueTasks(Long userId) {
        LocalDate today = LocalDate.now();
        List<Task> tasks = taskRepository.findUserOverdueTasks(userId, today);

        return tasks.stream()
                .map(this::convertToTaskResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> getUserTasksByDateRange(Long userId, LocalDate startDate, LocalDate endDate) {
        List<Task> tasks = taskRepository.findUserTasksByDateRange(userId, startDate, endDate);

        return tasks.stream()
                .map(this::convertToTaskResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> getUserTasksByEndDate(Long userId, LocalDate endDate) {
        List<Task> tasks = taskRepository.findUserTasksByEndDate(userId, endDate);

        return tasks.stream()
                .map(this::convertToTaskResponse)
                .collect(Collectors.toList());
    }

    // ========================================
    // 📊 내부 DTO 클래스들 - AssignmentStatsDto에 observerCount 추가
    // ========================================

    public static class TaskStatsDto {
        private long todoCount;
        private long inProgressCount;
        private long doneCount;
        private long totalCount;

        public TaskStatsDto(long todoCount, long inProgressCount, long doneCount, long totalCount) {
            this.todoCount = todoCount;
            this.inProgressCount = inProgressCount;
            this.doneCount = doneCount;
            this.totalCount = totalCount;
        }

        // Getters
        public long getTodoCount() { return todoCount; }
        public long getInProgressCount() { return inProgressCount; }
        public long getDoneCount() { return doneCount; }
        public long getTotalCount() { return totalCount; }
    }

    public static class AssignmentStatsDto {
        private long activeCount;
        private long completedCount;
        private long assigneeCount;
        private long reviewerCount;
        private long observerCount;

        public AssignmentStatsDto(long activeCount, long completedCount, long assigneeCount,
                                  long reviewerCount, long observerCount) {
            this.activeCount = activeCount;
            this.completedCount = completedCount;
            this.assigneeCount = assigneeCount;
            this.reviewerCount = reviewerCount;
            this.observerCount = observerCount;
        }

        // Getters
        public long getActiveCount() { return activeCount; }
        public long getCompletedCount() { return completedCount; }
        public long getAssigneeCount() { return assigneeCount; }
        public long getReviewerCount() { return reviewerCount; }
        public long getObserverCount() { return observerCount; }
    }

    public static class DashboardDto {
        private TaskStatsDto taskStats;
        private AssignmentStatsDto assignmentStats;
        private List<TaskResponse> recentTasks;
        private List<TaskResponse> todayDeadlines;
        private List<TaskResponse> overdueTasks;

        public DashboardDto(TaskStatsDto taskStats, AssignmentStatsDto assignmentStats,
                            List<TaskResponse> recentTasks, List<TaskResponse> todayDeadlines,
                            List<TaskResponse> overdueTasks) {
            this.taskStats = taskStats;
            this.assignmentStats = assignmentStats;
            this.recentTasks = recentTasks;
            this.todayDeadlines = todayDeadlines;
            this.overdueTasks = overdueTasks;
        }

        // Getters
        public TaskStatsDto getTaskStats() { return taskStats; }
        public AssignmentStatsDto getAssignmentStats() { return assignmentStats; }
        public List<TaskResponse> getRecentTasks() { return recentTasks; }
        public List<TaskResponse> getTodayDeadlines() { return todayDeadlines; }
        public List<TaskResponse> getOverdueTasks() { return overdueTasks; }
    }
}
