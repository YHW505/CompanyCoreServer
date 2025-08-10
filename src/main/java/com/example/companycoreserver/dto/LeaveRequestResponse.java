package com.example.companycoreserver.dto;


import com.example.companycoreserver.entity.Enum.LeaveStatus;
import com.example.companycoreserver.entity.Enum.LeaveType;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class LeaveRequestResponse {
    private Long leaveId;
    private Long userId;
    private String userName;        // 신청자 이름
    private Integer userDepartment;  // 신청자 부서
    private Integer userPosition;    // 신청자 직급
    private LeaveType leaveType;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    private String reason;
    private LeaveStatus status;

    private Long approvedBy;
    private String approverName;    // 승인자 이름
    private String approverPosition; // 승인자 직급

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime approvedAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime appliedAt;

    // 기본 생성자
    public LeaveRequestResponse() {}

    // 생성자
    public LeaveRequestResponse(Long leaveId, Long userId, String userName,
                                Integer userDepartment, Integer userPosition,
                                   LeaveType leaveType, LocalDate startDate, LocalDate endDate,
                                   String reason, LeaveStatus status, Long approvedBy,
                                   String approverName, String approverPosition,
                                   LocalDateTime approvedAt, LocalDateTime appliedAt) {
        this.leaveId = leaveId;
        this.userId = userId;
        this.userName = userName;
        this.userDepartment = userDepartment;
        this.userPosition = userPosition;
        this.leaveType = leaveType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.reason = reason;
        this.status = status;
        this.approvedBy = approvedBy;
        this.approverName = approverName;
        this.approverPosition = approverPosition;
        this.approvedAt = approvedAt;
        this.appliedAt = appliedAt;
    }

    // Getter, Setter
    public Long getLeaveId() { return leaveId; }
    public void setLeaveId(Long leaveId) { this.leaveId = leaveId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public Integer getUserDepartment() { return userDepartment; }
    public void setUserDepartment(Integer userDepartment) { this.userDepartment = userDepartment; }

    public Integer getUserPosition() { return userPosition; }
    public void setUserPosition(Integer userPosition) { this.userPosition = userPosition; }

    public LeaveType getLeaveType() { return leaveType; }
    public void setLeaveType(LeaveType leaveType) { this.leaveType = leaveType; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public LeaveStatus getStatus() { return status; }
    public void setStatus(LeaveStatus status) { this.status = status; }

    public Long getApprovedBy() { return approvedBy; }
    public void setApprovedBy(Long approvedBy) { this.approvedBy = approvedBy; }

    public String getApproverName() { return approverName; }
    public void setApproverName(String approverName) { this.approverName = approverName; }

    public String getApproverPosition() { return approverPosition; }
    public void setApproverPosition(String approverPosition) { this.approverPosition = approverPosition; }

    public LocalDateTime getApprovedAt() { return approvedAt; }
    public void setApprovedAt(LocalDateTime approvedAt) { this.approvedAt = approvedAt; }

    public LocalDateTime getAppliedAt() { return appliedAt; }
    public void setAppliedAt(LocalDateTime appliedAt) { this.appliedAt = appliedAt; }
}
