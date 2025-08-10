package com.example.companycoreserver.repository;

import com.example.companycoreserver.entity.LeaveRequest;
import com.example.companycoreserver.entity.Enum.LeaveStatus;
import com.example.companycoreserver.entity.Enum.LeaveType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Integer> {

    // ✅ 사용자별 휴가 조회
    List<LeaveRequest> findByUserId(Long userId);

    // ✅ 상태별 휴가 조회
    List<LeaveRequest> findByStatus(LeaveStatus status);

    // ✅ 휴가 타입별 조회
    List<LeaveRequest> findByLeaveType(LeaveType leaveType);

    // ✅ 특정 사용자의 특정 상태 휴가들
    List<LeaveRequest> findByUserIdAndStatus(Long userId, LeaveStatus status);

    // ✅ 특정 사용자의 특정 타입 휴가들
    List<LeaveRequest> findByUserIdAndLeaveType(Long userId, LeaveType leaveType);

    // ✅ 기간별 휴가 조회 (휴가 시작일 기준)
    List<LeaveRequest> findByStartDateBetween(LocalDate startDate, LocalDate endDate);

    // ✅ 신청일 기준 조회 (appliedAt 사용)
    List<LeaveRequest> findByAppliedAtBetween(LocalDateTime startDateTime, LocalDateTime endDateTime);

    // ✅ 승인 대기중인 모든 휴가들 (신청일 오름차순)
    List<LeaveRequest> findByStatusOrderByAppliedAtAsc(LeaveStatus status);

    // ✅ 특정 기간 동안 휴가 중인 사용자들
    @Query("SELECT l FROM LeaveRequest l WHERE l.status = :status " +
            "AND l.startDate <= :endDate AND l.endDate >= :startDate")
    List<LeaveRequest> findApprovedLeavesInPeriod(@Param("status") LeaveStatus status,
                                                  @Param("startDate") LocalDate startDate,
                                                  @Param("endDate") LocalDate endDate);

    // ✅ 특정 사용자의 연간 사용 휴가 일수 (DATEDIFF 사용)
    @Query("SELECT SUM(DATEDIFF(l.endDate, l.startDate) + 1) FROM LeaveRequest l " +
            "WHERE l.userId = :userId AND l.status = :status " +
            "AND YEAR(l.startDate) = :year")
    Long getTotalUsedLeaveDaysByUserAndYear(@Param("userId") Long userId,
                                            @Param("status") LeaveStatus status,
                                            @Param("year") int year);

    // ✅ 부서별 휴가 사용 통계
    @Query("SELECT u.department.departmentName, COUNT(l), " +
            "SUM(DATEDIFF(l.endDate, l.startDate) + 1) FROM LeaveRequest l " +
            "JOIN l.user u " +
            "WHERE l.status = :status " +
            "GROUP BY u.department.departmentName")
    List<Object[]> findLeaveStatsByDepartment(@Param("status") LeaveStatus status);

    // ✅ 월별 휴가 신청 수
    @Query("SELECT MONTH(l.appliedAt), COUNT(l) FROM LeaveRequest l " +
            "WHERE YEAR(l.appliedAt) = :year " +
            "GROUP BY MONTH(l.appliedAt) " +
            "ORDER BY MONTH(l.appliedAt)")
    List<Object[]> findLeaveApplicationsByMonth(@Param("year") int year);

    // ✅ 특정 부서의 승인 대기중인 휴가들
    @Query("SELECT l FROM LeaveRequest l JOIN l.user u " +
            "WHERE u.department.departmentId = :departmentId AND l.status = :status " +
            "ORDER BY l.appliedAt ASC")
    List<LeaveRequest> findPendingLeavesByDepartment(@Param("departmentId") Long departmentId,
                                                     @Param("status") LeaveStatus status);

    // 🆕 특정 사용자의 최근 휴가 신청들
    List<LeaveRequest> findByUserIdOrderByAppliedAtDesc(Long userId);

    // 🆕 오늘 시작하는 휴가들
    List<LeaveRequest> findByStartDateAndStatus(LocalDate startDate, LeaveStatus status);

    // 🆕 특정 승인자가 승인한 휴가들
    List<LeaveRequest> findByApprovedByOrderByApprovedAtDesc(Long approvedBy);

    // 🆕 특정 기간 내 신청된 휴가 수
    @Query("SELECT COUNT(l) FROM LeaveRequest l " +
            "WHERE DATE(l.appliedAt) BETWEEN :startDate AND :endDate")
    Long countLeaveRequestsByPeriod(@Param("startDate") LocalDate startDate,
                                    @Param("endDate") LocalDate endDate);

    // 🆕 특정 사용자의 특정 연도 휴가 목록
    @Query("SELECT l FROM LeaveRequest l WHERE l.userId = :userId " +
            "AND YEAR(l.startDate) = :year ORDER BY l.startDate ASC")
    List<LeaveRequest> findByUserIdAndYear(@Param("userId") Long userId, @Param("year") int year);

    // 🆕 휴가 타입별 통계 (연도별)
    @Query("SELECT l.leaveType, COUNT(l), SUM(DATEDIFF(l.endDate, l.startDate) + 1) " +
            "FROM LeaveRequest l WHERE l.status = :status AND YEAR(l.startDate) = :year " +
            "GROUP BY l.leaveType")
    List<Object[]> findLeaveStatsByTypeAndYear(@Param("status") LeaveStatus status,
                                               @Param("year") int year);

    // 🆕 현재 휴가 중인 사용자들
    @Query("SELECT l FROM LeaveRequest l WHERE l.status = :status " +
            "AND :currentDate BETWEEN l.startDate AND l.endDate")
    List<LeaveRequest> findCurrentLeaves(@Param("status") LeaveStatus status,
                                         @Param("currentDate") LocalDate currentDate);

    // 🆕 승인 대기 시간이 긴 휴가들 (3일 이상)
    @Query("SELECT l FROM LeaveRequest l WHERE l.status = :status " +
            "AND DATEDIFF(CURRENT_DATE, DATE(l.appliedAt)) >= :days " +
            "ORDER BY l.appliedAt ASC")
    List<LeaveRequest> findLongPendingLeaves(@Param("status") LeaveStatus status,
                                             @Param("days") int days);

    // 🆕 특정 사용자의 남은 휴가일수 계산을 위한 사용된 휴가일수
    @Query("SELECT COALESCE(SUM(DATEDIFF(l.endDate, l.startDate) + 1), 0) " +
            "FROM LeaveRequest l WHERE l.userId = :userId " +
            "AND l.status = :status AND YEAR(l.startDate) = :year")
    Long getUsedLeaveDaysForUser(@Param("userId") Long userId,
                                 @Param("status") LeaveStatus status,
                                 @Param("year") int year);
}
