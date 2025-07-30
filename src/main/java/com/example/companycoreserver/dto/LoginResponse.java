package com.example.companycoreserver.dto;

public class LoginResponse {
    private String token;
    private String employeeCode;
    private String username;  // ✅ User entity의 username 필드에 맞춤
    private String role;
    private String message;
    private Boolean firstLogin;  // ✅ Boolean 타입으로 (User entity와 동일)
    private Long userId;  // ✅ 추가
    private Integer departmentId;  // ✅ 추가

    // Constructors
    public LoginResponse() {}

    // Getters and Setters
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getEmployeeCode() { return employeeCode; }
    public void setEmployeeCode(String employeeCode) { this.employeeCode = employeeCode; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Boolean getFirstLogin() { return firstLogin; }
    public void setFirstLogin(Boolean firstLogin) { this.firstLogin = firstLogin; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Integer getDepartmentId() { return departmentId; }
    public void setDepartmentId(Integer departmentId) { this.departmentId = departmentId; }
}
