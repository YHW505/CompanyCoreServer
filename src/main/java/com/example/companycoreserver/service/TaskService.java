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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskAssignmentRepository taskAssignmentRepository;

    @Autowired
    private UserRepository userRepository;

    // 모든 작업 조회
    public List<Task> getAllTasks() {
        try {
            List<Task> tasks = taskRepository.findAll();
            System.out.println("Found " + tasks.size() + " tasks");
            return tasks;
        } catch (Exception e) {
            System.err.println("Error fetching all tasks: " + e.getMessage());
            throw new RuntimeException("Failed to fetch tasks", e);
        }
    }

    // ID로 작업 조회
    public Optional<Task> getTaskById(Integer taskId) {
        try {
            return taskRepository.findById(taskId);
        } catch (Exception e) {
            System.err.println("Error fetching task by id: " + e.getMessage());
            throw new RuntimeException("Failed to fetch task", e);
        }
    }

    // ✅ 특정 사용자가 할당받은 작업 조회 (TaskAssignment 기반)
    public List<Task> getTasksByAssignedTo(Long userId) {
        try {
            List<Task> tasks = taskRepository.findTasksAssignedToUser(userId);
            System.out.println("Found " + tasks.size() + " tasks assigned to user: " + userId);
            return tasks;
        } catch (Exception e) {
            System.err.println("Error fetching tasks by assignedTo: " + e.getMessage());
            throw new RuntimeException("Failed to fetch tasks", e);
        }
    }

    // ✅ 특정 사용자가 생성한 작업 조회 (createdBy 기반)
    public List<Task> getTasksByCreatedBy(Long createdBy) {
        try {
            List<Task> tasks = taskRepository.findByCreatedByOrderByCreatedAtDesc(createdBy);
            System.out.println("Found " + tasks.size() + " tasks created by user: " + createdBy);
            return tasks;
        } catch (Exception e) {
            System.err.println("Error fetching tasks by createdBy: " + e.getMessage());
            throw new RuntimeException("Failed to fetch tasks", e);
        }
    }

    // 상태별 작업 조회
    public List<Task> getTasksByStatus(TaskStatus status) {
        try {
            List<Task> tasks = taskRepository.findByStatusOrderByCreatedAtDesc(status);
            System.out.println("Found " + tasks.size() + " tasks with status: " + status);
            return tasks;
        } catch (Exception e) {
            System.err.println("Error fetching tasks by status: " + e.getMessage());
            throw new RuntimeException("Failed to fetch tasks", e);
        }
    }

    // 작업 타입별 조회
    public List<Task> getTasksByType(TaskType taskType) {
        try {
            List<Task> tasks = taskRepository.findByTaskTypeOrderByCreatedAtDesc(taskType);
            System.out.println("Found " + tasks.size() + " tasks with type: " + taskType);
            return tasks;
        } catch (Exception e) {
            System.err.println("Error fetching tasks by type: " + e.getMessage());
            throw new RuntimeException("Failed to fetch tasks", e);
        }
    }

    // ✅ 특정 사용자가 생성한 특정 상태 작업 조회
    public List<Task> getTasksByCreatedByAndStatus(Long createdBy, TaskStatus status) {
        try {
            return taskRepository.findByCreatedByAndStatusOrderByCreatedAtDesc(createdBy, status);
        } catch (Exception e) {
            System.err.println("Error fetching tasks by createdBy and status: " + e.getMessage());
            throw new RuntimeException("Failed to fetch tasks", e);
        }
    }

    // ✅ 특정 사용자가 생성한 특정 타입 작업 조회
    public List<Task> getTasksByCreatedByAndType(Long createdBy, TaskType taskType) {
        try {
            return taskRepository.findByCreatedByAndTaskTypeOrderByCreatedAtDesc(createdBy, taskType);
        } catch (Exception e) {
            System.err.println("Error fetching tasks by createdBy and taskType: " + e.getMessage());
            throw new RuntimeException("Failed to fetch tasks", e);
        }
    }

    // ✅ 날짜 범위로 작업 조회 (시작일 기준)
    public List<Task> getTasksByDateRange(LocalDate startDate, LocalDate endDate) {
        try {
            return taskRepository.findByStartDateRange(startDate, endDate);
        } catch (Exception e) {
            System.err.println("Error fetching tasks by date range: " + e.getMessage());
            throw new RuntimeException("Failed to fetch tasks", e);
        }
    }

    // 🆕 마감일 범위로 작업 조회
    public List<Task> getTasksByEndDateRange(LocalDate startDate, LocalDate endDate) {
        try {
            return taskRepository.findByEndDateRange(startDate, endDate);
        } catch (Exception e) {
            System.err.println("Error fetching tasks by end date range: " + e.getMessage());
            throw new RuntimeException("Failed to fetch tasks", e);
        }
    }

    // 제목으로 검색
    public List<Task> searchTasksByTitle(String keyword) {
        try {
            return taskRepository.findByTitleContaining(keyword);
        } catch (Exception e) {
            System.err.println("Error searching tasks by title: " + e.getMessage());
            throw new RuntimeException("Failed to search tasks", e);
        }
    }

    // 🆕 제목 또는 설명으로 검색
    public List<Task> searchTasksByTitleOrDescription(String keyword) {
        try {
            return taskRepository.findByTitleOrDescriptionContaining(keyword);
        } catch (Exception e) {
            System.err.println("Error searching tasks by title or description: " + e.getMessage());
            throw new RuntimeException("Failed to search tasks", e);
        }
    }

    // ✅ 복합 조건 검색 (수정됨)
    public List<Task> getTasksByMultipleConditions(Long createdBy, TaskStatus status, TaskType taskType) {
        try {
            return taskRepository.findByMultipleConditions(createdBy, status, taskType);
        } catch (Exception e) {
            System.err.println("Error fetching tasks by multiple conditions: " + e.getMessage());
            throw new RuntimeException("Failed to fetch tasks", e);
        }
    }

    // ✅ 작업 생성 (첨부파일 포함)
    @Transactional
    public Task createTask(Task task, MultipartFile attachmentFile) {
        try {
            // 생성 시간 설정
            task.setCreatedAt(LocalDateTime.now());
            task.setUpdatedAt(LocalDateTime.now());

            // 첨부파일이 있는 경우 처리
            if (attachmentFile != null && !attachmentFile.isEmpty()) {
                // 바이트 배열을 Base64 문자열로 인코딩
                String base64Content = java.util.Base64.getEncoder().encodeToString(attachmentFile.getBytes());

                task.updateAttachment(
                        attachmentFile.getOriginalFilename(),
                        attachmentFile.getContentType(),
                        base64Content
                );
                // 파일 크기 설정
                task.setAttachmentSize(attachmentFile.getSize());
            }

            Task savedTask = taskRepository.save(task);
            System.out.println("Task created successfully with ID: " + savedTask.getTaskId());
            return savedTask;
        } catch (IOException e) {
            System.err.println("Error processing attachment file: " + e.getMessage());
            throw new RuntimeException("Failed to process attachment file", e);
        } catch (Exception e) {
            System.err.println("Error creating task: " + e.getMessage());
            throw new RuntimeException("Failed to create task", e);
        }
    }

    // ✅ 작업 생성 (첨부파일 없이)
    @Transactional
    public Task createTask(Task task) {
        try {
            // 생성 시간 설정
            task.setCreatedAt(LocalDateTime.now());
            task.setUpdatedAt(LocalDateTime.now());

            Task savedTask = taskRepository.save(task);
            System.out.println("Task created successfully with ID: " + savedTask.getTaskId());
            return savedTask;
        } catch (Exception e) {
            System.err.println("Error creating task: " + e.getMessage());
            throw new RuntimeException("Failed to create task", e);
        }
    }

    // ✅ 작업 업데이트 (수정됨)
    @Transactional
    public Task updateTask(Integer taskId, Task updatedTask) {
        try {
            Optional<Task> existingTaskOpt = taskRepository.findById(taskId);
            if (existingTaskOpt.isPresent()) {
                Task existingTask = existingTaskOpt.get();

                // 기본 필드 업데이트
                if (updatedTask.getTitle() != null) {
                    existingTask.setTitle(updatedTask.getTitle());
                }
                if (updatedTask.getDescription() != null) {
                    existingTask.setDescription(updatedTask.getDescription());
                }
                if (updatedTask.getStatus() != null) {
                    existingTask.setStatus(updatedTask.getStatus());
                }
                if (updatedTask.getTaskType() != null) {
                    existingTask.setTaskType(updatedTask.getTaskType());
                }
                if (updatedTask.getStartDate() != null) {
                    existingTask.setStartDate(updatedTask.getStartDate());
                }
                if (updatedTask.getEndDate() != null) {
                    existingTask.setEndDate(updatedTask.getEndDate());
                }

                // 업데이트 시간 설정
                existingTask.setUpdatedAt(LocalDateTime.now());

                Task savedTask = taskRepository.save(existingTask);
                System.out.println("Task updated successfully: " + taskId);
                return savedTask;
            } else {
                throw new RuntimeException("Task not found with id: " + taskId);
            }
        } catch (Exception e) {
            System.err.println("Error updating task: " + e.getMessage());
            throw new RuntimeException("Failed to update task", e);
        }
    }

    // ✅ 첨부파일 업데이트
    @Transactional
    public Task updateTaskAttachment(Integer taskId, MultipartFile attachmentFile) {
        try {
            Optional<Task> taskOpt = taskRepository.findById(taskId);
            if (taskOpt.isPresent()) {
                Task task = taskOpt.get();

                if (attachmentFile != null && !attachmentFile.isEmpty()) {
                    // 바이트 배열을 Base64 문자열로 인코딩
                    String base64Content = java.util.Base64.getEncoder().encodeToString(attachmentFile.getBytes());

                    task.updateAttachment(
                            attachmentFile.getOriginalFilename(),
                            attachmentFile.getContentType(),
                            base64Content
                    );
                    task.setAttachmentSize(attachmentFile.getSize());
                } else {
                    task.removeAttachment();
                }

                task.setUpdatedAt(LocalDateTime.now());
                Task savedTask = taskRepository.save(task);
                System.out.println("Task attachment updated successfully: " + taskId);
                return savedTask;
            } else {
                throw new RuntimeException("Task not found with id: " + taskId);
            }
        } catch (IOException e) {
            System.err.println("Error processing attachment file: " + e.getMessage());
            throw new RuntimeException("Failed to process attachment file", e);
        } catch (Exception e) {
            System.err.println("Error updating task attachment: " + e.getMessage());
            throw new RuntimeException("Failed to update task attachment", e);
        }
    }

    // ✅ 첨부파일 제거
    @Transactional
    public Task removeTaskAttachment(Integer taskId) {
        try {
            Optional<Task> taskOpt = taskRepository.findById(taskId);
            if (taskOpt.isPresent()) {
                Task task = taskOpt.get();
                task.removeAttachment();
                task.setUpdatedAt(LocalDateTime.now());

                Task savedTask = taskRepository.save(task);
                System.out.println("Task attachment removed successfully: " + taskId);
                return savedTask;
            } else {
                throw new RuntimeException("Task not found with id: " + taskId);
            }
        } catch (Exception e) {
            System.err.println("Error removing task attachment: " + e.getMessage());
            throw new RuntimeException("Failed to remove task attachment", e);
        }
    }

    // ✅ 작업 삭제 (TaskAssignment도 함께 삭제됨 - CASCADE)
    @Transactional
    public boolean deleteTask(Integer taskId) {
        try {
            if (taskRepository.existsById(taskId)) {
                taskRepository.deleteById(taskId);
                System.out.println("Task deleted successfully: " + taskId);
                return true;
            } else {
                System.out.println("Task not found for deletion: " + taskId);
                return false;
            }
        } catch (Exception e) {
            System.err.println("Error deleting task: " + e.getMessage());
            throw new RuntimeException("Failed to delete task", e);
        }
    }

    // ✅ 작업 상태 업데이트
    @Transactional
    public Task updateTaskStatus(Integer taskId, TaskStatus newStatus) {
        try {
            Optional<Task> taskOpt = taskRepository.findById(taskId);
            if (taskOpt.isPresent()) {
                Task task = taskOpt.get();
                task.setStatus(newStatus);
                task.setUpdatedAt(LocalDateTime.now());

                Task savedTask = taskRepository.save(task);
                System.out.println("Task status updated successfully: " + taskId + " -> " + newStatus);
                return savedTask;
            } else {
                throw new RuntimeException("Task not found with id: " + taskId);
            }
        } catch (Exception e) {
            System.err.println("Error updating task status: " + e.getMessage());
            throw new RuntimeException("Failed to update task status", e);
        }
    }

    // ✅ 페이지네이션 (수정됨)
    public Map<String, Object> getTasksByCreatedByAndTypeWithPagination(Long createdBy, TaskType taskType, int page, int size, String sortBy, String sortDir) {
        try {
            Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);

            Page<Task> taskPage = taskRepository.findByCreatedByAndTaskType(createdBy, taskType, pageable);

            Map<String, Object> response = new HashMap<>();
            response.put("content", taskPage.getContent());
            response.put("totalElements", taskPage.getTotalElements());
            response.put("totalPages", taskPage.getTotalPages());
            response.put("currentPage", page);
            response.put("size", size);

            return response;
        } catch (Exception e) {
            System.err.println("Error fetching tasks by createdBy and taskType with pagination: " + e.getMessage());
            throw new RuntimeException("Failed to fetch tasks with pagination", e);
        }
    }

    // 🆕 ===== TaskAssignment 관련 메서드들 =====

    // 작업에 사용자 할당
    @Transactional
    public TaskAssignment assignUserToTask(Integer taskId, Long userId, AssignmentRole role, Long assignedBy) {
        try {
            Optional<Task> taskOpt = taskRepository.findById(taskId);
            Optional<User> userOpt = userRepository.findById(userId);

            if (taskOpt.isEmpty()) {
                throw new RuntimeException("Task not found with id: " + taskId);
            }
            if (userOpt.isEmpty()) {
                throw new RuntimeException("User not found with id: " + userId);
            }

            // 이미 할당되어 있는지 확인
            if (taskAssignmentRepository.existsByTaskTaskIdAndUserUserIdAndRole(taskId, userId, role)) {
                throw new RuntimeException("User is already assigned to this task with the same role");
            }

            TaskAssignment assignment = new TaskAssignment();
            assignment.setTask(taskOpt.get());
            assignment.setUser(userOpt.get());
            assignment.setRole(role);
            assignment.setStatus(AssignmentStatus.ACTIVE);
            assignment.setAssignedBy(assignedBy);
            assignment.setAssignedAt(LocalDateTime.now());

            TaskAssignment savedAssignment = taskAssignmentRepository.save(assignment);
            System.out.println("User assigned to task successfully: " + userId + " -> " + taskId);
            return savedAssignment;
        } catch (Exception e) {
            System.err.println("Error assigning user to task: " + e.getMessage());
            throw new RuntimeException("Failed to assign user to task", e);
        }
    }

    // 작업에서 사용자 할당 해제
    @Transactional
    public boolean unassignUserFromTask(Integer taskId, Long userId) {
        try {
            int updatedCount = taskAssignmentRepository.cancelAssignment(taskId, userId);
            if (updatedCount > 0) {
                System.out.println("User unassigned from task successfully: " + userId + " -> " + taskId);
                return true;
            } else {
                System.out.println("No active assignment found for user: " + userId + " and task: " + taskId);
                return false;
            }
        } catch (Exception e) {
            System.err.println("Error unassigning user from task: " + e.getMessage());
            throw new RuntimeException("Failed to unassign user from task", e);
        }
    }

    // 내가 담당자로 할당받은 작업들
    public List<Task> getMyAssignedTasks(Long userId) {
        try {
            return taskRepository.findMyAssignedTasks(userId);
        } catch (Exception e) {
            System.err.println("Error fetching my assigned tasks: " + e.getMessage());
            throw new RuntimeException("Failed to fetch assigned tasks", e);
        }
    }

    // 내가 검토자로 할당받은 작업들
    public List<Task> getMyReviewTasks(Long userId) {
        try {
            return taskRepository.findMyReviewTasks(userId);
        } catch (Exception e) {
            System.err.println("Error fetching my review tasks: " + e.getMessage());
            throw new RuntimeException("Failed to fetch review tasks", e);
        }
    }

    // 특정 작업의 할당자들 조회
    public List<TaskAssignment> getTaskAssignments(Integer taskId) {
        try {
            return taskAssignmentRepository.findByTaskTaskIdOrderByAssignedAtAsc(taskId);
        } catch (Exception e) {
            System.err.println("Error fetching task assignments: " + e.getMessage());
            throw new RuntimeException("Failed to fetch task assignments", e);
        }
    }

    // 🆕 마감일 임박 작업들
    public List<Task> getTasksWithUpcomingDeadline(int days) {
        try {
            LocalDate deadlineDate = LocalDate.now().plusDays(days);
            return taskRepository.findTasksWithUpcomingDeadline(deadlineDate);
        } catch (Exception e) {
            System.err.println("Error fetching tasks with upcoming deadline: " + e.getMessage());
            throw new RuntimeException("Failed to fetch tasks with upcoming deadline", e);
        }
    }

    // 🆕 오늘 마감 작업들
    public List<Task> getTasksDueToday() {
        try {
            return taskRepository.findTasksDueToday(LocalDate.now());
        } catch (Exception e) {
            System.err.println("Error fetching tasks due today: " + e.getMessage());
            throw new RuntimeException("Failed to fetch tasks due today", e);
        }
    }

    // 🆕 연체된 작업들
    public List<Task> getOverdueTasks() {
        try {
            return taskRepository.findOverdueTasks(LocalDate.now());
        } catch (Exception e) {
            System.err.println("Error fetching overdue tasks: " + e.getMessage());
            throw new RuntimeException("Failed to fetch overdue tasks", e);
        }
    }

    // 🆕 작업 통계
    public Map<String, Object> getTaskStatistics(Long userId) {
        try {
            Map<String, Object> stats = new HashMap<>();

            // 전체 활성 작업 수
            stats.put("totalActiveTasks", taskRepository.countActiveTasks());

            // 사용자별 할당 작업 수
            if (userId != null) {
                stats.put("myAssignedTasks", taskRepository.countActiveAssignedTasks(userId));
                stats.put("myCreatedTasks", taskRepository.findByCreatedByOrderByCreatedAtDesc(userId).size());
            }

            // 타입별 통계
            List<Object[]> typeStats = taskRepository.countTasksByType();
            Map<String, Long> typeStatsMap = new HashMap<>();
            for (Object[] stat : typeStats) {
                typeStatsMap.put(stat[0].toString(), (Long) stat[1]);
            }
            stats.put("tasksByType", typeStatsMap);

            return stats;
        } catch (Exception e) {
            System.err.println("Error fetching task statistics: " + e.getMessage());
            throw new RuntimeException("Failed to fetch task statistics", e);
        }
    }
}
