package com.example.companycoreserver.dto;

import com.example.companycoreserver.entity.Enum.MessageType;

public class MessageRequest {
    private String receiverEmail;
    private MessageType messageType;
    private String title;
    private String content;

    // 🆕 첨부파일 관련 필드 추가
    private String attachmentContentType;
    private Long attachmentSize;
    private String attachmentFileName; // 원본 파일명
    private String attachmentContent; // Base64 인코딩된 파일 내용


    // 기본 생성자
    public MessageRequest() {}

    // 전체 생성자 (첨부파일 포함)
    public MessageRequest(String receiverEmail, MessageType messageType, String title, String content,
                          String attachmentContentType, Long attachmentSize, String attachmentFileName, String attachmentContent) {
        this.receiverEmail = receiverEmail;
        this.messageType = messageType;
        this.title = title;
        this.content = content;
        this.attachmentContentType = attachmentContentType;
        this.attachmentSize = attachmentSize;
        this.attachmentFileName = attachmentFileName;
        this.attachmentContent = attachmentContent;
    }

    // 기존 생성자 (첨부파일 없음)
    public MessageRequest(String receiverEmail, MessageType messageType, String title, String content) {
        this.receiverEmail = receiverEmail;
        this.messageType = messageType;
        this.title = title;
        this.content = content;
    }

    // Getter & Setter
    public String getReceiverEmail() {
        return receiverEmail;
    }

    // 🔧 수정: 메소드명 오타 수정
    public void setReceiverEmail(String receiverEmail) {
        this.receiverEmail = receiverEmail;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
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

    // 🆕 첨부파일 관련 Getter & Setter
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

    public String getAttachmentFileName() {
        return attachmentFileName;
    }

    public void setAttachmentFileName(String attachmentFileName) {
        this.attachmentFileName = attachmentFileName;
    }

    public String getAttachmentContent() {
        return attachmentContent;
    }
    public void setAttachmentContent(String attachmentContent) {
        this.attachmentContent = attachmentContent;
    }

    // 🆕 첨부파일 존재 여부 확인
    public boolean hasAttachment() {
        return attachmentFileName != null && !attachmentFileName.trim().isEmpty();
    }
}
