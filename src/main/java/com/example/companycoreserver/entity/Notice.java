package com.example.companycoreserver.entity;

import jakarta.persistence.*;
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

    @Column(nullable = false)
    private Long authorId;

    @Column(nullable = false, length = 50)
    private String authorName;

    @Column(nullable = false, length = 50)
    private String authorDepartment;

    @Column(name = "has_attachment")
    private boolean hasAttachment = false;

    @Column(length = 255)
    private String attachmentFilename;

    @Column(length = 100)
    private String attachmentContentType;

    @Column
    private Long attachmentSize;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String attachmentContent;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public Notice() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Notice(String title, String content, Long authorId, String authorName, String authorDepartment) {
        this();
        this.title = title;
        this.content = content;
        this.authorId = authorId;
        this.authorName = authorName;
        this.authorDepartment = authorDepartment;
    }

    public void updateNotice(String title, String content) {
        this.title = title;
        this.content = content;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateAttachment(String filename, String contentType, String base64Content) {
        this.attachmentFilename = filename;
        this.attachmentContentType = contentType;
        this.attachmentContent = base64Content;
        this.updatedAt = LocalDateTime.now();
    }

    public void removeAttachment() {
        this.hasAttachment = false;
        this.attachmentFilename = null;
        this.attachmentContentType = null;
        this.attachmentContent = null;
        this.attachmentSize = null;
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getAuthorDepartment() {
        return authorDepartment;
    }

    public void setAuthorDepartment(String authorDepartment) {
        this.authorDepartment = authorDepartment;
    }

    public boolean hasAttachment() {
        return hasAttachment;
    }

    public void setHasAttachment(boolean hasAttachment) {
        this.hasAttachment = hasAttachment;
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

    @Override
    public String toString() {
        return "Notice{"
                + "id=" + id
                + ", title='" + title + "'"
                + ", content='" + content + "'"
                + ", authorId=" + authorId
                + ", authorName='" + authorName + "'"
                + ", authorDepartment='" + authorDepartment + "'"
                + ", hasAttachment=" + hasAttachment
                + ", attachmentFilename='" + attachmentFilename + "'"
                + ", attachmentContentType='" + attachmentContentType + "'"
                + ", attachmentSize=" + attachmentSize
                + ", createdAt=" + createdAt
                + ", updatedAt=" + updatedAt
                + "}";
    }
}