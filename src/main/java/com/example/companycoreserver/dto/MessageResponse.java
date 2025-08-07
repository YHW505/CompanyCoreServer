package com.example.companycoreserver.dto;
import java.time.LocalDateTime;

public class MessageResponse {
    private Integer messageId;
    private Long senderId;
    private Long receiverId;
    private String messageType;
    private String title;
    private String content;
    private boolean isRead;
    private LocalDateTime sentAt;

    // 🆕 첨부파일 관련 필드 추가 (content는 제외)
    private boolean hasAttachment;
    private String attachmentContentType;
    private Long attachmentSize;
    private String attachmentFileName;

    // 발신자 정보
    private String senderName;
    private String senderEmployeeCode;
    private String senderPositionName;
    private String senderDepartmentName;
    private String senderEmail;

    // 수신자 정보
    private String receiverName;
    private String receiverEmployeeCode;
    private String receiverPositionName;
    private String receiverDepartmentName;
    private String receiverEmail;

    // 기본 생성자
    public MessageResponse() {}

    // 전체 생성자 (첨부파일 정보 포함)
    public MessageResponse(Integer messageId, Long senderId, Long receiverId, String messageType,
                           String title, String content, boolean isRead, LocalDateTime sentAt,
                           boolean hasAttachment, String attachmentContentType, Long attachmentSize, String attachmentFileName,
                           String senderName, String senderEmployeeCode, String senderPositionName,
                           String senderDepartmentName, String senderEmail,
                           String receiverName, String receiverEmployeeCode, String receiverPositionName,
                           String receiverDepartmentName, String receiverEmail) {
        this.messageId = messageId;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.messageType = messageType;
        this.title = title;
        this.content = content;
        this.isRead = isRead;
        this.sentAt = sentAt;
        this.hasAttachment = hasAttachment;
        this.attachmentContentType = attachmentContentType;
        this.attachmentSize = attachmentSize;
        this.attachmentFileName = attachmentFileName;
        this.senderName = senderName;
        this.senderEmployeeCode = senderEmployeeCode;
        this.senderPositionName = senderPositionName;
        this.senderDepartmentName = senderDepartmentName;
        this.senderEmail = senderEmail;
        this.receiverName = receiverName;
        this.receiverEmployeeCode = receiverEmployeeCode;
        this.receiverPositionName = receiverPositionName;
        this.receiverDepartmentName = receiverDepartmentName;
        this.receiverEmail = receiverEmail;
    }

    // 기존 Getter & Setter들...
    public Integer getMessageId() {
        return messageId;
    }

    public void setMessageId(Integer messageId) {
        this.messageId = messageId;
    }

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

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

    // 🆕 첨부파일 관련 Getter & Setter
    public boolean isHasAttachment() {
        return hasAttachment;
    }

    public void setHasAttachment(boolean hasAttachment) {
        this.hasAttachment = hasAttachment;
    }

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

    // 기존 사용자 정보 Getter & Setter들... (생략)
    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderEmployeeCode() {
        return senderEmployeeCode;
    }

    public void setSenderEmployeeCode(String senderEmployeeCode) {
        this.senderEmployeeCode = senderEmployeeCode;
    }

    public String getSenderPositionName() {
        return senderPositionName;
    }

    public void setSenderPositionName(String senderPositionName) {
        this.senderPositionName = senderPositionName;
    }

    public String getSenderDepartmentName() {
        return senderDepartmentName;
    }

    public void setSenderDepartmentName(String senderDepartmentName) {
        this.senderDepartmentName = senderDepartmentName;
    }

    public String getSenderEmail() {
        return senderEmail;
    }

    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getReceiverEmployeeCode() {
        return receiverEmployeeCode;
    }

    public void setReceiverEmployeeCode(String receiverEmployeeCode) {
        this.receiverEmployeeCode = receiverEmployeeCode;
    }

    public String getReceiverPositionName() {
        return receiverPositionName;
    }

    public void setReceiverPositionName(String receiverPositionName) {
        this.receiverPositionName = receiverPositionName;
    }

    public String getReceiverDepartmentName() {
        return receiverDepartmentName;
    }

    public void setReceiverDepartmentName(String receiverDepartmentName) {
        this.receiverDepartmentName = receiverDepartmentName;
    }

    public String getReceiverEmail() {
        return receiverEmail;
    }

    public void setReceiverEmail(String receiverEmail) {
        this.receiverEmail = receiverEmail;
    }
}
