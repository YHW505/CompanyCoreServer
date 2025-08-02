package com.example.companycoreserver.dto;

import com.example.companycoreserver.entity.Notice;

public class NoticeRequest {


    private String title;
    private String content;
    private Long authorId;
    private String authorName;
    private String authorDepartment;

    private Boolean hasAttachments;

    // 기본 생성자
    public NoticeRequest() {}

    // 모든 필드 생성자
    public NoticeRequest(String title, String content, Long authorId,
                            String authorName, String authorDepartment, Boolean hasAttachments) {
        this.title = title;
        this.content = content;
        this.authorId = authorId;
        this.authorName = authorName;
        this.authorDepartment = authorDepartment;
        this.hasAttachments = hasAttachments;
    }

    // Getter 메서드들
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

    // Setter 메서드들
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

    // Entity 변환 메서드
    public Notice toEntity() {
        Notice notice = new Notice();
        notice.setTitle(this.title);
        notice.setContent(this.content);
        notice.setAuthorId(this.authorId);
        notice.setAuthorName(this.authorName);
        notice.setAuthorDepartment(this.authorDepartment);
        notice.setHasAttachments(this.hasAttachments != null ? this.hasAttachments : false);
        return notice;
    }

    // toString 메서드 (디버깅용)
    @Override
    public String toString() {
        return "NoticeRequestDto{" +
                "title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", authorId=" + authorId +
                ", authorName='" + authorName + '\'' +
                ", authorDepartment='" + authorDepartment + '\'' +
                ", hasAttachments=" + hasAttachments +
                '}';
    }
}
