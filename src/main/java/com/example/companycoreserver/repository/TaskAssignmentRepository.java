package com.example.companycoreserver.repository;

import com.example.companycoreserver.entity.TaskAssignment;
import com.example.companycoreserver.entity.Enum.AssignmentRole;
import com.example.companycoreserver.entity.Enum.AssignmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TaskAssignmentRepository extends JpaRepository<TaskAssignment, Long> {

    // ✅ 기본 조회 메서드들

    /**
     * 특정 작업의 모든 할당 정보 조회
     */
    List<TaskAssignment> findByTaskIdOrderByAssignedAtDesc(Long taskId);

    /**
     * 특정 사용자의 모든 할당 정보 조회
     */
    List<TaskAssignment> findByUserIdOrderByAssignedAtDesc(Long userId);

    /**
     * 특정 사용자가 할당한 모든 할당 정보 조회
     */
    List<TaskAssignment> findByAssignedByOrderByAssignedAtDesc(Long assignedBy);

    /**
     * 상태별 할당 정보 조회
     */
    List<TaskAssignment> findByStatusOrderByAssignedAtDesc(AssignmentStatus status);

    /**
     * 역할별 할당 정보 조회
     */
    List<TaskAssignment> findByRoleOrderByAssignedAtDesc(AssignmentRole role);

    // ✅ 복합 조건 조회 메서드들

    /**
     * 특정 작업의 특정 상태 할당 정보 조회
     */
    List<TaskAssignment> findByTaskIdAndStatusOrderByAssignedAtDesc(Long taskId, AssignmentStatus status);

    /**
     * 특정 작업의 특정 역할 할당 정보 조회
     */
    List<TaskAssignment> findByTaskIdAndRoleOrderByAssignedAtDesc(Long taskId, AssignmentRole role);

    /**
     * 특정 사용자의 특정 상태 할당 정보 조회
     */
    List<TaskAssignment> findByUserIdAndStatusOrderByAssignedAtDesc(Long userId, AssignmentStatus status);

    /**
     * 특정 사용자의 특정 역할 할당 정보 조회
     */
    List<TaskAssignment> findByUserIdAndRoleOrderByAssignedAtDesc(Long userId, AssignmentRole role);

    /**
     * 특정 작업에서 특정 사용자의 할당 정보 조회
     */
    Optional<TaskAssignment> findByTaskIdAndUserId(Long taskId, Long userId);

    /**
     * 특정 작업에서 특정 사용자의 활성 할당 정보 조회
     */
    Optional<TaskAssignment> findByTaskIdAndUserIdAndStatus(Long taskId, Long userId, AssignmentStatus status);

    // ✅ 활성 할당 관련 메서드들

    /**
     * 특정 작업의 활성 할당자들 조회
     */
    List<TaskAssignment> findByTaskIdAndStatus(Long taskId, AssignmentStatus status);

    /**
     * 특정 사용자의 활성 할당 작업들 조회
     */
    List<TaskAssignment> findByUserIdAndStatus(Long userId, AssignmentStatus status);

    /**
     * 특정 작업의 주 담당자 조회 (ASSIGNEE 역할)
     */
    @Query("SELECT ta FROM TaskAssignment ta WHERE ta.taskId = :taskId AND ta.role = 'ASSIGNEE' AND ta.status = 'ACTIVE'")
    List<TaskAssignment> findMainAssigneesByTaskId(@Param("taskId") Long taskId);

    /**
     * 특정 작업의 검토자들 조회 (REVIEWER 역할)
     */
    @Query("SELECT ta FROM TaskAssignment ta WHERE ta.taskId = :taskId AND ta.role = 'REVIEWER' AND ta.status = 'ACTIVE'")
    List<TaskAssignment> findReviewersByTaskId(@Param("taskId") Long taskId);

    // ✅ 통계 및 카운트 메서드들

    /**
     * 특정 작업의 할당자 수 조회
     */
    long countByTaskIdAndStatus(Long taskId, AssignmentStatus status);

    /**
     * 특정 사용자의 할당된 작업 수 조회
     */
    long countByUserIdAndStatus(Long userId, AssignmentStatus status);

    /**
     * 특정 사용자가 특정 역할로 할당된 작업 수
     */
    long countByUserIdAndRoleAndStatus(Long userId, AssignmentRole role, AssignmentStatus status);

    /**
     * 특정 작업의 역할별 할당자 수
     */
    long countByTaskIdAndRoleAndStatus(Long taskId, AssignmentRole role, AssignmentStatus status);

    // ✅ 페이징 조회 메서드들

    /**
     * 특정 사용자의 할당 정보 페이징 조회
     */
    Page<TaskAssignment> findByUserIdOrderByAssignedAtDesc(Long userId, Pageable pageable);

    /**
     * 특정 작업의 할당 정보 페이징 조회
     */
    Page<TaskAssignment> findByTaskIdOrderByAssignedAtDesc(Long taskId, Pageable pageable);

    // ✅ 조인을 활용한 복잡한 조회

    /**
     * 할당 정보와 작업, 사용자 정보를 함께 조회
     */
    @Query("SELECT ta FROM TaskAssignment ta " +
            "LEFT JOIN FETCH ta.task " +
            "LEFT JOIN FETCH ta.user " +
            "LEFT JOIN FETCH ta.assignedByUser " +
            "WHERE ta.assignmentId = :assignmentId")
    Optional<TaskAssignment> findAssignmentWithDetails(@Param("assignmentId") Long assignmentId);

    /**
     * 특정 사용자의 할당 정보를 작업 정보와 함께 조회
     */
    @Query("SELECT ta FROM TaskAssignment ta " +
            "LEFT JOIN FETCH ta.task t " +
            "WHERE ta.userId = :userId AND ta.status = :status " +
            "ORDER BY ta.assignedAt DESC")
    List<TaskAssignment> findUserAssignmentsWithTask(@Param("userId") Long userId,
                                                     @Param("status") AssignmentStatus status);

    // ✅ 날짜 기반 조회 메서드들

    /**
     * 특정 기간에 할당된 작업들 조회
     */
    List<TaskAssignment> findByAssignedAtBetweenOrderByAssignedAtDesc(LocalDateTime startDateTime,
                                                                      LocalDateTime endDateTime);

    /**
     * 특정 기간에 완료된 할당 작업들 조회
     */
    List<TaskAssignment> findByCompletedAtBetweenOrderByCompletedAtDesc(LocalDateTime startDateTime,
                                                                        LocalDateTime endDateTime);

    /**
     * 최근 완료된 할당 작업들 조회
     */
    List<TaskAssignment> findByStatusAndCompletedAtIsNotNullOrderByCompletedAtDesc(AssignmentStatus status,
                                                                                   Pageable pageable);

    // ✅ 존재 여부 확인 메서드들

    /**
     * 특정 작업에 특정 사용자가 이미 할당되어 있는지 확인
     */
    boolean existsByTaskIdAndUserIdAndStatus(Long taskId, Long userId, AssignmentStatus status);

    /**
     * 특정 작업에 활성 할당자가 있는지 확인
     */
    boolean existsByTaskIdAndStatus(Long taskId, AssignmentStatus status);

    /**
     * 특정 사용자에게 활성 할당된 작업이 있는지 확인
     */
    boolean existsByUserIdAndStatus(Long userId, AssignmentStatus status);

    // ✅ 대시보드용 조회 메서드들

    /**
     * 특정 사용자의 최근 할당 작업들 (제한된 개수)
     */
    @Query("SELECT ta FROM TaskAssignment ta " +
            "LEFT JOIN FETCH ta.task " +
            "WHERE ta.userId = :userId AND ta.status = 'ACTIVE' " +
            "ORDER BY ta.assignedAt DESC")
    List<TaskAssignment> findRecentAssignmentsByUser(@Param("userId") Long userId, Pageable pageable);

    /**
     * 팀별 할당 현황 조회 (부서별 통계용)
     */
    @Query("SELECT ta FROM TaskAssignment ta " +
            "LEFT JOIN ta.user u " +
            "WHERE u.department = :department AND ta.status = 'ACTIVE' " +
            "ORDER BY ta.assignedAt DESC")
    List<TaskAssignment> findAssignmentsByDepartment(@Param("department") String department);

    // ✅ 삭제 관련 메서드들

    /**
     * 특정 작업의 모든 할당 정보 삭제
     */
    void deleteByTaskId(Long taskId);

    /**
     * 특정 작업에서 특정 사용자의 할당 정보 삭제
     */
    void deleteByTaskIdAndUserId(Long taskId, Long userId);

    /**
     * 비활성 할당 정보들 삭제
     */
    void deleteByStatus(AssignmentStatus status);
}
