package com.example.companycoreserver.dto;

import com.example.companycoreserver.entity.Notice;
import java.time.LocalDateTime;
import java.util.Base64;

public class NoticeResponse {
    private Long id;
    private String title;
    private String content;
    private Long authorId;
    private String authorName;
    private String authorDepartment;
    private boolean hasAttachment;
    private String attachmentFilename;
    private String attachmentContentType;
    private Long attachmentSize; // 🆕 파일 크기 (바이트)
    private String attachmentContent; // 🆕 첨부파일 내용 (Base64 인코딩)
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Entity로부터 생성하는 생성자
    public NoticeResponse(Notice notice) {
        this.id = notice.getId();
        this.title = notice.getTitle();
        this.content = notice.getContent();
        this.authorId = notice.getAuthorId();
        this.authorName = notice.getAuthorName();
        this.authorDepartment = notice.getAuthorDepartment();
        this.hasAttachment = notice.getHasAttachment();

        if (notice.getHasAttachment()) {
            this.attachmentFilename = notice.getAttachmentFilename();
            this.attachmentContentType = notice.getAttachmentContentType();
            this.attachmentSize = notice.getAttachmentSize();
            this.attachmentContent = notice.getAttachmentContent();
        }

        this.createdAt = notice.getCreatedAt();
        this.updatedAt = notice.getUpdatedAt();
    }

    // 모든 필드를 받는 생성자
    public NoticeResponse(Long id, String title, String content, Long authorId,
                          String authorName, String authorDepartment, boolean hasAttachment,
                          String attachmentFilename, String attachmentContentType, Long attachmentSize,
                          String attachmentContent, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.authorId = authorId;
        this.authorName = authorName;
        this.authorDepartment = authorDepartment;
        this.hasAttachment = hasAttachment;
        this.attachmentFilename = attachmentFilename;
        this.attachmentContentType = attachmentContentType;
        this.attachmentSize = attachmentSize;
        this.attachmentContent = attachmentContent;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public NoticeResponse() {}

    // 모든 getter/setter들
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

    public boolean gethasAttachment() { return hasAttachment; }
    public void setHasAttachment(boolean hasAttachment) { this.hasAttachment = hasAttachment; }

    public String getAttachmentFilename() { return attachmentFilename; }
    public void setAttachmentFilename(String attachmentFilename) { this.attachmentFilename = attachmentFilename; }

    public String getAttachmentContentType() { return attachmentContentType; }
    public void setAttachmentContentType(String attachmentContentType) { this.attachmentContentType = attachmentContentType; }
    
    public Long getAttachmentSize() { return attachmentSize; }
    public void setAttachmentSize(Long attachmentSize) { this.attachmentSize = attachmentSize; }
    
    public String getAttachmentContent() { return attachmentContent; }
    public void setAttachmentContent(String attachmentContent) { this.attachmentContent = attachmentContent; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
