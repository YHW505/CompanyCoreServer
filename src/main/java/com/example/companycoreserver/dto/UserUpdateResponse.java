// UserUpdateResponse.java
package com.example.companycoreserver.dto;


public class UserUpdateResponse {
    private boolean success;
    private String message;
    private UserInfo userInfo;

    // 기본 생성자
    public UserUpdateResponse() {}

    // 전체 생성자
    public UserUpdateResponse(boolean success, String message, UserInfo userInfo) {
        this.success = success;
        this.message = message;
        this.userInfo = userInfo;
    }

    // 성공/실패만 처리하는 생성자
    public UserUpdateResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
        this.userInfo = null;
    }

    // Getters
    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    // Setters
    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    @Override
    public String toString() {
        return "UserUpdateResponse{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", userInfo=" + userInfo +
                '}';
    }
}
