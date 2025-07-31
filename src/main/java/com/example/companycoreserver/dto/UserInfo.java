package com.example.companycoreserver.dto;
import com.fasterxml.jackson.annotation.JsonProperty;

public class UserInfo {
    private Long userId;
    private String employeeCode;
    private String username;
    private String email;
    private String phone;
    private String birthDate;
    private String role;
    private String departmentName;
    private String positionName;

    @JsonProperty("isFirstLogin")
    private Integer isFirstLogin; // Integer로 변경

    @JsonProperty("isActive")
    private Integer isActive; // Integer로 변경

    private String createdAt;

    // 기본 생성자
    public UserInfo() {}

    // 전체 생성자
    public UserInfo(Long userId, String employeeCode, String username, String email,
                    String phone, String birthDate, String role, String departmentName,
                    String positionName, Integer isFirstLogin, Integer isActive, String createdAt) {
        this.userId = userId;
        this.employeeCode = employeeCode;
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.birthDate = birthDate;
        this.role = role;
        this.departmentName = departmentName;
        this.positionName = positionName;
        this.isFirstLogin = isFirstLogin;
        this.isActive = isActive;
        this.createdAt = createdAt;
    }

    // Getters
    public Long getUserId() {
        return userId;
    }

    public String getEmployeeCode() {
        return employeeCode;
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

    public String getRole() {
        return role;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public String getPositionName() {
        return positionName;
    }

    public Integer getIsFirstLogin() {
        return isFirstLogin;
    }

    public Integer getIsActive() {
        return isActive;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    // Setters
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setEmployeeCode(String employeeCode) {
        this.employeeCode = employeeCode;
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

    public void setRole(String role) {
        this.role = role;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public void setPositionName(String positionName) {
        this.positionName = positionName;
    }

    public void setIsFirstLogin(Integer isFirstLogin) {
        this.isFirstLogin = isFirstLogin;
    }

    public void setIsActive(Integer isActive) {
        this.isActive = isActive;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    // 편의 메서드 추가
    public boolean isFirstLoginBoolean() {
        return isFirstLogin != null && isFirstLogin == 1;
    }

    public boolean isActiveBoolean() {
        return isActive != null && isActive == 1;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "userId=" + userId +
                ", employeeCode='" + employeeCode + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", birthDate='" + birthDate + '\'' +
                ", role='" + role + '\'' +
                ", departmentName='" + departmentName + '\'' +
                ", positionName='" + positionName + '\'' +
                ", isFirstLogin=" + isFirstLogin +
                ", isActive=" + isActive +
                ", createdAt='" + createdAt + '\'' +
                '}';
    }
}
