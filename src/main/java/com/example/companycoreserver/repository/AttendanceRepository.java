package com.example.companycoreserver.repository;

import com.example.companycoreserver.entity.Attendance;
import com.example.companycoreserver.entity.Enum.AttendanceStatus;
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

    // ===== 📝 출근/퇴근 처리 관련 메서드 =====

    // 1-2. 출근/퇴근 처리 시 필요한 조회
    Optional<Attendance> findByUserIdAndWorkDate(Long userId, LocalDate workDate);
    Optional<Attendance> findByUserIdAndWorkDateAndCheckOutIsNull(Long userId, LocalDate workDate);

    // ===== 📊 조회 메서드 =====

    // 3. 사용자별 출근 기록 조회 (최신순)
    List<Attendance> findByUserIdOrderByWorkDateDesc(Long userId);

    // 4. 날짜 범위별 출근 기록 조회
    List<Attendance> findByUserIdAndWorkDateBetween(Long userId, LocalDate startDate, LocalDate endDate);

    // 5. 특정 날짜의 모든 출근 기록
    List<Attendance> findByWorkDate(LocalDate workDate);

    // ===== 📈 통계 & 대시보드 관련 메서드 =====

    // 6. 오늘의 출근 현황 대시보드용
    // - 특정 날짜의 특정 상태 출근 기록 조회 (정상출근자, 지각자)
    List<Attendance> findByWorkDateAndStatus(LocalDate workDate, AttendanceStatus status);

    // 최근 5개 출근 기록 조회 (전체)
    List<Attendance> findTop5ByOrderByCheckInDesc();

    // - 미퇴근자 조회
    List<Attendance> findByCheckOutIsNull();

    // 7. 특정 사용자의 출근 통계용
    List<Attendance> findByUserIdAndStatus(Long userId, AttendanceStatus status);

    // 8. 월별 출근 통계
    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.userId = :userId " +
            "AND YEAR(a.workDate) = :year AND MONTH(a.workDate) = :month")
    Long countAttendanceDaysByUserAndMonth(@Param("userId") Long userId, @Param("year") int year, @Param("month") int month);

    @Query(value = "SELECT AVG(TIMESTAMPDIFF(MINUTE, check_in, check_out)) FROM attendance " +
            "WHERE user_id = :userId AND YEAR(work_date) = :year AND MONTH(work_date) = :month " +
            "AND check_out IS NOT NULL",
            nativeQuery = true)
    Double getAverageWorkingMinutesByUserAndMonth(@Param("userId") Long userId, @Param("year") int year, @Param("month") int month);

    // ===== 🔍 상태별 조회 메서드 =====

    // 9. 상태별 출근 기록 조회 (다중 필터링 지원)
    List<Attendance> findByStatus(AttendanceStatus status);

    // 🆕 특정 사용자의 특정 날짜 + 상태 출근 기록 조회 (Controller에서 필요)
    List<Attendance> findByUserIdAndStatusAndWorkDate(Long userId, AttendanceStatus status, LocalDate workDate);

    // 10. 미퇴근 기록 조회
    Optional<Attendance> findByUserIdAndCheckOutIsNull(Long userId);

    // ===== ⚙️ 관리 APIs 관련 메서드 =====
    // 11-13. 기본 CRUD는 JpaRepository에서 제공
    // - save() : 생성/수정
    // - deleteById() : 삭제
    // - findById() : ID로 조회
    // - existsById() : 존재 확인

    // ===== 🛠️ 통계 지원 메서드 =====

    // 사용자별 총 근무시간 조회 (분 단위)
    @Query(value = "SELECT SUM(TIMESTAMPDIFF(MINUTE, check_in, check_out)) FROM attendance " +
            "WHERE user_id = :userId AND work_date BETWEEN :startDate AND :endDate " +
            "AND check_out IS NOT NULL",
            nativeQuery = true)
    Long getTotalWorkingMinutesByUserAndPeriod(@Param("userId") Long userId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    // ===== 🔧 고급 통계 & 분석 메서드 (선택사항) =====

    // 지각 기록 조회 (시간 기준)
    @Query("SELECT a FROM Attendance a WHERE a.checkIn > :lateTime")
    List<Attendance> findLateArrivals(@Param("lateTime") LocalDateTime lateTime);

    // 부서별 출근율 통계
    @Query("SELECT u.department.departmentName, COUNT(a) FROM Attendance a " +
            "JOIN User u ON a.userId = u.userId " +
            "WHERE a.workDate BETWEEN :startDate AND :endDate AND u.isActive = 1 " +
            "GROUP BY u.department.departmentName")
    List<Object[]> findAttendanceStatsByDepartment(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    // 특정 사용자의 월별 근무시간 계산용 데이터
    @Query("SELECT a FROM Attendance a " +
            "WHERE a.userId = :userId AND YEAR(a.workDate) = :year AND MONTH(a.workDate) = :month " +
            "AND a.checkOut IS NOT NULL")
    List<Attendance> findAttendanceForWorkingHoursCalculation(@Param("userId") Long userId, @Param("year") int year, @Param("month") int month);

    // 특정 사용자의 일별 근무시간 조회
    @Query(value = "SELECT work_date, TIMESTAMPDIFF(MINUTE, check_in, check_out) as working_minutes " +
            "FROM attendance WHERE user_id = :userId AND work_date BETWEEN :startDate AND :endDate " +
            "AND check_out IS NOT NULL ORDER BY work_date",
            nativeQuery = true)
    List<Object[]> getDailyWorkingMinutesByUserAndPeriod(@Param("userId") Long userId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    // 여러 상태 중 하나에 해당하는 출근 기록 조회 (향후 확장용)
    List<Attendance> findByStatusIn(List<AttendanceStatus> statuses);
    List<Attendance> findByWorkDateAndStatusIn(LocalDate workDate, List<AttendanceStatus> statuses);

    // 특정 사용자의 특정 날짜, 특정 상태 출근 기록 조회 (단일 결과)
    Optional<Attendance> findByUserIdAndWorkDateAndStatus(Long userId, LocalDate workDate, AttendanceStatus status);
}
