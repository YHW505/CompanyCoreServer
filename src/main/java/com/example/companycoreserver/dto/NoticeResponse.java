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
    private Boolean hasAttachments; // ✨ 동적으로 계산됨
    private String attachmentFilename;
    private String attachmentContentType;
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

        // ✅ 동적으로 첨부파일 여부 판단
        this.hasAttachments = notice.hasAttachment();

        // 첨부파일 정보 설정
        if (notice.hasAttachment()) {
            this.attachmentFilename = notice.getAttachmentFilename();
            this.attachmentContentType = notice.getAttachmentContentType();
            
            // 🆕 첨부파일 내용을 Base64로 인코딩하여 설정
            if (notice.getAttachmentFile() != null && notice.getAttachmentFile().length > 0) {
                this.attachmentContent = Base64.getEncoder().encodeToString(notice.getAttachmentFile());
            }
        }

        this.createdAt = notice.getCreatedAt();
        this.updatedAt = notice.getUpdatedAt();
    }

    // 모든 필드를 받는 생성자
    public NoticeResponse(Long id, String title, String content, Long authorId,
                          String authorName, String authorDepartment, Boolean hasAttachments,
                          String attachmentFilename, String attachmentContentType,
                          String attachmentContent, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.authorId = authorId;
        this.authorName = authorName;
        this.authorDepartment = authorDepartment;
        this.hasAttachments = hasAttachments;
        this.attachmentFilename = attachmentFilename;
        this.attachmentContentType = attachmentContentType;
        this.attachmentContent = attachmentContent;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public NoticeResponse() {}

    // 모든 getter/setter들 (변경 없음)
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

    public String getAttachmentFilename() { return attachmentFilename; }
    public void setAttachmentFilename(String attachmentFilename) { this.attachmentFilename = attachmentFilename; }

    public String getAttachmentContentType() { return attachmentContentType; }
    public void setAttachmentContentType(String attachmentContentType) { this.attachmentContentType = attachmentContentType; }
    
    public String getAttachmentContent() { return attachmentContent; }
    public void setAttachmentContent(String attachmentContent) { this.attachmentContent = attachmentContent; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
