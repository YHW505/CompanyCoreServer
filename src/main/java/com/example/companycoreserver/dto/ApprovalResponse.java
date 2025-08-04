package com.example.companycoreserver.dto;

import com.example.companycoreserver.entity.Enum.ApprovalStatus;

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
    private String attachmentPath;

    // 기본 생성자
    public ApprovalResponse() {}

    // 모든 필드 생성자
    public ApprovalResponse(Long id, String title, String content, RequesterInfo requester,
                            ApproverInfo approver, LocalDateTime requestDate, ApprovalStatus status,
                            String rejectionReason, LocalDateTime processedDate, String attachmentPath) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.requester = requester;
        this.approver = approver;
        this.requestDate = requestDate;
        this.status = status;
        this.rejectionReason = rejectionReason;
        this.processedDate = processedDate;
        this.attachmentPath = attachmentPath;
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

    public String getAttachmentPath() {
        return attachmentPath;
    }

    public void setAttachmentPath(String attachmentPath) {
        this.attachmentPath = attachmentPath;
    }

    // 내부 클래스 - RequesterInfo
    public static class RequesterInfo {
        private Long userId;
        private String employeeCode;
        private String username;
        private String position;
        private String department;

        // 기본 생성자
        public RequesterInfo() {}

        // 모든 필드 생성자
        public RequesterInfo(Long userId, String employeeCode, String username,
                             String position, String department) {
            this.userId = userId;
            this.employeeCode = employeeCode;
            this.username = username;
            this.position = position;
            this.department = department;
        }

        // Getter & Setter
        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public String getEmployeeCode() {
            return employeeCode;
        }

        public void setEmployeeCode(String employeeCode) {
            this.employeeCode = employeeCode;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPosition() {
            return position;
        }

        public void setPosition(String position) {
            this.position = position;
        }

        public String getDepartment() {
            return department;
        }

        public void setDepartment(String department) {
            this.department = department;
        }
    }

    // 내부 클래스 - ApproverInfo
    public static class ApproverInfo {
        private Long userId;
        private String employeeCode;
        private String username;
        private String position;
        private String department;

        // 기본 생성자
        public ApproverInfo() {}

        // 모든 필드 생성자
        public ApproverInfo(Long userId, String employeeCode, String username,
                            String position, String department) {
            this.userId = userId;
            this.employeeCode = employeeCode;
            this.username = username;
            this.position = position;
            this.department = department;
        }

        // Getter & Setter
        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public String getEmployeeCode() {
            return employeeCode;
        }

        public void setEmployeeCode(String employeeCode) {
            this.employeeCode = employeeCode;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPosition() {
            return position;
        }

        public void setPosition(String position) {
            this.position = position;
        }

        public String getDepartment() {
            return department;
        }

        public void setDepartment(String department) {
            this.department = department;
        }
    }
}
