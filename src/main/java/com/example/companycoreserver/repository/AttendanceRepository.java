package com.example.companycoreserver.repository;

import com.example.companycoreserver.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    // 특정 사용자의 특정 날짜 출근 기록
    Optional<Attendance> findByUserIdAndWorkDate(Long userId, LocalDate workDate);

    // 사용자별 출근 기록 조회
    List<Attendance> findByUserId(Long userId);

    // 기간별 출근 기록 조회
    List<Attendance> findByUserIdAndWorkDateBetween(Long userId, LocalDate startDate, LocalDate endDate);

    // 특정 날짜의 모든 출근 기록
    List<Attendance> findByWorkDate(LocalDate workDate);

    // 아직 퇴근하지 않은 기록들
    List<Attendance> findByCheckOutIsNull();

    // 특정 사용자의 미퇴근 기록
    Optional<Attendance> findByUserIdAndCheckOutIsNull(Long userId);

    // 지각 기록 조회 (9시 이후 출근)
    @Query("SELECT a FROM Attendance a WHERE a.checkIn > :lateTime")
    List<Attendance> findLateArrivals(@Param("lateTime") LocalDateTime lateTime);

    // 특정 사용자의 월별 출근 일수
    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.userId = :userId " +
            "AND YEAR(a.workDate) = :year AND MONTH(a.workDate) = :month")
    Long countAttendanceDaysByUserAndMonth(@Param("userId") Long userId, @Param("year") int year, @Param("month") int month);

    // 부서별 출근율 통계
    @Query("SELECT u.department.departmentName, COUNT(a) FROM Attendance a " +
            "JOIN User u ON a.userId = u.userId " +
            "WHERE a.workDate BETWEEN :startDate AND :endDate AND u.isActive = 1 " +
            "GROUP BY u.department.departmentName")
    List<Object[]> findAttendanceStatsByDepartment(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    // 🔧 수정: JPA 호환 방식으로 근무시간 계산
    // 방법 1: 시간 단위로 계산 (소수점 포함)
    @Query("SELECT a FROM Attendance a " +
            "WHERE a.userId = :userId AND YEAR(a.workDate) = :year AND MONTH(a.workDate) = :month " +
            "AND a.checkOut IS NOT NULL")
    List<Attendance> findAttendanceForWorkingHoursCalculation(@Param("userId") Long userId, @Param("year") int year, @Param("month") int month);

    // 방법 2: Native Query 사용 (MySQL 전용)
    @Query(value = "SELECT AVG(TIMESTAMPDIFF(HOUR, check_in, check_out)) FROM attendance " +
            "WHERE user_id = :userId AND YEAR(work_date) = :year AND MONTH(work_date) = :month " +
            "AND check_out IS NOT NULL",
            nativeQuery = true)
    Double getAverageWorkingHoursByUserAndMonth(@Param("userId") Long userId, @Param("year") int year, @Param("month") int month);

    // 방법 3: 분 단위로 더 정확한 계산 (Native Query)
    @Query(value = "SELECT AVG(TIMESTAMPDIFF(MINUTE, check_in, check_out)) FROM attendance " +
            "WHERE user_id = :userId AND YEAR(work_date) = :year AND MONTH(work_date) = :month " +
            "AND check_out IS NOT NULL",
            nativeQuery = true)
    Double getAverageWorkingMinutesByUserAndMonth(@Param("userId") Long userId, @Param("year") int year, @Param("month") int month);

    // 최근 출근 기록들 (최신순)
    List<Attendance> findByUserIdOrderByWorkDateDesc(Long userId);

    // 🆕 추가: 특정 사용자의 총 근무시간 (분 단위)
    @Query(value = "SELECT SUM(TIMESTAMPDIFF(MINUTE, check_in, check_out)) FROM attendance " +
            "WHERE user_id = :userId AND work_date BETWEEN :startDate AND :endDate " +
            "AND check_out IS NOT NULL",
            nativeQuery = true)
    Long getTotalWorkingMinutesByUserAndPeriod(@Param("userId") Long userId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    // 🆕 추가: 일별 근무시간 조회
    @Query(value = "SELECT work_date, TIMESTAMPDIFF(MINUTE, check_in, check_out) as working_minutes " +
            "FROM attendance WHERE user_id = :userId AND work_date BETWEEN :startDate AND :endDate " +
            "AND check_out IS NOT NULL ORDER BY work_date",
            nativeQuery = true)
    List<Object[]> getDailyWorkingMinutesByUserAndPeriod(@Param("userId") Long userId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
