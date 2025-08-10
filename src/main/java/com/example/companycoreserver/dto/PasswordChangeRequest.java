package com.example.companycoreserver.dto;

// dto/PasswordChangeRequest.java
public class PasswordChangeRequest {
    private Long userId;
    private String currentPassword;
    private String newPassword;

    // 기본 생성자
    public PasswordChangeRequest() {}

    // 생성자
    public PasswordChangeRequest(Long userId, String currentPassword, String newPassword) {
        this.userId = userId;
        this.currentPassword = currentPassword;
        this.newPassword = newPassword;
    }

    // Getter & Setter
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
