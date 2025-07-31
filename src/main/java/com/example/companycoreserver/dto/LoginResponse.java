package com.example.companycoreserver.dto;

import com.example.companycoreserver.entity.User;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class LoginResponse {
    private Long userId;
    private String username;
    private String email;
    private String phone;
    private String token;
    private String employeeCode;
    private LocalDate joinDate;
    private Integer positionId;
    private Integer departmentId;
    private Role role;
    private Integer isFirstLogin;
    private Integer isActive;
    private LocalDateTime createdAt;

    // 기본 생성자
    public LoginResponse() {}

    // 전체 필드 생성자
    public LoginResponse(Long userId, String username, String email, String phone, String token,
                         String employeeCode, LocalDate joinDate, Integer positionId, Integer departmentId,
                         Role role, Integer isFirstLogin, Integer isActive, LocalDateTime createdAt) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.token = token;
        this.employeeCode = employeeCode;
        this.joinDate = joinDate;
        this.positionId = positionId;
        this.departmentId = departmentId;
        this.role = role;
        this.isFirstLogin = isFirstLogin;
        this.isActive = isActive;
        this.createdAt = createdAt;
    }

    // User 객체와 token으로 생성하는 편의 생성자
    public LoginResponse(User user, String token) {
        this.userId = user.getUserId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.phone = user.getPhone();
        this.token = token;
        this.employeeCode = user.getEmployeeCode();
        this.joinDate = user.getJoinDate();
        this.positionId = user.getPositionId();
        this.departmentId = user.getDepartmentId();
        this.role = user.getRole();
        this.isFirstLogin = user.getIsFirstLogin();
        this.isActive = user.getIsActive();
        this.createdAt = user.getCreatedAt();
    }

    // Getter 메서드들
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

    public String getToken() {
        return token;
    }

    public String getEmployeeCode() {
        return employeeCode;
    }

    public LocalDate getJoinDate() {
        return joinDate;
    }

    public Integer getPositionId() {
        return positionId;
    }

    public Integer getDepartmentId() {
        return departmentId;
    }

    public Role getRole() {
        return role;
    }

    public Integer getIsFirstLogin() {
        return isFirstLogin;
    }

    public Integer getIsActive() {
        return isActive;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // Setter 메서드들
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

    public void setToken(String token) {
        this.token = token;
    }

    public void setEmployeeCode(String employeeCode) {
        this.employeeCode = employeeCode;
    }

    public void setJoinDate(LocalDate joinDate) {
        this.joinDate = joinDate;
    }

    public void setPositionId(Integer positionId) {
        this.positionId = positionId;
    }

    public void setDepartmentId(Integer departmentId) {
        this.departmentId = departmentId;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public void setIsFirstLogin(Integer isFirstLogin) {
        this.isFirstLogin = isFirstLogin;
    }

    public void setIsActive(Integer isActive) {
        this.isActive = isActive;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // toString 메서드
    @Override
    public String toString() {
        return "LoginResponse{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", token='" + token + '\'' +
                ", employeeCode='" + employeeCode + '\'' +
                ", joinDate=" + joinDate +
                ", positionId=" + positionId +
                ", departmentId=" + departmentId +
                ", role='" + role + '\'' +
                ", isFirstLogin=" + isFirstLogin +
                ", isActive=" + isActive +
                ", createdAt=" + createdAt +
                '}';
    }
}
