package com.example.companycoreserver.entity;

import com.example.companycoreserver.entity.Enum.ApprovalStatus;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "approvals")
public class Approval {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title; // 결재 제목

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content; // 결재 내용

    // 결재 요청자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id", nullable = false)
    private User requester;

    // 결재자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approver_id", nullable = false)
    private User approver;

    @Column(nullable = false)
    private LocalDateTime requestDate; // 결재 요청일

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApprovalStatus status = ApprovalStatus.PENDING; // 승인/거부 여부

    @Column(columnDefinition = "TEXT")
    private String rejectionReason; // 거부 이유

    private LocalDateTime processedDate; // 처리일 (승인/거부 날짜)

    // 첨부파일 관련 필드들
    @Column(length = 255)
    private String attachmentFilename;

    @Column(length = 100)
    private String attachmentContentType;

    @Column
    private Long attachmentSize;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String attachmentContent; // Base64 인코딩된 첨부파일 내용

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // 생성자
    public Approval() {
    }

    public Approval(String title, String content, User requester, User approver) {
        this.title = title;
        this.content = content;
        this.requester = requester;
        this.approver = approver;
        this.requestDate = LocalDateTime.now();
        this.status = ApprovalStatus.PENDING;
    }

    public Approval(String title, String content, User requester, User approver, String attachmentPath) {
        this.title = title;
        this.content = content;
        this.requester = requester;
        this.approver = approver;
        this.requestDate = LocalDateTime.now();
        this.status = ApprovalStatus.PENDING;
//        this.attachmentPath = attachmentPath;
    }

    // 첨부파일 메타데이터를 포함한 생성자 추가
    public Approval(String title, String content, User requester, User approver,
                    String attachmentFilename, String attachmentContentType, Long attachmentSize) {
        this.title = title;
        this.content = content;
        this.requester = requester;
        this.approver = approver;
        this.attachmentFilename = attachmentFilename;
        this.attachmentContentType = attachmentContentType;
        this.attachmentSize = attachmentSize;
        this.status = ApprovalStatus.PENDING;
        this.requestDate = LocalDateTime.now();
    }

    // 🆕 첨부파일 메타데이터만 업데이트 (Base64 문자열 방식)
    public void updateAttachment(String filename, String contentType, String base64Content) {
        this.attachmentFilename = filename;
        this.attachmentContentType = contentType;
        this.attachmentContent = base64Content;
        this.updatedAt = LocalDateTime.now();
    }

    // ✅ 첨부파일 제거
    public void removeAttachment() {
        this.attachmentFilename = null;
        this.attachmentContentType = null;
        this.attachmentSize = null;
        this.attachmentContent = null;
        this.updatedAt = LocalDateTime.now();
    }

    // Getter, Setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public User getRequester() {
        return requester;
    }

    public void setRequester(User requester) {
        this.requester = requester;
    }

    public User getApprover() {
        return approver;
    }

    public void setApprover(User approver) {
        this.approver = approver;
    }

    public LocalDateTime getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(LocalDateTime requestDate) {
        this.requestDate = requestDate;
    }

    public ApprovalStatus getStatus() {
        return status;
    }

    public void setStatus(ApprovalStatus status) {
        this.status = status;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public LocalDateTime getProcessedDate() {
        return processedDate;
    }

    public void setProcessedDate(LocalDateTime processedDate) {
        this.processedDate = processedDate;
    }

    public String getAttachmentFilename() {
        return attachmentFilename;
    }

    public void setAttachmentFilename(String attachmentFilename) {
        this.attachmentFilename = attachmentFilename;
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

    public String getAttachmentContent() {
        return attachmentContent;
    }

    public void setAttachmentContent(String attachmentContent) {
        this.attachmentContent = attachmentContent;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // 편의 메서드
    public boolean isPending() {
        return this.status == ApprovalStatus.PENDING;
    }

    public boolean isApproved() {
        return this.status == ApprovalStatus.APPROVED;
    }

    public boolean isRejected() {
        return this.status == ApprovalStatus.REJECTED;
    }

}
