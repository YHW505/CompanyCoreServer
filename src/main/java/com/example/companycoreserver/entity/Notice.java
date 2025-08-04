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

    // 첨부파일 관련 필드들
    @Column(length = 255)
    private String attachmentFilename;

    @Column(length = 100)
    private String attachmentContentType;

    @Column
    private Long attachmentSize;

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] attachmentFile;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // 기본 생성자
    public Notice() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // 생성자
    public Notice(String title, String content, Long authorId, String authorName, String authorDepartment) {
        this();
        this.title = title;
        this.content = content;
        this.authorId = authorId;
        this.authorName = authorName;
        this.authorDepartment = authorDepartment;
    }

    // ✅ 기본 정보 업데이트
    public void updateNotice(String title, String content) {
        this.title = title;
        this.content = content;
        this.updatedAt = LocalDateTime.now();
    }

    // 🆕 첨부파일 메타데이터만 업데이트 (size 없이)
    public void updateAttachment(String filename, String contentType, byte[] fileData) {
        this.attachmentFilename = filename;
        this.attachmentContentType = contentType;
        this.attachmentFile = fileData;
        this.updatedAt = LocalDateTime.now();
    }

    // ✅ 첨부파일 여부 확인
    public boolean hasAttachment() {
        return this.attachmentFilename != null && !this.attachmentFilename.trim().isEmpty();
    }

    // ✅ 첨부파일 제거
    public void removeAttachment() {
        this.attachmentFilename = null;
        this.attachmentContentType = null;
        this.attachmentSize = null;
        this.attachmentFile = null;
        this.updatedAt = LocalDateTime.now();
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

    public String getAttachmentFilename() {
        return attachmentFilename;
    }

    public String getAttachmentContentType() {
        return attachmentContentType;
    }

    public Long getAttachmentSize() {
        return attachmentSize;
    }

    public byte[] getAttachmentFile() {
        return attachmentFile;
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

    public void setAttachmentFilename(String attachmentFilename) {
        this.attachmentFilename = attachmentFilename;
    }

    public void setAttachmentContentType(String attachmentContentType) {
        this.attachmentContentType = attachmentContentType;
    }

    public void setAttachmentSize(Long attachmentSize) {
        this.attachmentSize = attachmentSize;
    }

    public void setAttachmentFile(byte[] attachmentFile) {
        this.attachmentFile = attachmentFile;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "Notice{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", authorId=" + authorId +
                ", authorName='" + authorName + '\'' +
                ", authorDepartment='" + authorDepartment + '\'' +
                ", attachmentFilename='" + attachmentFilename + '\'' +
                ", attachmentContentType='" + attachmentContentType + '\'' +
                ", attachmentSize=" + attachmentSize +
                ", hasAttachmentFile=" + (attachmentFile != null) +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
