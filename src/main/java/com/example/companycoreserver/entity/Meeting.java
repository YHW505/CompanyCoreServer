package com.example.companycoreserver.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "meetings")
public class Meeting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long meetingId;

    @Column(nullable = false, length = 200)
    private String title;                    // 회의 제목

    @Column(columnDefinition = "TEXT")
    private String description;              // 회의 설명/안건

    @Column(nullable = false)
    private LocalDateTime startTime;         // 회의 시작 시간

    @Column(nullable = false)
    private LocalDateTime endTime;           // 회의 종료 시간

    @Column(length = 200)
    private String location;                 // 회의 장소 (회의실명)

    @Column
    private String attachmentPath;           // 첨부파일 경로

    @Column(nullable = false)
    private LocalDateTime createdAt;         // 생성일시

    @Column
    private LocalDateTime updatedAt;         // 수정일시

    // 생성자
    public Meeting() {
        this.createdAt = LocalDateTime.now();
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

    public String getAttachmentPath() {
        return attachmentPath;
    }

    public void setAttachmentPath(String attachmentPath) {
        this.attachmentPath = attachmentPath;
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

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
