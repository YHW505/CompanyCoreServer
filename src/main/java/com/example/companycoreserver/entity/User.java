package com.example.companycoreserver.entity;

import com.example.companycoreserver.entity.Enum.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "employee_code", length = 255, unique = true, nullable = false)
    private String employeeCode;

    @Column(name = "username", length = 50, nullable = false)
    private String username;

    @Column(name = "join_date")
    @JsonFormat(shape = JsonFormat.Shape.ARRAY) // 배열 형식으로 파싱
    private LocalDate joinDate;

    @Column(name = "password", length = 255, nullable = false)
    private String password;

    @Column(name = "position_id")
    private Integer positionId;

    @Column(name = "department_id")
    private Integer departmentId;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Role role;

    @Column(name = "is_first_login")
    private Integer isFirstLogin;

    @Column(name = "is_active")
    private Integer isActive;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @JsonFormat(shape = JsonFormat.Shape.ARRAY) // 배열 형식으로 파싱
    private LocalDateTime createdAt;

    @Column(name = "email", length = 255, unique = true, nullable = false)
    private String email;

    @Column(name = "phone", length = 255)
    private String phone;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "address", length = 500)
    private String address;

    // 🔗 관계 매핑
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "position_id", insertable = false, updatable = false)
    @JsonIgnoreProperties({"users", "hibernateLazyInitializer", "handler"})
    private Position position;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", insertable = false, updatable = false)
    @JsonIgnoreProperties({"users", "hibernateLazyInitializer", "handler"})
    private Department department;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Attendance> attendances = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Schedule> schedules = new ArrayList<>();

    //    @OneToMany(mappedBy = "assignedToUser", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    @JsonIgnore
//    private List<Task> assignedTasks = new ArrayList<>();
//
//    @OneToMany(mappedBy = "assignedByUser", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    @JsonIgnore
//    private List<Task> createdTasks = new ArrayList<>();
// 1. 할당받은 업무들 - TaskAssignment를 통해 간접 연결
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<TaskAssignment> taskAssignments = new ArrayList<>();

    // 2. 생성한 업무들 - Task의 createdByUser와 연결
    @OneToMany(mappedBy = "createdByUser", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Task> createdTasks = new ArrayList<>();
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<LeaveRequest> leaves = new ArrayList<>();

    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Message> sentMessages = new ArrayList<>();

    @OneToMany(mappedBy = "receiver", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Message> receivedMessages = new ArrayList<>();

    // 기본 생성자
    public User() {
    }

    // 생성자
    public User(String employeeCode, String username, String email, String password,
                Integer positionId, Integer departmentId, Role role) {
        this.employeeCode = employeeCode;
        this.username = username;
        this.email = email;
        this.password = password;
        this.positionId = positionId;
        this.departmentId = departmentId;
        this.role = role;
    }

    // Getter/Setter (모든 필드에 대해)
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

    public LocalDate getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(LocalDate joinDate) {
        this.joinDate = joinDate;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getPositionId() {
        return positionId;
    }

    public void setPositionId(Integer positionId) {
        this.positionId = positionId;
    }

    public Integer getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Integer departmentId) {
        this.departmentId = departmentId;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Integer getIsFirstLogin() {
        return isFirstLogin;
    }

    public void setIsFirstLogin(Integer isFirstLogin) {
        this.isFirstLogin = isFirstLogin;
    }

    public Integer getIsActive() {
        return isActive;
    }

    public void setIsActive(Integer isActive) {
        this.isActive = isActive;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    // 관계 필드들의 Getter/Setter...
    public List<Attendance> getAttendances() {
        return attendances;
    }

    public void setAttendances(List<Attendance> attendances) {
        this.attendances = attendances;
    }

    public List<Schedule> getSchedules() {
        return schedules;
    }

    public void setSchedules(List<Schedule> schedules) {
        this.schedules = schedules;
    }

    public List<TaskAssignment> getTaskAssignments() {
        return taskAssignments;
    }

    public void setTaskAssignments(List<TaskAssignment> taskAssignments) {
        this.taskAssignments = taskAssignments;
    }

    public List<Task> getCreatedTasks() {
        return createdTasks;
    }

    public void setCreatedTasks(List<Task> createdTasks) {
        this.createdTasks = createdTasks;
    }

    public List<LeaveRequest> getLeaves() {
        return leaves;
    }

    public void setLeaves(List<LeaveRequest> leaves) {
        this.leaves = leaves;
    }

    public List<Message> getSentMessages() {
        return sentMessages;
    }

    public void setSentMessages(List<Message> sentMessages) {
        this.sentMessages = sentMessages;
    }

    public List<Message> getReceivedMessages() {
        return receivedMessages;
    }

    public void setReceivedMessages(List<Message> receivedMessages) {
        this.receivedMessages = receivedMessages;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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
                ", email='" + email + '\'' +
                ", role=" + role +
                ", positionId=" + positionId +
                ", departmentId=" + departmentId +
                ", isActive=" + isActive +
                '}';
    }
}
