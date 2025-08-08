package com.example.companycoreserver.entity;

import com.example.companycoreserver.entity.Enum.TaskStatus;
import com.example.companycoreserver.entity.Enum.TaskType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

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

    // ✅ TaskStatus enum 사용
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 50)
    private TaskStatus status = TaskStatus.TODO; // 기본값: TODO

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // 첨부파일 관련 필드들 (테이블 구조와 일치)
    @Column(name = "attachment_filename", length = 255)
    private String attachmentFilename;

    @Column(name = "attachment_size")
    private Long attachmentSize;

    @Column(name = "attachment_content_type", length = 100)
    private String attachmentContentType;

    @Lob
    @Column(name = "attachment_content", columnDefinition = "LONGTEXT")
    private String attachmentContent; // Base64 인코딩된 첨부파일 내용

    // ✅ 테이블에 있는 assigned_by, assigned_to 컬럼 매핑
    @Column(name = "assigned_by")
    private Long assignedBy;

    @Column(name = "assigned_to")
    private Long assignedTo;

    // ✅ TaskType enum 사용
    @Enumerated(EnumType.STRING)
    @Column(name = "task_type", length = 50)
    private TaskType taskType;

    // 🔗 관계 매핑 - 업무 생성자와의 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_by", insertable = false, updatable = false)
    @JsonIgnoreProperties({"assignedTasks", "createdTasks", "attendances", "schedules"})
    private User assignedByUser;

    // 🔗 관계 매핑 - 업무 담당자와의 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_to", insertable = false, updatable = false)
    @JsonIgnoreProperties({"assignedTasks", "createdTasks", "attendances", "schedules"})
    private User assignedToUser;

    // 기본 생성자
    public Task() {}

    // 생성자
    public Task(Long assignedBy, Long assignedTo, TaskType taskType, String title,
                String description, TaskStatus status) {
        this.assignedBy = assignedBy;
        this.assignedTo = assignedTo;
        this.taskType = taskType;
        this.title = title;
        this.description = description;
        this.status = status != null ? status : TaskStatus.TODO;
    }

    // ✅ 첨부파일 업데이트 메서드
    public void updateAttachment(String filename, String contentType, String base64Content, Long size) {
        this.attachmentFilename = filename;
        this.attachmentContentType = contentType;
        this.attachmentContent = base64Content;
        this.attachmentSize = size;
        this.updatedAt = LocalDateTime.now();
    }

    // ✅ 첨부파일 제거 메서드
    public void removeAttachment() {
        this.attachmentFilename = null;
        this.attachmentContentType = null;
        this.attachmentSize = null;
        this.attachmentContent = null;
        this.updatedAt = LocalDateTime.now();
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

    // ✅ TaskStatus enum getter/setter
    public TaskStatus getStatus() { return status; }
    public void setStatus(TaskStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public String getAttachmentFilename() { return attachmentFilename; }
    public void setAttachmentFilename(String attachmentFilename) { this.attachmentFilename = attachmentFilename; }

    public Long getAttachmentSize() { return attachmentSize; }
    public void setAttachmentSize(Long attachmentSize) { this.attachmentSize = attachmentSize; }

    public String getAttachmentContentType() { return attachmentContentType; }
    public void setAttachmentContentType(String attachmentContentType) { this.attachmentContentType = attachmentContentType; }

    public String getAttachmentContent() { return attachmentContent; }
    public void setAttachmentContent(String attachmentContent) { this.attachmentContent = attachmentContent; }

    public Long getAssignedBy() { return assignedBy; }
    public void setAssignedBy(Long assignedBy) { this.assignedBy = assignedBy; }

    public Long getAssignedTo() { return assignedTo; }
    public void setAssignedTo(Long assignedTo) { this.assignedTo = assignedTo; }

    // ✅ TaskType enum getter/setter
    public TaskType getTaskType() { return taskType; }
    public void setTaskType(TaskType taskType) { this.taskType = taskType; }

    public User getAssignedByUser() { return assignedByUser; }
    public void setAssignedByUser(User assignedByUser) { this.assignedByUser = assignedByUser; }

    public User getAssignedToUser() { return assignedToUser; }
    public void setAssignedToUser(User assignedToUser) { this.assignedToUser = assignedToUser; }

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
                ", assignedTo=" + assignedTo +
                ", taskType=" + taskType +
                ", status=" + status +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }
}
