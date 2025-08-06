package com.example.companycoreserver.entity;

import com.example.companycoreserver.entity.Enum.AssignmentRole;
import com.example.companycoreserver.entity.Enum.AssignmentStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "task_assignment")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class TaskAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "assignment_id")
    private Long assignmentId;

    // 🔗 Task와의 관계 (다대일) - Task의 taskId가 Integer이므로 맞춤
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    @JsonIgnoreProperties({"assignments", "createdByUser"})
    private Task task;

    // 🔗 User와의 관계 (다대일)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties({"assignedTasks", "createdTasks", "attendances", "schedules"})
    private User user;

    // ✅ AssignmentRole enum 사용 (ASSIGNEE, REVIEWER, OBSERVER)
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private AssignmentRole role; // ASSIGNEE(담당자), REVIEWER(검토자), OBSERVER(참관자)

    @Column(name = "assigned_at")
    private LocalDateTime assignedAt;

    @Column(name = "assigned_by")
    private Long assignedBy;

    // 🔗 할당한 사용자와의 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_by", insertable = false, updatable = false)
    @JsonIgnoreProperties({"assignedTasks", "createdTasks", "attendances", "schedules"})
    private User assignedByUser;

    // ✅ AssignmentStatus enum 사용 (ACTIVE, COMPLETED, CANCELLED)
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private AssignmentStatus status = AssignmentStatus.ACTIVE; // 기본값: ACTIVE(활성)

    // 🆕 업데이트 시간 추가
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // 🆕 완료 시간 추가 (상태가 COMPLETED로 변경된 시간)
    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    // 생성자 (필수 필드만)
    public TaskAssignment(Task task, User user, AssignmentRole role, Long assignedBy) {
        this.task = task;
        this.user = user;
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

    // ✅ 할당이 활성 상태인지 확인
    public boolean isActive() {
        return this.status == AssignmentStatus.ACTIVE;
    }

    // ✅ 할당이 완료 상태인지 확인
    public boolean isCompleted() {
        return this.status == AssignmentStatus.COMPLETED;
    }

    // ✅ 담당자인지 확인
    public boolean isAssignee() {
        return this.role == AssignmentRole.ASSIGNEE;
    }

    // ✅ 검토자인지 확인
    public boolean isReviewer() {
        return this.role == AssignmentRole.REVIEWER;
    }

    // ✅ 참관자인지 확인
    public boolean isObserver() {
        return this.role == AssignmentRole.OBSERVER;
    }

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
                ", taskId=" + (task != null ? task.getTaskId() : null) +
                ", userId=" + (user != null ? user.getUserId() : null) +
                ", role=" + role +
                ", status=" + status +
                ", assignedAt=" + assignedAt +
                '}';
    }
}
