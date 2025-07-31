package com.example.companycoreserver.dto;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty;

public class UserUpdateRequest {
    private Long userId; // 추가
    private String username;
    private String email;
    private String phone;
    private String birthDate;
    private String currentPassword;
    private String newPassword;

    @JsonProperty("isFirstLogin")
    private boolean isFirstLogin;

    // 기본 생성자
    public UserUpdateRequest() {}

    // 전체 생성자
    public UserUpdateRequest(Long userId, String username, String email, String phone,
                             String birthDate, String currentPassword, String newPassword,
                             boolean isFirstLogin) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.birthDate = birthDate;
        this.currentPassword = currentPassword;
        this.newPassword = newPassword;
        this.isFirstLogin = isFirstLogin;
    }

    // Getters
    public Long getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public String getCurrentPassword() {
        return currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public boolean isFirstLogin() {
        return isFirstLogin;
    }

    // Setters
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public void setFirstLogin(boolean firstLogin) {
        isFirstLogin = firstLogin;
    }

    @Override
    public String toString() {
        return "UserUpdateRequest{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", birthDate='" + birthDate + '\'' +
                ", isFirstLogin=" + isFirstLogin +
                '}';
    }
}
