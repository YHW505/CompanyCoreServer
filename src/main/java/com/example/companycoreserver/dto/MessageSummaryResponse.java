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

    // 기본 생성자
    public MessageSummaryResponse() {}

    // 전체 생성자
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
    }

    // Getter & Setter
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
}
