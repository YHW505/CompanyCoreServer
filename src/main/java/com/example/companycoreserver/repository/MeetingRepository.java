package com.example.companycoreserver.repository;

import com.example.companycoreserver.entity.Meeting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MeetingRepository extends JpaRepository<Meeting, Long> {

    // 기존 메서드들
    List<Meeting> findByStartTimeBetween(LocalDateTime startDate, LocalDateTime endDate);
    List<Meeting> findByLocationContaining(String location);
    List<Meeting> findByTitleContaining(String title);
    List<Meeting> findAllByOrderByStartTimeDesc();


    // 1. 현재 진행중인 회의 조회
    @Query("SELECT m FROM Meeting m WHERE m.startTime <= :now AND m.endTime >= :now")
    List<Meeting> findCurrentMeetings(@Param("now") LocalDateTime now);

    // 2. 예정된 회의 조회 (미래 회의)
    List<Meeting> findByStartTimeAfterOrderByStartTimeAsc(LocalDateTime now);

    // 3. 완료된 회의 조회 (과거 회의)
    List<Meeting> findByEndTimeBeforeOrderByStartTimeDesc(LocalDateTime now);


    // 5. 오늘의 회의 조회
    @Query("SELECT m FROM Meeting m WHERE DATE(m.startTime) = DATE(:date) ORDER BY m.startTime ASC")
    List<Meeting> findMeetingsByDate(@Param("date") LocalDateTime date);

    // 6. 이번 주 회의 조회
    @Query("SELECT m FROM Meeting m WHERE m.startTime >= :weekStart AND m.startTime < :weekEnd ORDER BY m.startTime ASC")
    List<Meeting> findMeetingsThisWeek(@Param("weekStart") LocalDateTime weekStart,
                                       @Param("weekEnd") LocalDateTime weekEnd);

    // 7. 부서별 회의 조회
    List<Meeting> findByDepartmentOrderByStartTimeDesc(String department);

    // 8. 부서별 현재 진행중인 회의 조회
    @Query("SELECT m FROM Meeting m WHERE m.department = :department AND m.startTime <= :now AND m.endTime >= :now")
    List<Meeting> findCurrentMeetingsByDepartment(@Param("department") String department, @Param("now") LocalDateTime now);

    // 9. 부서별 예정된 회의 조회
    @Query("SELECT m FROM Meeting m WHERE m.department = :department AND m.startTime > :now ORDER BY m.startTime ASC")
    List<Meeting> findUpcomingMeetingsByDepartment(@Param("department") String department, @Param("now") LocalDateTime now);

    // 10. 부서별 완료된 회의 조회
    @Query("SELECT m FROM Meeting m WHERE m.department = :department AND m.endTime < :now ORDER BY m.startTime DESC")
    List<Meeting> findPastMeetingsByDepartment(@Param("department") String department, @Param("now") LocalDateTime now);

}
