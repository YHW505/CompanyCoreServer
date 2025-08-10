package com.example.companycoreserver.entity;

import com.example.companycoreserver.entity.Enum.AssignmentRole;
import com.example.companycoreserver.entity.Enum.AssignmentStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "task_assignment")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class TaskAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "assignment_id")
    private Long assignmentId;

    // ✅ 테이블 구조에 맞게 수정 - task_id를 직접 저장
    @Column(name = "task_id", nullable = false)
    private Long taskId;

    // ✅ 테이블 구조에 맞게 수정 - user_id를 직접 저장
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private AssignmentRole role;

    @Column(name = "assigned_at")
    private LocalDateTime assignedAt;

    @Column(name = "assigned_by")
    private Long assignedBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private AssignmentStatus status = AssignmentStatus.ACTIVE;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    // 🔗 관계 매핑 - Task와의 관계 (조회용)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", insertable = false, updatable = false)
    @JsonIgnoreProperties({"assignments"})
    private Task task;

    // 🔗 관계 매핑 - User와의 관계 (조회용)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    @JsonIgnoreProperties({"assignedTasks", "createdTasks", "attendances", "schedules"})
    private User user;

    // 🔗 할당한 사용자와의 관계 (조회용)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_by", insertable = false, updatable = false)
    @JsonIgnoreProperties({"assignedTasks", "createdTasks", "attendances", "schedules"})
    private User assignedByUser;

    // 기본 생성자
    public TaskAssignment() {}

    // 생성자 (필수 필드만)
    public TaskAssignment(Long taskId, Long userId, AssignmentRole role, Long assignedBy) {
        this.taskId = taskId;
        this.userId = userId;
        this.role = role;
        this.assignedBy = assignedBy;
        this.status = AssignmentStatus.ACTIVE;
    }

    // ✅ 할당 상태 변경 메서드
    public void updateStatus(AssignmentStatus newStatus) {
        this.status = newStatus;
        this.updatedAt = LocalDateTime.now();

        // 완료 상태로 변경될 때 완료 시간 기록
        if (newStatus == AssignmentStatus.COMPLETED) {
            this.completedAt = LocalDateTime.now();
        } else if (newStatus == AssignmentStatus.CANCELED) {
            this.completedAt = null; // 취소된 경우 완료 시간 제거
        }
    }

    // ✅ 편의 메서드들
    public boolean isActive() {
        return this.status == AssignmentStatus.ACTIVE;
    }

    public boolean isCompleted() {
        return this.status == AssignmentStatus.COMPLETED;
    }

    public boolean isAssignee() {
        return this.role == AssignmentRole.ASSIGNEE;
    }

    public boolean isReviewer() {
        return this.role == AssignmentRole.REVIEWER;
    }

    public boolean isObserver() {
        return this.role == AssignmentRole.OBSERVER;
    }

    // Getter/Setter
    public Long getAssignmentId() { return assignmentId; }
    public void setAssignmentId(Long assignmentId) { this.assignmentId = assignmentId; }

    public Long getTaskId() { return taskId; }
    public void setTaskId(Long taskId) { this.taskId = taskId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public AssignmentRole getRole() { return role; }
    public void setRole(AssignmentRole role) { this.role = role; }

    public LocalDateTime getAssignedAt() { return assignedAt; }
    public void setAssignedAt(LocalDateTime assignedAt) { this.assignedAt = assignedAt; }

    public Long getAssignedBy() { return assignedBy; }
    public void setAssignedBy(Long assignedBy) { this.assignedBy = assignedBy; }

    public AssignmentStatus getStatus() { return status; }
    public void setStatus(AssignmentStatus status) { this.status = status; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }

    public Task getTask() { return task; }
    public void setTask(Task task) { this.task = task; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public User getAssignedByUser() { return assignedByUser; }
    public void setAssignedByUser(User assignedByUser) { this.assignedByUser = assignedByUser; }

    @PrePersist
    protected void onCreate() {
        assignedAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = AssignmentStatus.ACTIVE;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "TaskAssignment{" +
                "assignmentId=" + assignmentId +
                ", taskId=" + taskId +
                ", userId=" + userId +
                ", role=" + role +
                ", status=" + status +
                ", assignedAt=" + assignedAt +
                '}';
    }
}
