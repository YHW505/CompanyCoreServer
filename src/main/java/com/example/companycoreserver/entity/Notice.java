package com.example.companycoreserver.entity;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "notice")
public class Notice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "author_id", nullable = false)
    private Long authorId;

    @Column(name = "author_name", nullable = false, length = 100)
    private String authorName;

    @Column(name = "author_department", length = 100)
    private String authorDepartment;

    @Column(name = "has_attachments")
    private Boolean hasAttachments = false;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // 기본 생성자
    public Notice() {}

    // 모든 필드 생성자
    public Notice(String title, String content, Long authorId,
                  String authorName, String authorDepartment, Boolean hasAttachments) {
        this.title = title;
        this.content = content;
        this.authorId = authorId;
        this.authorName = authorName;
        this.authorDepartment = authorDepartment;
        this.hasAttachments = hasAttachments != null ? hasAttachments : false;
    }

    // Getter 메서드들
    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public String getAuthorDepartment() {
        return authorDepartment;
    }

    public Boolean getHasAttachments() {
        return hasAttachments;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    // Setter 메서드들
    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public void setAuthorDepartment(String authorDepartment) {
        this.authorDepartment = authorDepartment;
    }

    public void setHasAttachments(Boolean hasAttachments) {
        this.hasAttachments = hasAttachments;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // 업데이트 메서드 (비즈니스 로직)
    public void updateNotice(String title, String content, Boolean hasAttachments) {
        this.title = title;
        this.content = content;
        this.hasAttachments = hasAttachments != null ? hasAttachments : false;
    }

    // toString 메서드 (디버깅용)
    @Override
    public String toString() {
        return "Notice{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", authorId=" + authorId +
                ", authorName='" + authorName + '\'' +
                ", authorDepartment='" + authorDepartment + '\'' +
                ", hasAttachments=" + hasAttachments +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

    // equals & hashCode (ID 기준)
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Notice notice = (Notice) obj;
        return id != null && id.equals(notice.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
