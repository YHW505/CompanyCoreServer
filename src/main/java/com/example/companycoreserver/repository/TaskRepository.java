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
public interface TaskRepository extends JpaRepository<Task, Long> {

    // ✅ 특정 사용자가 생성한 작업 조회 (assignedBy 사용)
    List<Task> findByAssignedByOrderByCreatedAtDesc(Long assignedBy);

    // ✅ 상태별 작업 조회 (Enum 타입)
    List<Task> findByStatusOrderByCreatedAtDesc(TaskStatus status);

    // ✅ 작업 타입별 조회 (Enum 타입)
    List<Task> findByTaskTypeOrderByCreatedAtDesc(TaskType taskType);

    // ✅ 특정 사용자가 생성한 특정 상태 작업 조회
    List<Task> findByAssignedByAndStatusOrderByCreatedAtDesc(Long assignedBy, TaskStatus status);

    // ✅ 특정 사용자가 생성한 특정 타입 작업 조회
    List<Task> findByAssignedByAndTaskTypeOrderByCreatedAtDesc(Long assignedBy, TaskType taskType);

    // ✅ 특정 사용자에게 할당된 작업 조회 (assignedTo 사용)
    List<Task> findByAssignedToOrderByCreatedAtDesc(Long assignedTo);

    // ✅ 특정 사용자에게 할당된 특정 상태 작업 조회
    List<Task> findByAssignedToAndStatusOrderByCreatedAtDesc(Long assignedTo, TaskStatus status);

    // ✅ 특정 사용자에게 할당된 특정 타입 작업 조회
    List<Task> findByAssignedToAndTaskTypeOrderByCreatedAtDesc(Long assignedTo, TaskType taskType);

    // ✅ 날짜 범위로 작업 조회 (시작일 기준)
    @Query("SELECT t FROM Task t WHERE t.startDate >= :startDate AND t.startDate <= :endDate ORDER BY t.startDate ASC")
    List<Task> findByStartDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    // ✅ 마감일 범위로 작업 조회
    @Query("SELECT t FROM Task t WHERE t.endDate >= :startDate AND t.endDate <= :endDate ORDER BY t.endDate ASC")
    List<Task> findByEndDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    // ✅ 특정 사용자의 담당 업무
    @Query("SELECT t FROM Task t WHERE t.assignedTo = :userId ORDER BY t.endDate ASC")
    List<Task> findMyAssignedTasks(@Param("userId") Long userId);

    // ✅ 제목으로 검색
    @Query("SELECT t FROM Task t WHERE t.title LIKE %:keyword% ORDER BY t.createdAt DESC")
    List<Task> findByTitleContaining(@Param("keyword") String keyword);

    // ✅ 제목 또는 설명으로 검색
    @Query("SELECT t FROM Task t WHERE t.title LIKE %:keyword% OR t.description LIKE %:keyword% ORDER BY t.createdAt DESC")
    List<Task> findByTitleOrDescriptionContaining(@Param("keyword") String keyword);

    // ✅ 복합 조건 검색 (Enum 타입)
    @Query("SELECT t FROM Task t WHERE " +
            "(:assignedBy IS NULL OR t.assignedBy = :assignedBy) AND " +
            "(:status IS NULL OR t.status = :status) AND " +
            "(:taskType IS NULL OR t.taskType = :taskType) " +
            "ORDER BY t.createdAt DESC")
    List<Task> findByMultipleConditions(@Param("assignedBy") Long assignedBy,
                                        @Param("status") TaskStatus status,
                                        @Param("taskType") TaskType taskType);

    // ✅ 마감일이 임박한 작업들 (DONE 상태 제외) - 간단하게 수정
    @Query("SELECT t FROM Task t WHERE t.endDate <= :deadlineDate AND t.status != com.example.companycoreserver.entity.Enum.TaskStatus.DONE ORDER BY t.endDate ASC")
    List<Task> findTasksWithUpcomingDeadline(@Param("deadlineDate") LocalDate deadlineDate);

    // ✅ 오늘 마감인 작업들 (DONE 상태 제외) - 간단하게 수정
    @Query("SELECT t FROM Task t WHERE t.endDate = :today AND t.status != com.example.companycoreserver.entity.Enum.TaskStatus.DONE ORDER BY t.createdAt DESC")
    List<Task> findTasksDueToday(@Param("today") LocalDate today);

    // ✅ 연체된 작업들 (DONE 상태 제외) - 간단하게 수정
    @Query("SELECT t FROM Task t WHERE t.endDate < :today AND t.status != com.example.companycoreserver.entity.Enum.TaskStatus.DONE ORDER BY t.endDate ASC")
    List<Task> findOverdueTasks(@Param("today") LocalDate today);

    // ✅ 특정 기간에 생성된 작업들
    @Query("SELECT t FROM Task t WHERE t.createdAt BETWEEN :startDateTime AND :endDateTime ORDER BY t.createdAt DESC")
    List<Task> findByCreatedAtBetween(@Param("startDateTime") LocalDateTime startDateTime,
                                      @Param("endDateTime") LocalDateTime endDateTime);

    // ✅ 특정 사용자가 생성한 작업 통계
    @Query("SELECT t.status, COUNT(t) FROM Task t WHERE t.assignedBy = :assignedBy GROUP BY t.status")
    List<Object[]> countTasksByAssignedByAndStatus(@Param("assignedBy") Long assignedBy);

    // ✅ 작업 타입별 통계
    @Query("SELECT t.taskType, COUNT(t) FROM Task t GROUP BY t.taskType")
    List<Object[]> countTasksByType();

    // ✅ 상태별 통계
    @Query("SELECT t.status, COUNT(t) FROM Task t GROUP BY t.status")
    List<Object[]> countTasksByStatus();

    // ✅ 페이지네이션 - 특정 사용자가 생성한 특정 타입 작업
    @Query("SELECT t FROM Task t WHERE t.assignedBy = :assignedBy AND t.taskType = :taskType ORDER BY t.createdAt DESC")
    Page<Task> findByAssignedByAndTaskType(@Param("assignedBy") Long assignedBy,
                                           @Param("taskType") TaskType taskType,
                                           Pageable pageable);

    // ✅ 페이지네이션 - 할당받은 작업들
    @Query("SELECT t FROM Task t WHERE t.assignedTo = :userId ORDER BY t.endDate ASC")
    Page<Task> findTasksAssignedToUser(@Param("userId") Long userId, Pageable pageable);

    // ✅ 페이지네이션 - 상태별 작업 조회
    Page<Task> findByStatusOrderByCreatedAtDesc(TaskStatus status, Pageable pageable);

    // ✅ 페이지네이션 - 타입별 작업 조회
    Page<Task> findByTaskTypeOrderByCreatedAtDesc(TaskType taskType, Pageable pageable);

    // ✅ 완료되지 않은 작업 개수 (DONE 상태 제외) - 간단하게 수정
    @Query("SELECT COUNT(t) FROM Task t WHERE t.status != com.example.companycoreserver.entity.Enum.TaskStatus.DONE")
    long countActiveTasks();

    // ✅ 특정 사용자의 완료되지 않은 할당 작업 개수 - 간단하게 수정
    @Query("SELECT COUNT(t) FROM Task t WHERE t.assignedTo = :userId AND t.status != com.example.companycoreserver.entity.Enum.TaskStatus.DONE")
    long countActiveAssignedTasks(@Param("userId") Long userId);

    // ✅ 특정 사용자가 생성한 완료되지 않은 작업 개수
    @Query("SELECT COUNT(t) FROM Task t WHERE t.assignedBy = :userId AND t.status != com.example.companycoreserver.entity.Enum.TaskStatus.DONE")
    long countActiveCreatedTasks(@Param("userId") Long userId);

    // ✅ 첨부파일이 있는 작업들 조회
    @Query("SELECT t FROM Task t WHERE t.attachmentFilename IS NOT NULL ORDER BY t.createdAt DESC")
    List<Task> findTasksWithAttachments();

    // ✅ 특정 사용자의 첨부파일이 있는 작업들
    @Query("SELECT t FROM Task t WHERE t.assignedTo = :userId AND t.attachmentFilename IS NOT NULL ORDER BY t.createdAt DESC")
    List<Task> findTasksWithAttachmentsByUser(@Param("userId") Long userId);

    // ✅ 특정 날짜에 시작하는 작업들
    List<Task> findByStartDateOrderByCreatedAtDesc(LocalDate startDate);

    // ✅ 특정 날짜에 마감인 작업들
    List<Task> findByEndDateOrderByCreatedAtDesc(LocalDate endDate);

    // ✅ 특정 사용자가 생성하고 특정 사용자에게 할당된 작업들
    List<Task> findByAssignedByAndAssignedToOrderByCreatedAtDesc(Long assignedBy, Long assignedTo);

    // ✅ 최근 생성된 작업들 (상위 N개)
    @Query("SELECT t FROM Task t ORDER BY t.createdAt DESC")
    Page<Task> findRecentTasks(Pageable pageable);

    // ✅ 최근 업데이트된 작업들 (상위 N개)
    @Query("SELECT t FROM Task t ORDER BY t.updatedAt DESC")
    Page<Task> findRecentlyUpdatedTasks(Pageable pageable);
}
