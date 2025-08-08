package com.example.companycoreserver.dto;

import java.time.LocalDateTime;

public class MessageSummaryResponse {
    private Integer messageId;
    private String title;
    private String content;
    private String senderName;
    private String receiverName;
    private boolean isRead;
    private LocalDateTime sentAt;
    private String messageType;

    // 🆕 첨부파일 관련 필드 추가
    private boolean hasAttachment;
    private String attachmentFilename; // 파일명만 표시

    // 기본 생성자
    public MessageSummaryResponse() {}

    // 전체 생성자 (첨부파일 정보 포함)
    public MessageSummaryResponse(Integer messageId, String title, String content, String senderName,
                                  String receiverName, boolean isRead, LocalDateTime sentAt, String messageType,
                                  boolean hasAttachment, String attachmentFileName) {
        this.messageId = messageId;
        this.title = title;
        this.content = content;
        this.senderName = senderName;
        this.receiverName = receiverName;
        this.isRead = isRead;
        this.sentAt = sentAt;
        this.messageType = messageType;
        this.hasAttachment = hasAttachment;
        this.attachmentFilename = attachmentFileName;
    }

    // 기존 생성자 (첨부파일 없음)
    public MessageSummaryResponse(Integer messageId, String title, String content, String senderName,
                                  String receiverName, boolean isRead, LocalDateTime sentAt, String messageType) {
        this.messageId = messageId;
        this.title = title;
        this.content = content;
        this.senderName = senderName;
        this.receiverName = receiverName;
        this.isRead = isRead;
        this.sentAt = sentAt;
        this.messageType = messageType;
        this.hasAttachment = false;
    }

    // 기존 Getter & Setter들...
    public Integer getMessageId() {
        return messageId;
    }

    public void setMessageId(Integer messageId) {
        this.messageId = messageId;
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

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    // 🆕 첨부파일 관련 Getter & Setter
    public boolean isHasAttachment() {
        return hasAttachment;
    }

    public void setHasAttachment(boolean hasAttachment) {
        this.hasAttachment = hasAttachment;
    }

    public String getAttachmentFilename() {
        return attachmentFilename;
    }

    public void setAttachmentFilename(String attachmentFileName) {
        this.attachmentFilename = attachmentFileName;
    }
}
