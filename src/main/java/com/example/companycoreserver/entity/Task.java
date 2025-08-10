package com.example.companycoreserver.entity;

import com.example.companycoreserver.entity.Enum.AssignmentRole;
import com.example.companycoreserver.entity.Enum.AssignmentStatus;
import com.example.companycoreserver.entity.Enum.TaskStatus;
import com.example.companycoreserver.entity.Enum.TaskType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "tasks")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "task_id")
    private Long taskId;

    @Column(name = "title", length = 255)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 50)
    private TaskStatus status = TaskStatus.TODO;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ✅ 작업 할당자 (누가 이 작업을 할당했는지)
    @Column(name = "assigned_by")
    private Long assignedBy;

    // ❌ assigned_to 제거 - TaskAssignment로 관리
    // @Column(name = "assigned_to")
    // private Long assignedTo;

    @Enumerated(EnumType.STRING)
    @Column(name = "task_type", length = 50)
    private TaskType taskType;

    // ✅ 실제 작업 생성자
    @Column(name = "created_by")
    private Long createdBy;

    // 🔗 관계 매핑 - 작업 할당자와의 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_by", insertable = false, updatable = false)
    @JsonIgnoreProperties({"assignedTasks", "createdTasks", "attendances", "schedules"})
    private User assignedByUser;

    // ❌ assignedToUser 제거
    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "assigned_to", insertable = false, updatable = false)
    // @JsonIgnoreProperties({"assignedTasks", "createdTasks", "attendances", "schedules"})
    // private User assignedToUser;

    // 🔗 관계 매핑 - 실제 생성자와의 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", insertable = false, updatable = false)
    @JsonIgnoreProperties({"assignedTasks", "createdTasks", "attendances", "schedules"})
    private User createdByUser;

    // ✅ TaskAssignment와의 관계 (담당자들 조회용)
    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"task"})
    private List<TaskAssignment> assignments = new ArrayList<>();

    // 기본 생성자
    public Task() {}

    // ✅ 수정된 생성자 (assignedTo 제거)
    public Task(Long assignedBy, TaskType taskType, String title,
                String description, TaskStatus status, Long createdBy) {
        this.assignedBy = assignedBy;
        this.taskType = taskType;
        this.title = title;
        this.description = description;
        this.status = status != null ? status : TaskStatus.TODO;
        this.createdBy = createdBy;
    }

    // ✅ 상태 변경을 위한 편의 메서드들
    public void markAsInProgress() {
        this.status = TaskStatus.IN_PROGRESS;
        this.updatedAt = LocalDateTime.now();
    }

    public void markAsDone() {
        this.status = TaskStatus.DONE;
        this.updatedAt = LocalDateTime.now();
    }

    public void markAsCancelled() {
        this.status = TaskStatus.CANCELLED;
        this.updatedAt = LocalDateTime.now();
    }

    // ✅ 편의 메서드 - 활성 담당자들 조회
    public List<User> getActiveAssignees() {
        return assignments.stream()
                .filter(assignment -> assignment.getStatus() == AssignmentStatus.ACTIVE)
                .map(TaskAssignment::getUser)
                .collect(Collectors.toList());
    }

    // ✅ 편의 메서드 - 특정 역할의 담당자들 조회
    public List<User> getAssigneesByRole(AssignmentRole role) {
        return assignments.stream()
                .filter(assignment -> assignment.getStatus() == AssignmentStatus.ACTIVE)
                .filter(assignment -> assignment.getRole() == role)
                .map(TaskAssignment::getUser)
                .collect(Collectors.toList());
    }

    // ✅ 편의 메서드 - 메인 담당자들만 조회
    public List<User> getMainAssignees() {
        return getAssigneesByRole(AssignmentRole.ASSIGNEE);
    }

    // ✅ 편의 메서드 - 리뷰어들만 조회
    public List<User> getReviewers() {
        return getAssigneesByRole(AssignmentRole.REVIEWER);
    }

    // ✅ 편의 메서드 - 옵저버들만 조회
    public List<User> getObservers() {
        return getAssigneesByRole(AssignmentRole.OBSERVER);
    }

    // ✅ 편의 메서드 - 특정 사용자가 이 작업에 할당되어 있는지 확인
    public boolean isAssignedTo(Long userId) {
        return assignments.stream()
                .anyMatch(assignment ->
                        assignment.getUser().getUserId().equals(userId) &&
                                assignment.getStatus() == AssignmentStatus.ACTIVE);
    }

    // Getter/Setter
    public Long getTaskId() { return taskId; }
    public void setTaskId(Long taskId) { this.taskId = taskId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public TaskStatus getStatus() { return status; }
    public void setStatus(TaskStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public Long getAssignedBy() { return assignedBy; }
    public void setAssignedBy(Long assignedBy) { this.assignedBy = assignedBy; }

    // ❌ assignedTo 관련 메서드들 제거
    // public Long getAssignedTo() { return assignedTo; }
    // public void setAssignedTo(Long assignedTo) { this.assignedTo = assignedTo; }

    public TaskType getTaskType() { return taskType; }
    public void setTaskType(TaskType taskType) { this.taskType = taskType; }

    public Long getCreatedBy() { return createdBy; }
    public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }

    public User getAssignedByUser() { return assignedByUser; }
    public void setAssignedByUser(User assignedByUser) { this.assignedByUser = assignedByUser; }

    // ❌ assignedToUser 관련 메서드들 제거
    // public User getAssignedToUser() { return assignedToUser; }
    // public void setAssignedToUser(User assignedToUser) { this.assignedToUser = assignedToUser; }

    public User getCreatedByUser() { return createdByUser; }
    public void setCreatedByUser(User createdByUser) { this.createdByUser = createdByUser; }

    // ✅ assignments getter/setter 추가
    public List<TaskAssignment> getAssignments() { return assignments; }
    public void setAssignments(List<TaskAssignment> assignments) { this.assignments = assignments; }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = TaskStatus.TODO;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "Task{" +
                "taskId=" + taskId +
                ", title='" + title + '\'' +
                ", assignedBy=" + assignedBy +
                // ❌ assignedTo 제거
                ", taskType=" + taskType +
                ", status=" + status +
                ", createdBy=" + createdBy +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", assignmentsCount=" + (assignments != null ? assignments.size() : 0) +
                '}';
    }
}
