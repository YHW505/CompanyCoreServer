package com.example.companycoreserver.dto;

import com.example.companycoreserver.entity.Notice;

public class NoticeRequest {

    private String title;
    private String content;
    private Long authorId;
    private String authorName;
    private String authorDepartment;

    // 🆕 첨부파일 메타데이터만 (바이너리 데이터 제외)
    private String attachmentFilename;
    private String attachmentContentType;
    
    // 🆕 첨부파일 내용 (Base64 인코딩된 문자열)
    private String attachmentContent;

    // 기본 생성자
    public NoticeRequest() {}

    // ✅ 기본 생성자 (필수 필드만)
    public NoticeRequest(String title, String content, Long authorId,
                         String authorName, String authorDepartment) {
        this.title = title;
        this.content = content;
        this.authorId = authorId;
        this.authorName = authorName;
        this.authorDepartment = authorDepartment;
    }

    // 🆕 첨부파일 메타데이터 포함 생성자
    public NoticeRequest(String title, String content, Long authorId,
                         String authorName, String authorDepartment,
                         String attachmentFilename, String attachmentContentType) {
        this.title = title;
        this.content = content;
        this.authorId = authorId;
        this.authorName = authorName;
        this.authorDepartment = authorDepartment;
        this.attachmentFilename = attachmentFilename;
        this.attachmentContentType = attachmentContentType;
    }

    // ✅ 기존 Getter 메서드들
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

    // 🆕 첨부파일 메타데이터 Getter (바이너리 제외)
    public String getAttachmentFilename() {
        return attachmentFilename;
    }

    public String getAttachmentContentType() {
        return attachmentContentType;
    }
    
    // 🆕 첨부파일 내용 Getter
    public String getAttachmentContent() {
        return attachmentContent;
    }

    // ✅ 기존 Setter 메서드들
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

    // 🆕 첨부파일 메타데이터 Setter (바이너리 제외)
    public void setAttachmentFilename(String attachmentFilename) {
        this.attachmentFilename = attachmentFilename;
    }

    public void setAttachmentContentType(String attachmentContentType) {
        this.attachmentContentType = attachmentContentType;
    }
    
    // 🆕 첨부파일 내용 Setter
    public void setAttachmentContent(String attachmentContent) {
        this.attachmentContent = attachmentContent;
    }

    // ✅ Entity 변환 메서드 (바이너리 데이터 없이)
    public Notice toEntity() {
        Notice notice = new Notice();
        notice.setTitle(this.title);
        notice.setContent(this.content);
        notice.setAuthorId(this.authorId);
        notice.setAuthorName(this.authorName);
        notice.setAuthorDepartment(this.authorDepartment);

        // 🆕 첨부파일 메타데이터만 설정 (바이너리는 별도 처리)
        if (this.attachmentFilename != null && !this.attachmentFilename.trim().isEmpty()) {
            notice.setAttachmentFilename(this.attachmentFilename);
            notice.setAttachmentContentType(this.attachmentContentType);
        }

        return notice;
    }

    // ✅ toString 메서드
    @Override
    public String toString() {
        return "NoticeRequest{" +
                "title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", authorId=" + authorId +
                ", authorName='" + authorName + '\'' +
                ", authorDepartment='" + authorDepartment + '\'' +
                ", attachmentFilename='" + attachmentFilename + '\'' +
                ", attachmentContentType='" + attachmentContentType + '\'' +
                ", attachmentContent='" + (attachmentContent != null ? attachmentContent.substring(0, Math.min(50, attachmentContent.length())) + "..." : "null") + '\'' +
                '}';
    }
}
