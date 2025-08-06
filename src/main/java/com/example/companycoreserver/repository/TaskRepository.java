// repository/TaskRepository.java
package com.example.companycoreserver.repository;

import com.example.companycoreserver.entity.Task;
import com.example.companycoreserver.entity.Enum.TaskStatus;
import com.example.companycoreserver.entity.Enum.TaskType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface TaskRepository extends JpaRepository<Task, Integer> {

    // ✅ 특정 사용자가 생성한 작업 조회 (createdBy 사용)
    List<Task> findByCreatedByOrderByCreatedAtDesc(Long createdBy);

    // ✅ 상태별 작업 조회
    List<Task> findByStatusOrderByCreatedAtDesc(TaskStatus status);

    // ✅ 작업 타입별 조회
    List<Task> findByTaskTypeOrderByCreatedAtDesc(TaskType taskType);

    // ✅ 특정 사용자가 생성한 특정 상태 작업 조회
    List<Task> findByCreatedByAndStatusOrderByCreatedAtDesc(Long createdBy, TaskStatus status);

    // ✅ 특정 사용자가 생성한 특정 타입 작업 조회
    List<Task> findByCreatedByAndTaskTypeOrderByCreatedAtDesc(Long createdBy, TaskType taskType);

    // ✅ 날짜 범위로 작업 조회 (시작일 기준)
    @Query("SELECT t FROM Task t WHERE t.startDate >= :startDate AND t.startDate <= :endDate ORDER BY t.startDate ASC")
    List<Task> findByStartDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    // ✅ 마감일 범위로 작업 조회
    @Query("SELECT t FROM Task t WHERE t.endDate >= :startDate AND t.endDate <= :endDate ORDER BY t.endDate ASC")
    List<Task> findByEndDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    // ✅ 특정 사용자가 할당받은 작업들 (TaskAssignment를 통해)
    @Query("SELECT DISTINCT t FROM Task t " +
            "JOIN t.assignments ta " +
            "WHERE ta.user.userId = :userId " +
            "AND ta.status = 'ACTIVE' " +
            "ORDER BY t.createdAt DESC")
    List<Task> findTasksAssignedToUser(@Param("userId") Long userId);

    // ✅ 특정 사용자가 특정 역할로 할당받은 작업들
    @Query("SELECT DISTINCT t FROM Task t " +
            "JOIN t.assignments ta " +
            "WHERE ta.user.userId = :userId " +
            "AND ta.role = :role " +
            "AND ta.status = 'ACTIVE' " +
            "ORDER BY t.createdAt DESC")
    List<Task> findTasksAssignedToUserByRole(@Param("userId") Long userId,
                                             @Param("role") com.example.companycoreserver.entity.Enum.AssignmentRole role);

    // ✅ 특정 사용자의 담당 업무 (ASSIGNEE 역할) - priority 제거
    @Query("SELECT DISTINCT t FROM Task t " +
            "JOIN t.assignments ta " +
            "WHERE ta.user.userId = :userId " +
            "AND ta.role = 'ASSIGNEE' " +
            "AND ta.status = 'ACTIVE' " +
            "ORDER BY t.endDate ASC")
    List<Task> findMyAssignedTasks(@Param("userId") Long userId);

    // ✅ 특정 사용자의 검토 업무 (REVIEWER 역할) - priority 제거
    @Query("SELECT DISTINCT t FROM Task t " +
            "JOIN t.assignments ta " +
            "WHERE ta.user.userId = :userId " +
            "AND ta.role = 'REVIEWER' " +
            "AND ta.status = 'ACTIVE' " +
            "ORDER BY t.endDate ASC")
    List<Task> findMyReviewTasks(@Param("userId") Long userId);

    // ✅ 제목으로 검색
    @Query("SELECT t FROM Task t WHERE t.title LIKE %:keyword% ORDER BY t.createdAt DESC")
    List<Task> findByTitleContaining(@Param("keyword") String keyword);

    // ✅ 제목 또는 설명으로 검색
    @Query("SELECT t FROM Task t WHERE t.title LIKE %:keyword% OR t.description LIKE %:keyword% ORDER BY t.createdAt DESC")
    List<Task> findByTitleOrDescriptionContaining(@Param("keyword") String keyword);

    // ✅ 복합 조건 검색 (priority 제거)
    @Query("SELECT t FROM Task t WHERE " +
            "(:createdBy IS NULL OR t.createdBy = :createdBy) AND " +
            "(:status IS NULL OR t.status = :status) AND " +
            "(:taskType IS NULL OR t.taskType = :taskType) " +
            "ORDER BY t.createdAt DESC")
    List<Task> findByMultipleConditions(@Param("createdBy") Long createdBy,
                                        @Param("status") TaskStatus status,
                                        @Param("taskType") TaskType taskType);

    // ✅ 마감일이 임박한 작업들
    @Query("SELECT t FROM Task t WHERE t.endDate <= :deadlineDate AND t.status != 'COMPLETED' ORDER BY t.endDate ASC")
    List<Task> findTasksWithUpcomingDeadline(@Param("deadlineDate") LocalDate deadlineDate);

    // ✅ 오늘 마감인 작업들 - priority 제거
    @Query("SELECT t FROM Task t WHERE t.endDate = :today AND t.status != 'COMPLETED' ORDER BY t.createdAt DESC")
    List<Task> findTasksDueToday(@Param("today") LocalDate today);

    // ✅ 연체된 작업들
    @Query("SELECT t FROM Task t WHERE t.endDate < :today AND t.status != 'COMPLETED' ORDER BY t.endDate ASC")
    List<Task> findOverdueTasks(@Param("today") LocalDate today);

    // ✅ 특정 기간에 생성된 작업들
    @Query("SELECT t FROM Task t WHERE t.createdAt BETWEEN :startDateTime AND :endDateTime ORDER BY t.createdAt DESC")
    List<Task> findByCreatedAtBetween(@Param("startDateTime") LocalDateTime startDateTime,
                                      @Param("endDateTime") LocalDateTime endDateTime);

    // ✅ 특정 사용자가 생성한 작업 통계
    @Query("SELECT t.status, COUNT(t) FROM Task t WHERE t.createdBy = :createdBy GROUP BY t.status")
    List<Object[]> countTasksByCreatedByAndStatus(@Param("createdBy") Long createdBy);

    // ✅ 작업 타입별 통계
    @Query("SELECT t.taskType, COUNT(t) FROM Task t GROUP BY t.taskType")
    List<Object[]> countTasksByType();

    // ✅ 페이지네이션 - 특정 사용자가 생성한 특정 타입 작업
    @Query("SELECT t FROM Task t WHERE t.createdBy = :createdBy AND t.taskType = :taskType ORDER BY t.createdAt DESC")
    Page<Task> findByCreatedByAndTaskType(@Param("createdBy") Long createdBy,
                                          @Param("taskType") TaskType taskType,
                                          Pageable pageable);

    // ✅ 페이지네이션 - 할당받은 작업들 - priority 제거
    @Query("SELECT DISTINCT t FROM Task t " +
            "JOIN t.assignments ta " +
            "WHERE ta.user.userId = :userId " +
            "AND ta.status = 'ACTIVE' " +
            "ORDER BY t.endDate ASC")
    Page<Task> findTasksAssignedToUser(@Param("userId") Long userId, Pageable pageable);

    // ✅ 페이지네이션 - 상태별 작업 조회
    Page<Task> findByStatusOrderByCreatedAtDesc(TaskStatus status, Pageable pageable);

    // ✅ 완료되지 않은 작업 개수
    @Query("SELECT COUNT(t) FROM Task t WHERE t.status != 'COMPLETED'")
    long countActiveTasks();

    // ✅ 특정 사용자의 완료되지 않은 할당 작업 개수
    @Query("SELECT COUNT(DISTINCT t) FROM Task t " +
            "JOIN t.assignments ta " +
            "WHERE ta.user.userId = :userId " +
            "AND ta.status = 'ACTIVE' " +
            "AND t.status != 'COMPLETED'")
    long countActiveAssignedTasks(@Param("userId") Long userId);
}
