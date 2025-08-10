package com.example.companycoreserver.entity;

import com.example.companycoreserver.entity.Enum.ScheduleType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "schedules")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Schedule {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_id")
    private Integer scheduleId;

    @Column(name = "user_id")
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "schedule_type")
    private ScheduleType scheduleType;

    // 나머지 필드들...
    @Column(name = "title", length = 255)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "attendees", columnDefinition = "TEXT")
    private String attendees;

    @Column(name = "start_datetime")
    private LocalDateTime startDatetime;

    @Column(name = "end_datetime")
    private LocalDateTime endDatetime;

    @Column(name = "meeting_minutes", columnDefinition = "TEXT")
    private String meetingMinutes;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    // 🔗 관계 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    // 기본 생성자
    public Schedule() {}

    // 생성자
    public Schedule(Long userId, ScheduleType scheduleType, String title, String description,
                    String attendees, LocalDateTime startDatetime, LocalDateTime endDatetime) {
        this.userId = userId;
        this.scheduleType = scheduleType;
        this.title = title;
        this.description = description;
        this.attendees = attendees;
        this.startDatetime = startDatetime;
        this.endDatetime = endDatetime;
    }

    // Getter/Setter
    public Integer getScheduleId() { return scheduleId; }
    public void setScheduleId(Integer scheduleId) { this.scheduleId = scheduleId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public ScheduleType getScheduleType() { return scheduleType; }
    public void setScheduleType(ScheduleType scheduleType) { this.scheduleType = scheduleType; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getAttendees() { return attendees; }
    public void setAttendees(String attendees) { this.attendees = attendees; }

    public LocalDateTime getStartDatetime() { return startDatetime; }
    public void setStartDatetime(LocalDateTime startDatetime) { this.startDatetime = startDatetime; }

    public LocalDateTime getEndDatetime() { return endDatetime; }
    public void setEndDatetime(LocalDateTime endDatetime) { this.endDatetime = endDatetime; }

    public String getMeetingMinutes() { return meetingMinutes; }
    public void setMeetingMinutes(String meetingMinutes) { this.meetingMinutes = meetingMinutes; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "Schedule{" +
                "scheduleId=" + scheduleId +
                ", userId=" + userId +
                ", scheduleType=" + scheduleType +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", attendees='" + attendees + '\'' +
                ", startDatetime=" + startDatetime +
                ", endDatetime=" + endDatetime +
                ", meetingMinutes='" + meetingMinutes + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
