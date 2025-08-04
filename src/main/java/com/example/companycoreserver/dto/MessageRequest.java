package com.example.companycoreserver.dto;


public class MessageRequest {
    private String receiverEmail;
    private String messageType;
    private String title;
    private String content;

    // 기본 생성자
    public MessageRequest() {}

    // 전체 생성자
    public MessageRequest(String receiverEmail, String messageType, String title, String content) {
        this.receiverEmail = receiverEmail;
        this.messageType = messageType;
        this.title = title;
        this.content = content;
    }

    // Getter & Setter
    public String getReceiverEmail() {
        return receiverEmail;
    }

    public void getReceiverEmail(String receiverId) {
        this.receiverEmail = receiverId;
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
