package com.example.companycoreserver.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.awt.*;
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

    // MessageType enum이 있다면 import해서 사용
     @Enumerated(EnumType.STRING)
     @Column(name = "message_type")
     private TrayIcon.MessageType messageType;

    @Column(name = "title", length = 255)
    private String title;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "is_read", columnDefinition = "TINYINT(1) DEFAULT 0")
    private Boolean isRead = false;

    @Column(name = "sent_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime sentAt;

    // 🔗 관계 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", insertable = false, updatable = false)
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", insertable = false, updatable = false)
    private User receiver;

    // 기본 생성자
    public Message() {}

    // 생성자
    public Message(Long senderId, Long receiverId, TrayIcon.MessageType messageType,
                   String title, String content) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.messageType = messageType;
        this.title = title;
        this.content = content;
    }

    // Getter/Setter
    public Integer getMessageId() { return messageId; }
    public void setMessageId(Integer messageId) { this.messageId = messageId; }

    public Long getSenderId() { return senderId; }
    public void setSenderId(Long senderId) { this.senderId = senderId; }

    public Long getReceiverId() { return receiverId; }
    public void setReceiverId(Long receiverId) { this.receiverId = receiverId; }

    public TrayIcon.MessageType getMessageType() { return messageType; }
    public void setMessageType(TrayIcon.MessageType messageType) { this.messageType = messageType; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Boolean getIsRead() { return isRead; }
    public void setIsRead(Boolean isRead) { this.isRead = isRead; }

    public LocalDateTime getSentAt() { return sentAt; }
    public void setSentAt(LocalDateTime sentAt) { this.sentAt = sentAt; }

    public User getSender() { return sender; }
    public void setSender(User sender) { this.sender = sender; }

    public User getReceiver() { return receiver; }
    public void setReceiver(User receiver) { this.receiver = receiver; }

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
                '}';
    }
}
