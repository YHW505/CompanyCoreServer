package com.example.companycoreserver.entity;

import com.example.companycoreserver.entity.Enum.MessageType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    private Integer messageId;

    @Column(name = "sender_id")
    private Long senderId;

    @Column(name = "receiver_id")
    private Long receiverId;

    @Enumerated(EnumType.STRING)
    @Column(name = "message_type")
    private MessageType messageType;

    @Column(name = "title", length = 255)
    private String title;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "is_read", columnDefinition = "TINYINT(1) DEFAULT 0")
    private Boolean isRead = false;

    @Column(name = "sent_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime sentAt;

    // 🆕 첨부파일 관련 필드들 추가
    @Column(name = "attachment_content_type", length = 100)
    private String attachmentContentType;

    @Column(name = "attachment_size")
    private Long attachmentSize;

    @Column(name = "attachment_content", columnDefinition = "TEXT")
    private String attachmentContent;

    @Column(name = "attachment_filename", length = 255)
    private String attachmentFilename;

    // 🔗 관계 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", insertable = false, updatable = false)
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", insertable = false, updatable = false)
    private User receiver;

    // 기본 생성자
    public Message() {
    }

    // 생성자 (기존)
    public Message(Long senderId, Long receiverId, MessageType messageType,
                   String title, String content) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.messageType = messageType;
        this.title = title;
        this.content = content;
    }

    // 🆕 첨부파일 포함 생성자
    public Message(Long senderId, Long receiverId, MessageType messageType,
                   String title, String content, String attachmentContentType,
                   Long attachmentSize, String attachmentContent, String attachmentFilename) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.messageType = messageType;
        this.title = title;
        this.content = content;
        this.attachmentContentType = attachmentContentType;
        this.attachmentSize = attachmentSize;
        this.attachmentContent = attachmentContent;
        this.attachmentFilename = attachmentFilename;
    }

    // 기존 Getter/Setter들...
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

    public Boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }

    // 🆕 첨부파일 관련 Getter/Setter들
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

    public String getAttachmentContent() {
        return attachmentContent;
    }

    public void setAttachmentContent(String attachmentContent) {
        this.attachmentContent = attachmentContent;
    }

    public String getAttachmentFilename() {
        return attachmentFilename;
    }

    public void setAttachmentFilename(String attachmentFilename) {
        this.attachmentFilename = attachmentFilename;
    }


    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    // 🆕 첨부파일 존재 여부 확인 헬퍼 메소드
    public boolean hasAttachment() {
        return attachmentContent != null && !attachmentContent.trim().isEmpty();
    }

    // 🆕 첨부파일 정보 초기화 헬퍼 메소드
    public void clearAttachment() {
        this.attachmentContentType = null;
        this.attachmentSize = null;
        this.attachmentContent = null;
    }

    @PrePersist
    protected void onCreate() {
        sentAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "Message{" +
                "messageId=" + messageId +
                ", senderId=" + senderId +
                ", receiverId=" + receiverId +
                ", messageType=" + messageType +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", isRead=" + isRead +
                ", sentAt=" + sentAt +
                ", hasAttachment=" + hasAttachment() +
                ", attachmentSize=" + attachmentSize +
                '}';
    }
}
