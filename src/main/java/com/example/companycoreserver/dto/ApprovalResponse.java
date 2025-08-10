package com.example.companycoreserver.dto;

import com.example.companycoreserver.entity.Enum.ApprovalStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;

public class ApprovalResponse {
    private Long id;
    private String title;
    private String content;
    private RequesterInfo requester;
    private ApproverInfo approver;
    private LocalDateTime requestDate;
    private ApprovalStatus status;
    private String rejectionReason;
    private LocalDateTime processedDate;
    private String attachmentFilename;
    private String attachmentContentType;
    private Long attachmentSize;

    @JsonIgnore
    private String attachmentContent; // Base64 인코딩된 첨부파일 내용

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 기본 생성자
    public ApprovalResponse() {}

    // 🔄 모든 필드 생성자 수정
    public ApprovalResponse(Long id, String title, String content, RequesterInfo requester,
                            ApproverInfo approver, LocalDateTime requestDate, ApprovalStatus status,
                            String rejectionReason, LocalDateTime processedDate,
                            String attachmentFilename, String attachmentContentType, Long attachmentSize,
                            LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.requester = requester;
        this.approver = approver;
        this.requestDate = requestDate;
        this.status = status;
        this.rejectionReason = rejectionReason;
        this.processedDate = processedDate;
        this.attachmentFilename = attachmentFilename;
        this.attachmentContentType = attachmentContentType;
        this.attachmentSize = attachmentSize;
//        this.attachmentContent = attachmentContent;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // 🆕 첨부파일 유무 확인 편의 메서드
    public boolean hasAttachment() {
        return attachmentFilename != null && !attachmentFilename.trim().isEmpty();
    }

    // Getter & Setter
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

    public RequesterInfo getRequester() {
        return requester;
    }

    public void setRequester(RequesterInfo requester) {
        this.requester = requester;
    }

    public ApproverInfo getApprover() {
        return approver;
    }

    public void setApprover(ApproverInfo approver) {
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

    // 🔄 첨부파일 관련 getter/setter 수정
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

    // 🆕 생성/수정 시간 getter/setter 추가
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

    // 내부 클래스들은 기존과 동일
    public static class RequesterInfo {
        private Long userId;
        private String employeeCode;
        private String username;
        private String position;
        private String department;

        public RequesterInfo() {}

        public RequesterInfo(Long userId, String employeeCode, String username,
                             String position, String department) {
            this.userId = userId;
            this.employeeCode = employeeCode;
            this.username = username;
            this.position = position;
            this.department = department;
        }

        // Getter & Setter (기존과 동일)
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public String getEmployeeCode() { return employeeCode; }
        public void setEmployeeCode(String employeeCode) { this.employeeCode = employeeCode; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPosition() { return position; }
        public void setPosition(String position) { this.position = position; }
        public String getDepartment() { return department; }
        public void setDepartment(String department) { this.department = department; }
    }

    public static class ApproverInfo {
        private Long userId;
        private String employeeCode;
        private String username;
        private String position;
        private String department;

        public ApproverInfo() {}

        public ApproverInfo(Long userId, String employeeCode, String username,
                            String position, String department) {
            this.userId = userId;
            this.employeeCode = employeeCode;
            this.username = username;
            this.position = position;
            this.department = department;
        }

        // Getter & Setter (기존과 동일)
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public String getEmployeeCode() { return employeeCode; }
        public void setEmployeeCode(String employeeCode) { this.employeeCode = employeeCode; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPosition() { return position; }
        public void setPosition(String position) { this.position = position; }
        public String getDepartment() { return department; }
        public void setDepartment(String department) { this.department = department; }
    }
}
