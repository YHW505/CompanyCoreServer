package com.example.companycoreserver.repository;

import com.example.companycoreserver.entity.Task;
import com.example.companycoreserver.entity.Enum.AssignmentRole;
import com.example.companycoreserver.entity.Enum.AssignmentStatus;
import com.example.companycoreserver.entity.Enum.TaskStatus;
import com.example.companycoreserver.entity.Enum.TaskType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    // ✅ 기본 조회 메서드들 (TaskAssignment 조인 방식으로 변경)

    /**
     * 특정 사용자에게 할당된 작업 목록 조회 (TaskAssignment를 통해)
     */
    @Query("SELECT DISTINCT t FROM Task t " +
            "JOIN t.assignments ta " +
            "WHERE ta.userId = :userId " +
            "AND ta.status = 'ACTIVE' " +
            "ORDER BY t.createdAt DESC")
    List<Task> findTasksAssignedToUser(@Param("userId") Long userId);

    /**
     * 특정 사용자에게 할당된 특정 역할의 작업 조회
     */
    @Query("SELECT DISTINCT t FROM Task t " +
            "JOIN t.assignments ta " +
            "WHERE ta.userId = :userId " +
            "AND ta.status = 'ACTIVE' " +
            "AND ta.role = :role " +
            "ORDER BY t.createdAt DESC")
    List<Task> findTasksAssignedToUserByRole(@Param("userId") Long userId,
                                             @Param("role") AssignmentRole role);

    /**
     * 특정 사용자가 할당한 작업 목록 조회
     */
    List<Task> findByAssignedByOrderByCreatedAtDesc(Long assignedBy);

    /**
     * 특정 사용자가 생성한 작업 목록 조회
     */
    List<Task> findByCreatedByOrderByCreatedAtDesc(Long createdBy);

    /**
     * 상태별 작업 목록 조회
     */
    List<Task> findByStatusOrderByCreatedAtDesc(TaskStatus status);

    /**
     * 작업 타입별 조회
     */
    List<Task> findByTaskTypeOrderByCreatedAtDesc(TaskType taskType);

    // ✅ 복합 조건 조회 메서드들 (TaskAssignment 기반)

    /**
     * 특정 사용자의 특정 상태 작업 조회
     */
    @Query("SELECT DISTINCT t FROM Task t " +
            "JOIN t.assignments ta " +
            "WHERE ta.userId = :userId " +
            "AND ta.status = 'ACTIVE' " +
            "AND t.status = :taskStatus " +
            "ORDER BY t.createdAt DESC")
    List<Task> findUserTasksByStatus(@Param("userId") Long userId,
                                     @Param("taskStatus") TaskStatus taskStatus);

    /**
     * 특정 사용자의 특정 타입 작업 조회
     */
    @Query("SELECT DISTINCT t FROM Task t " +
            "JOIN t.assignments ta " +
            "WHERE ta.userId = :userId " +
            "AND ta.status = 'ACTIVE' " +
            "AND t.taskType = :taskType " +
            "ORDER BY t.createdAt DESC")
    List<Task> findUserTasksByType(@Param("userId") Long userId,
                                   @Param("taskType") TaskType taskType);

    /**
     * 할당자와 담당자가 같은 작업 조회 (자기 자신에게 할당한 작업)
     */
    @Query("SELECT DISTINCT t FROM Task t " +
            "JOIN t.assignments ta " +
            "WHERE t.assignedBy = ta.userId " +
            "AND ta.userId = :userId " +
            "AND ta.status = 'ACTIVE'")
    List<Task> findSelfAssignedTasks(@Param("userId") Long userId);

    // ✅ 날짜 기반 조회 메서드들

    /**
     * 특정 날짜 범위의 작업 조회
     */
    @Query("SELECT t FROM Task t WHERE t.startDate >= :startDate AND t.endDate <= :endDate ORDER BY t.startDate")
    List<Task> findTasksByDateRange(@Param("startDate") LocalDate startDate,
                                    @Param("endDate") LocalDate endDate);

    /**
     * 특정 사용자의 특정 날짜 범위 작업 조회
     */
    @Query("SELECT DISTINCT t FROM Task t " +
            "JOIN t.assignments ta " +
            "WHERE ta.userId = :userId " +
            "AND ta.status = 'ACTIVE' " +
            "AND t.startDate >= :startDate " +
            "AND t.endDate <= :endDate " +
            "ORDER BY t.startDate")
    List<Task> findUserTasksByDateRange(@Param("userId") Long userId,
                                        @Param("startDate") LocalDate startDate,
                                        @Param("endDate") LocalDate endDate);

    /**
     * 오늘 시작하는 작업 조회
     */
    List<Task> findByStartDateOrderByCreatedAtDesc(LocalDate startDate);

    /**
     * 오늘 마감인 작업 조회
     */
    List<Task> findByEndDateOrderByEndDateAsc(LocalDate endDate);

    /**
     * 특정 사용자의 오늘 마감 작업 조회
     */
    @Query("SELECT DISTINCT t FROM Task t " +
            "JOIN t.assignments ta " +
            "WHERE ta.userId = :userId " +
            "AND ta.status = 'ACTIVE' " +
            "AND t.endDate = :endDate " +
            "ORDER BY t.endDate ASC")
    List<Task> findUserTasksByEndDate(@Param("userId") Long userId,
                                      @Param("endDate") LocalDate endDate);

    /**
     * 마감일이 지난 작업 조회 (연체 작업)
     */
    @Query("SELECT t FROM Task t WHERE t.endDate < :currentDate AND t.status != 'DONE' AND t.status != 'CANCELLED'")
    List<Task> findOverdueTasks(@Param("currentDate") LocalDate currentDate);

    /**
     * 특정 사용자의 연체 작업 조회
     */
    @Query("SELECT DISTINCT t FROM Task t " +
            "JOIN t.assignments ta " +
            "WHERE ta.userId = :userId " +
            "AND ta.status = 'ACTIVE' " +
            "AND t.endDate < :currentDate " +
            "AND t.status NOT IN ('DONE', 'CANCELLED')")
    List<Task> findUserOverdueTasks(@Param("userId") Long userId,
                                    @Param("currentDate") LocalDate currentDate);

    // ✅ 페이징 조회 메서드들

    /**
     * 특정 사용자 작업 페이징 조회
     */
    @Query("SELECT DISTINCT t FROM Task t " +
            "JOIN t.assignments ta " +
            "WHERE ta.userId = :userId " +
            "AND ta.status = 'ACTIVE' " +
            "ORDER BY t.createdAt DESC")
    Page<Task> findTasksAssignedToUser(@Param("userId") Long userId, Pageable pageable);

    /**
     * 상태별 작업 페이징 조회
     */
    Page<Task> findByStatusOrderByCreatedAtDesc(TaskStatus status, Pageable pageable);

    // ✅ 통계 및 카운트 메서드들

    /**
     * 특정 사용자의 상태별 작업 개수
     */
    @Query("SELECT COUNT(DISTINCT t) FROM Task t " +
            "JOIN t.assignments ta " +
            "WHERE ta.userId = :userId " +
            "AND ta.status = 'ACTIVE' " +
            "AND t.status = :taskStatus")
    long countUserTasksByStatus(@Param("userId") Long userId,
                                @Param("taskStatus") TaskStatus taskStatus);

    /**
     * 특정 사용자의 전체 작업 개수
     */
    @Query("SELECT COUNT(DISTINCT t) FROM Task t " +
            "JOIN t.assignments ta " +
            "WHERE ta.userId = :userId " +
            "AND ta.status = 'ACTIVE'")
    long countUserTasks(@Param("userId") Long userId);

    /**
     * 특정 날짜 이후 생성된 작업 개수
     */
    long countByCreatedAtAfter(LocalDateTime createdAt);

    // ✅ 복잡한 검색 쿼리들

    /**
     * 키워드로 작업 검색 (제목, 설명에서 검색)
     */
    @Query("SELECT t FROM Task t WHERE " +
            "(LOWER(t.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(t.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "ORDER BY t.createdAt DESC")
    List<Task> searchTasksByKeyword(@Param("keyword") String keyword);

    /**
     * 특정 사용자의 키워드 검색
     */
    @Query("SELECT DISTINCT t FROM Task t " +
            "JOIN t.assignments ta " +
            "WHERE ta.userId = :userId " +
            "AND ta.status = 'ACTIVE' " +
            "AND (LOWER(t.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(t.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "ORDER BY t.createdAt DESC")
    List<Task> searchUserTasksByKeyword(@Param("userId") Long userId,
                                        @Param("keyword") String keyword);

    // ✅ 관계 조인을 활용한 조회

    /**
     * 작업과 할당자 정보를 함께 조회 (assignedToUser 제거)
     */
    @Query("SELECT t FROM Task t " +
            "LEFT JOIN FETCH t.assignedByUser " +
            "LEFT JOIN FETCH t.createdByUser " +
            "WHERE t.taskId = :taskId")
    Optional<Task> findTaskWithUsers(@Param("taskId") Long taskId);

    /**
     * 작업과 모든 할당 정보를 함께 조회
     */
    @Query("SELECT t FROM Task t " +
            "LEFT JOIN FETCH t.assignments ta " +
            "LEFT JOIN FETCH ta.user " +
            "WHERE t.taskId = :taskId")
    Optional<Task> findTaskWithAssignments(@Param("taskId") Long taskId);

    /**
     * 특정 부서의 모든 작업 조회 (TaskAssignment를 통해)
     */
    @Query("SELECT DISTINCT t FROM Task t " +
            "JOIN t.assignments ta " +
            "JOIN ta.user u " +
            "WHERE u.department = :department " +
            "AND ta.status = 'ACTIVE' " +
            "ORDER BY t.createdAt DESC")
    List<Task> findTasksByDepartment(@Param("department") String department);

    // ✅ 대시보드용 조회 메서드들

    /**
     * 사용자의 최근 작업 목록 (제한된 개수)
     */
    @Query("SELECT DISTINCT t FROM Task t " +
            "JOIN t.assignments ta " +
            "WHERE ta.userId = :userId " +
            "AND ta.status = 'ACTIVE' " +
            "ORDER BY t.updatedAt DESC")
    List<Task> findRecentTasksByUser(@Param("userId") Long userId, Pageable pageable);

    /**
     * 긴급/중요 작업 조회 (마감일 기준)
     */
    @Query("SELECT DISTINCT t FROM Task t " +
            "JOIN t.assignments ta " +
            "WHERE ta.userId = :userId " +
            "AND ta.status = 'ACTIVE' " +
            "AND t.endDate BETWEEN :startDate AND :endDate " +
            "AND t.status IN ('TODO', 'IN_PROGRESS') " +
            "ORDER BY t.endDate ASC")
    List<Task> findUrgentTasks(@Param("userId") Long userId,
                               @Param("startDate") LocalDate startDate,
                               @Param("endDate") LocalDate endDate);

    // ✅ 존재 여부 확인 메서드들

    /**
     * 특정 사용자에게 할당된 진행중인 작업이 있는지 확인
     */
    @Query("SELECT COUNT(DISTINCT t) > 0 FROM Task t " +
            "JOIN t.assignments ta " +
            "WHERE ta.userId = :userId " +
            "AND ta.status = 'ACTIVE' " +
            "AND t.status IN :statuses")
    boolean existsUserTasksWithStatuses(@Param("userId") Long userId,
                                        @Param("statuses") List<TaskStatus> statuses);

    /**
     * 특정 기간에 생성된 작업이 있는지 확인
     */
    boolean existsByCreatedAtBetween(LocalDateTime startDateTime, LocalDateTime endDateTime);

    // ✅ 새로운 역할별 조회 메서드들

    /**
     * 메인 담당자로 할당된 작업들 조회
     */
    @Query("SELECT DISTINCT t FROM Task t " +
            "JOIN t.assignments ta " +
            "WHERE ta.userId = :userId " +
            "AND ta.status = 'ACTIVE' " +
            "AND ta.role = 'ASSIGNEE' " +
            "ORDER BY t.createdAt DESC")
    List<Task> findMainAssignedTasks(@Param("userId") Long userId);

    /**
     * 리뷰어로 할당된 작업들 조회
     */
    @Query("SELECT DISTINCT t FROM Task t " +
            "JOIN t.assignments ta " +
            "WHERE ta.userId = :userId " +
            "AND ta.status = 'ACTIVE' " +
            "AND ta.role = 'REVIEWER' " +
            "ORDER BY t.createdAt DESC")
    List<Task> findReviewTasks(@Param("userId") Long userId);

    /**
     * 옵저버로 할당된 작업들 조회
     */
    @Query("SELECT DISTINCT t FROM Task t " +
            "JOIN t.assignments ta " +
            "WHERE ta.userId = :userId " +
            "AND ta.status = 'ACTIVE' " +
            "AND ta.role = 'OBSERVER' " +
            "ORDER BY t.createdAt DESC")
    List<Task> findObservedTasks(@Param("userId") Long userId);
}
