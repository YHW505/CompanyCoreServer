package com.example.companycoreserver.entity;
import com.example.companycoreserver.dto.Role;
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

    @Column(name = "employee_code", length = 255)
    private String employeeCode;

    @Column(name = "username", length = 50)
    private String username;

    @Column(name = "join_date")
    private LocalDate joinDate;

    @Column(name = "password", length = 255)
    private String password;

    @Column(name = "position_id")
    private Integer positionId;

    @Column(name = "department_id")
    private Integer departmentId;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", length = 8)
    private Role role;

    @Column(name = "is_first_login")
    private Integer isFirstLogin = 1; // 1: 첫 로그인, 0: 일반 상태

    @Column(name = "is_active")
    private Integer isActive = 1; // 1: 활성, 0: 비활성

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "email", length = 255)
    private String email;

    @Column(name = "phone", length = 255)
    private String phone;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    // 기본 생성자
    public User() {}

    // 전체 생성자
    public User(Long userId, String employeeCode, String username, LocalDate joinDate,
                String password, Integer positionId, Integer departmentId, Role role,
                Integer isFirstLogin, Integer isActive, LocalDateTime createdAt,
                String email, String phone, LocalDate birthDate) {
        this.userId = userId;
        this.employeeCode = employeeCode;
        this.username = username;
        this.joinDate = joinDate;
        this.password = password;
        this.positionId = positionId;
        this.departmentId = departmentId;
        this.role = role;
        this.isFirstLogin = isFirstLogin;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.email = email;
        this.phone = phone;
        this.birthDate = birthDate;
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

    public LocalDate getJoinDate() {
        return joinDate;
    }

    public String getPassword() {
        return password;
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

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public LocalDate getBirthDate() {
        return birthDate;
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

    public void setJoinDate(LocalDate joinDate) {
        this.joinDate = joinDate;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    // 편의 메서드 추가
    public boolean isFirstLoginBoolean() {
        return isFirstLogin != null && isFirstLogin == 1;
    }

    public boolean isActiveBoolean() {
        return isActive != null && isActive == 1;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", employeeCode='" + employeeCode + '\'' +
                ", username='" + username + '\'' +
                ", joinDate=" + joinDate +
                ", positionId=" + positionId +
                ", departmentId=" + departmentId +
                ", role=" + role +
                ", isFirstLogin=" + isFirstLogin +
                ", isActive=" + isActive +
                ", createdAt=" + createdAt +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", birthDate=" + birthDate +
                '}';
    }
}

