package com.example.companycoreserver.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "employee_code", unique = true, nullable = false)
    private String employeeCode;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "join_date")
    private LocalDate joinDate;

    @Column(name = "password", nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Role role;

    @Column(name = "department_id")
    private Integer departmentId;

    @Column(name = "is_first_login")
    private Boolean isFirstLogin = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Constructors
    public User() {}

    public User(String employeeCode, String username, String password, Role role) {
        this.employeeCode = employeeCode;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    // Getters and Setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getEmployeeCode() { return employeeCode; }
    public void setEmployeeCode(String employeeCode) { this.employeeCode = employeeCode; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }

    public LocalDate getJoinDate() { return joinDate; }
    public void setJoinDate(LocalDate joinDate) { this.joinDate = joinDate; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public Integer getDepartmentId() { return departmentId; }
    public void setDepartmentId(Integer departmentId) { this.departmentId = departmentId; }

    public Boolean getIsFirstLogin() { return isFirstLogin; }
    public void setIsFirstLogin(Boolean isFirstLogin) { this.isFirstLogin = isFirstLogin; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public enum Role {
        ADMIN,
        MANAGER,
        EMPLOYEE,
        // USER가 있는지 확인
    }

}
