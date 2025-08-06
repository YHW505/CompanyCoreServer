package com.example.companycoreserver.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "meetings")
public class Meeting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "meeting_id")
    private Long meetingId;

    @Column(nullable = false, length = 200)
    private String title;                    // 회의 제목

    @Column(columnDefinition = "TEXT")
    private String description;              // 회의 설명/안건

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;         // 회의 시작 시간

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;           // 회의 종료 시간

    @Column(length = 200)
    private String location;                 // 회의 장소 (회의실명)

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;         // 생성일시

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;         // 수정일시

    // 첨부파일 관련 필드들
    @Column(name = "attachment_filename", length = 255)
    private String attachmentFilename;

    @Column(name = "attachment_content_type", length = 100)
    private String attachmentContentType;

    @Column(name = "attachment_size")
    private Long attachmentSize;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String attachmentContent; // Base64 인코딩된 첨부파일 내용

    @Column(length = 255)
    private String author;                   // 작성자

    @Column(length = 255)
    private String department;               // 부서

    // 생성자
    public Meeting() {
        this.createdAt = LocalDateTime.now();
    }

    // 첨부파일 관련 메서드들
    public void updateAttachment(String filename, String contentType, String base64Content) {
        this.attachmentFilename = filename;
        this.attachmentContentType = contentType;
        this.attachmentContent = base64Content;
        this.updatedAt = LocalDateTime.now();
    }

    public void removeAttachment() {
        this.attachmentFilename = null;
        this.attachmentContentType = null;
        this.attachmentSize = null;
        this.attachmentContent = null;
        this.updatedAt = LocalDateTime.now();
    }

    // Getter & Setter
    public Long getMeetingId() {
        return meetingId;
    }

    public void setMeetingId(Long meetingId) {
        this.meetingId = meetingId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

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

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "Meeting{" +
                "meetingId=" + meetingId +
                ", title='" + title + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", location='" + location + '\'' +
                ", author='" + author + '\'' +
                ", department='" + department + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
