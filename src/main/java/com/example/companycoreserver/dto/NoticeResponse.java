package com.example.companycoreserver.dto;

import com.example.companycoreserver.entity.Notice;
import java.time.LocalDateTime;

public class NoticeResponse {
    private Long id;
    private String title;
    private String content;
    private Long authorId;
    private String authorName;
    private String authorDepartment;
    private Boolean hasAttachments;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // ✅ 기존 생성자 (Notice 엔티티를 받는)
    public NoticeResponse(Notice notice) {
        this.id = notice.getId();
        this.title = notice.getTitle();
        this.content = notice.getContent();
        this.authorId = notice.getAuthorId();
        this.authorName = notice.getAuthorName();
        this.authorDepartment = notice.getAuthorDepartment();
        this.hasAttachments = notice.getHasAttachments();
        this.createdAt = notice.getCreatedAt();
        this.updatedAt = notice.getUpdatedAt();
    }

    // ✅ 새로 추가할 생성자 (모든 필드를 받는)
    public NoticeResponse(Long id, String title, String content, Long authorId,
                          String authorName, String authorDepartment, Boolean hasAttachments,
                          LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.authorId = authorId;
        this.authorName = authorName;
        this.authorDepartment = authorDepartment;
        this.hasAttachments = hasAttachments;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // ✅ 기본 생성자
    public NoticeResponse() {}

    // Getter, Setter 메서드들...
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Long getAuthorId() { return authorId; }
    public void setAuthorId(Long authorId) { this.authorId = authorId; }

    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }

    public String getAuthorDepartment() { return authorDepartment; }
    public void setAuthorDepartment(String authorDepartment) { this.authorDepartment = authorDepartment; }

    public Boolean getHasAttachments() { return hasAttachments; }
    public void setHasAttachments(Boolean hasAttachments) { this.hasAttachments = hasAttachments; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
