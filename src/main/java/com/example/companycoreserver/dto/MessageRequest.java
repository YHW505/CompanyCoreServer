package com.example.companycoreserver.dto;


public class MessageRequest {
    private Long receiverId;
    private String messageType;
    private String title;
    private String content;

    // 기본 생성자
    public MessageRequest() {}

    // 전체 생성자
    public MessageRequest(Long receiverId, String messageType, String title, String content) {
        this.receiverId = receiverId;
        this.messageType = messageType;
        this.title = title;
        this.content = content;
    }

    // Getter & Setter
    public Long getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
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
}
