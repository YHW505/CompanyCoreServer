package com.example.companycoreserver.entity;

import com.example.companycoreserver.entity.Enum.TaskStatus;
import com.example.companycoreserver.entity.Enum.TaskType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tasks")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "task_id")
    private Integer taskId;

    // 🔄 기존 assignedBy는 createdBy로 변경 (업무 생성자)
    @Column(name = "created_by")
    private Long createdBy;

    // 🔄 기존 assignedTo는 제거 (TaskAssignment 테이블로 분리)
    // @Column(name = "assigned_to")
    // private Long assignedTo;

    // ✅ TaskType enum 사용 (TASK, REPORT)
    @Enumerated(EnumType.STRING)
    @Column(name = "task_type")
    private TaskType taskType;

    @Column(name = "title", length = 255)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    // 첨부파일 관련 필드들
    @Column(length = 255)
    private String attachmentFilename;

    @Column(length = 100)
    private String attachmentContentType;

    @Column
    private Long attachmentSize;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String attachmentContent; // Base64 인코딩된 첨부파일 내용

    // ✅ TaskStatus enum 사용 (TODO, IN_PROGRESS, DONE, CANCELLED)
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private TaskStatus status = TaskStatus.TODO; // 기본값: TODO

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    // 🆕 업데이트 시간 추가
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // 🔗 관계 매핑 - 업무 생성자와의 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", insertable = false, updatable = false)
    @JsonIgnoreProperties({"assignedTasks", "createdTasks", "attendances", "schedules"})
    private User createdByUser;

    // 🔄 기존 assignedToUser 제거 (TaskAssignment로 분리)
    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "assigned_to", insertable = false, updatable = false)
    // @JsonIgnoreProperties({"assignedTasks", "createdTasks", "attendances", "schedules"})
    // private User assignedToUser;

    // 🆕 TaskAssignment와의 일대다 관계 추가
    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"task"})
    private List<TaskAssignment> assignments = new ArrayList<>();

    // 기본 생성자
    public Task() {}

    // 🔄 생성자 수정 (assignedTo 제거, createdBy로 변경)
    public Task(Long createdBy, TaskType taskType, String title,
                String description, TaskStatus status) {
        this.createdBy = createdBy;
        this.taskType = taskType;
        this.title = title;
        this.description = description;
        this.status = status != null ? status : TaskStatus.TODO;
    }

    // ✅ 첨부파일 메타데이터만 업데이트 (Base64 문자열 방식)
    public void updateAttachment(String filename, String contentType, String base64Content) {
        this.attachmentFilename = filename;
        this.attachmentContentType = contentType;
        this.attachmentContent = base64Content;
        this.updatedAt = LocalDateTime.now();
    }

    // ✅ 첨부파일 제거
    public void removeAttachment() {
        this.attachmentFilename = null;
        this.attachmentContentType = null;
        this.attachmentSize = null;
        this.attachmentContent = null;
        this.updatedAt = LocalDateTime.now();
    }

    // Getter/Setter
    public Integer getTaskId() { return taskId; }
    public void setTaskId(Integer taskId) { this.taskId = taskId; }

    // 🔄 assignedBy → createdBy로 변경
    public Long getCreatedBy() { return createdBy; }
    public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }

    // 🔄 assignedTo 관련 메서드 제거
    // public Long getAssignedTo() { return assignedTo; }
    // public void setAssignedTo(Long assignedTo) { this.assignedTo = assignedTo; }

    public TaskType getTaskType() { return taskType; }
    public void setTaskType(TaskType taskType) { this.taskType = taskType; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getAttachmentFilename() {
        return attachmentFilename;
    }
    public void setAttachmentFilename(String attachmentFilename) {
        this.attachmentFilename = attachmentFilename;
    }

    public String getAttachmentContentType() {
        return attachmentContentType;
    }
    public void setAttachmentContentType(String attachmentContentType) {
        this.attachmentContentType = attachmentContentType;
    }

    public Long getAttachmentSize() {
        return attachmentSize;
    }
    public void setAttachmentSize(Long attachmentSize) {
        this.attachmentSize = attachmentSize;
    }

    public String getAttachmentContent() {
        return attachmentContent;
    }
    public void setAttachmentContent(String attachmentContent) {
        this.attachmentContent = attachmentContent;
    }

    public TaskStatus getStatus() { return status; }
    public void setStatus(TaskStatus status) { this.status = status; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    // 🆕 updatedAt getter/setter 추가
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // 🔄 assignedByUser → createdByUser로 변경
    public User getCreatedByUser() { return createdByUser; }
    public void setCreatedByUser(User createdByUser) { this.createdByUser = createdByUser; }

    // 🔄 assignedToUser 관련 메서드 제거
    // public User getAssignedToUser() { return assignedToUser; }
    // public void setAssignedToUser(User assignedToUser) { this.assignedToUser = assignedToUser; }

    // 🆕 assignments getter/setter 추가
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
                ", createdBy=" + createdBy +
                ", taskType=" + taskType +
                ", title='" + title + '\'' +
                ", status=" + status +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }
}
