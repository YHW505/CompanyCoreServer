package com.example.companycoreserver.entity;

import com.example.companycoreserver.entity.Enum.AttendanceStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "attendances")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Attendance {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attendance_id")
    private Integer attendanceId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "check_in")
    private LocalDateTime checkIn;

    @Column(name = "check_out")
    private LocalDateTime checkOut;

    @Column(name = "work_hours", precision = 5, scale = 2)
    private BigDecimal workHours;

    @Column(name = "work_date")
    private LocalDate workDate;

    // AttendanceStatus enum 사용 (필요시 추가)
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private AttendanceStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    // 기본 생성자
    public Attendance() {}

    // 생성자
    public Attendance(Long userId, LocalDateTime checkIn, LocalDateTime checkOut,
                      BigDecimal workHours, LocalDate workDate) {
        this.userId = userId;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.workHours = workHours;
        this.workDate = workDate;
    }

    // Getter/Setter
    public Integer getAttendanceId() { return attendanceId; }
    public void setAttendanceId(Integer attendanceId) { this.attendanceId = attendanceId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public LocalDateTime getCheckIn() { return checkIn; }
    public void setCheckIn(LocalDateTime checkIn) { this.checkIn = checkIn; }

    public LocalDateTime getCheckOut() { return checkOut; }
    public void setCheckOut(LocalDateTime checkOut) { this.checkOut = checkOut; }

    public BigDecimal getWorkHours() { return workHours; }
    public void setWorkHours(BigDecimal workHours) { this.workHours = workHours; }

    public LocalDate getWorkDate() { return workDate; }
    public void setWorkDate(LocalDate workDate) { this.workDate = workDate; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    @Override
    public String toString() {
        return "Attendance{" +
                "attendanceId=" + attendanceId +
                ", userId=" + userId +
                ", checkIn=" + checkIn +
                ", checkOut=" + checkOut +
                ", workHours=" + workHours +
                ", workDate=" + workDate +
                '}';
    }
}
