package com.example.companycoreserver.dto;

import com.example.companycoreserver.entity.Attendance;
import com.example.companycoreserver.entity.Enum.AttendanceStatus;
import com.example.companycoreserver.entity.User;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class AttendanceResponse {
    private Long userId;
    private LocalDateTime checkIn;
    private LocalDateTime checkOut;
    private String workHours;
    private LocalDate workDate;
    private AttendanceStatus status;

    // User 정보는 필요한 것만
    private String username;
    private String employeeCode;
    private String departmentName;
    private String positionName;
    private String email;

    // 기본 생성자
    public AttendanceResponse() {}

    // Entity에서 DTO로 변환하는 생성자
    public AttendanceResponse(Attendance attendance) {
        this.userId = attendance.getUserId();
        this.checkIn = attendance.getCheckIn();
        this.checkOut = attendance.getCheckOut();
        this.workHours = attendance.getWorkHours();
        this.workDate = attendance.getWorkDate();
        this.status = attendance.getStatus();

        // User 정보 안전하게 추출
        if (attendance.getUser() != null) {
            User user = attendance.getUser();
            this.username = user.getUsername();
            this.employeeCode = user.getEmployeeCode();
            this.email = user.getEmail();

            // Department 정보
            if (user.getDepartment() != null) {
                this.departmentName = user.getDepartment().getDepartmentName();
            }

            // Position 정보
            if (user.getPosition() != null) {
                this.positionName = user.getPosition().getPositionName();
            }
        }
    }

    // getter, setter 모두 추가
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public LocalDateTime getCheckIn() { return checkIn; }
    public void setCheckIn(LocalDateTime checkIn) { this.checkIn = checkIn; }

    public LocalDateTime getCheckOut() { return checkOut; }
    public void setCheckOut(LocalDateTime checkOut) { this.checkOut = checkOut; }

    public String getWorkHours() { return workHours; }
    public void setWorkHours(String workHours) { this.workHours = workHours; }

    public LocalDate getWorkDate() { return workDate; }
    public void setWorkDate(LocalDate workDate) { this.workDate = workDate; }

    public AttendanceStatus getStatus() { return status; }
    public void setStatus(AttendanceStatus status) { this.status = status; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmployeeCode() { return employeeCode; }
    public void setEmployeeCode(String employeeCode) { this.employeeCode = employeeCode; }

    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }

    public String getPositionName() { return positionName; }
    public void setPositionName(String positionName) { this.positionName = positionName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
