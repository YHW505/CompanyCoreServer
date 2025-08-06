package com.example.companycoreserver.repository;

import com.example.companycoreserver.entity.TaskAssignment;
import com.example.companycoreserver.entity.Enum.AssignmentRole;
import com.example.companycoreserver.entity.Enum.AssignmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TaskAssignmentRepository extends JpaRepository<TaskAssignment, Long> {

    // ✅ 특정 사용자에게 할당된 업무들 (최신순)
    List<TaskAssignment> findByUserUserIdOrderByAssignedAtDesc(Long userId);

    // ✅ 특정 업무의 모든 할당자들 (할당 시간 오름차순)
    List<TaskAssignment> findByTaskTaskIdOrderByAssignedAtAsc(Integer taskId); // Task의 taskId는 Integer

    // ✅ 사용자별 + 상태별 할당 업무
    List<TaskAssignment> findByUserUserIdAndStatusOrderByAssignedAtDesc(Long userId, AssignmentStatus status);

    // ✅ 사용자별 + 역할별 할당 업무
    List<TaskAssignment> findByUserUserIdAndRoleOrderByAssignedAtDesc(Long userId, AssignmentRole role);

    // ✅ 사용자별 + 역할별 + 상태별 할당 업무
    List<TaskAssignment> findByUserUserIdAndRoleAndStatusOrderByAssignedAtDesc(
            Long userId, AssignmentRole role, AssignmentStatus status);

    // ✅ 특정 업무에 특정 사용자가 할당되어 있는지 확인
    boolean existsByTaskTaskIdAndUserUserId(Integer taskId, Long userId);

    // ✅ 특정 업무에 특정 사용자가 특정 역할로 할당되어 있는지 확인
    boolean existsByTaskTaskIdAndUserUserIdAndRole(Integer taskId, Long userId, AssignmentRole role);

    // ✅ 특정 업무에 특정 사용자의 활성 할당 조회
    Optional<TaskAssignment> findByTaskTaskIdAndUserUserIdAndStatus(
            Integer taskId, Long userId, AssignmentStatus status);

    // 🆕 내가 담당자로 할당받은 활성 업무들
    @Query("SELECT ta FROM TaskAssignment ta " +
            "WHERE ta.user.userId = :userId " +
            "AND ta.role = :role " +
            "AND ta.status = :status " +
            "ORDER BY ta.assignedAt DESC")
    List<TaskAssignment> findMyActiveTasksByRole(
            @Param("userId") Long userId,
            @Param("role") AssignmentRole role,
            @Param("status") AssignmentStatus status);

    // 🆕 내가 담당자로 할당받은 활성 업무들 (간편 메서드)
    default List<TaskAssignment> findMyActiveAssignedTasks(Long userId) {
        return findMyActiveTasksByRole(userId, AssignmentRole.ASSIGNEE, AssignmentStatus.ACTIVE);
    }

    // 🆕 내가 검토자로 할당받은 활성 업무들 (간편 메서드)
    default List<TaskAssignment> findMyActiveReviewTasks(Long userId) {
        return findMyActiveTasksByRole(userId, AssignmentRole.REVIEWER, AssignmentStatus.ACTIVE);
    }

    // 🆕 내가 참관자로 할당받은 활성 업무들 (간편 메서드)
    default List<TaskAssignment> findMyActiveObserverTasks(Long userId) {
        return findMyActiveTasksByRole(userId, AssignmentRole.OBSERVER, AssignmentStatus.ACTIVE);
    }

    // ✅ 특정 사용자가 할당한 업무들 (내가 다른 사람에게 할당한 업무)
    List<TaskAssignment> findByAssignedByOrderByAssignedAtDesc(Long assignedBy);

    // ✅ 특정 기간 내 할당된 업무들
    @Query("SELECT ta FROM TaskAssignment ta " +
            "WHERE ta.assignedAt BETWEEN :startDate AND :endDate " +
            "ORDER BY ta.assignedAt DESC")
    List<TaskAssignment> findByAssignedAtBetween(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    // ✅ 특정 업무의 담당자들만 조회
    @Query("SELECT ta FROM TaskAssignment ta " +
            "WHERE ta.task.taskId = :taskId " +
            "AND ta.role = 'ASSIGNEE' " +
            "AND ta.status = 'ACTIVE' " +
            "ORDER BY ta.assignedAt ASC")
    List<TaskAssignment> findActiveAssigneesByTaskId(@Param("taskId") Integer taskId);

    // ✅ 특정 업무의 검토자들만 조회
    @Query("SELECT ta FROM TaskAssignment ta " +
            "WHERE ta.task.taskId = :taskId " +
            "AND ta.role = 'REVIEWER' " +
            "AND ta.status = 'ACTIVE' " +
            "ORDER BY ta.assignedAt ASC")
    List<TaskAssignment> findActiveReviewersByTaskId(@Param("taskId") Integer taskId);

    // ✅ 사용자의 완료된 할당 업무 개수
    @Query("SELECT COUNT(ta) FROM TaskAssignment ta " +
            "WHERE ta.user.userId = :userId " +
            "AND ta.status = 'COMPLETED'")
    long countCompletedAssignmentsByUserId(@Param("userId") Long userId);

    // ✅ 사용자의 활성 할당 업무 개수
    @Query("SELECT COUNT(ta) FROM TaskAssignment ta " +
            "WHERE ta.user.userId = :userId " +
            "AND ta.status = 'ACTIVE'")
    long countActiveAssignmentsByUserId(@Param("userId") Long userId);

    // ✅ 특정 업무의 활성 할당 개수
    @Query("SELECT COUNT(ta) FROM TaskAssignment ta " +
            "WHERE ta.task.taskId = :taskId " +
            "AND ta.status = 'ACTIVE'")
    long countActiveAssignmentsByTaskId(@Param("taskId") Integer taskId);

    // 🆕 사용자별 역할별 통계
    @Query("SELECT ta.role, COUNT(ta) FROM TaskAssignment ta " +
            "WHERE ta.user.userId = :userId " +
            "AND ta.status = 'ACTIVE' " +
            "GROUP BY ta.role")
    List<Object[]> countActiveAssignmentsByUserIdAndRole(@Param("userId") Long userId);

    // 🆕 할당 취소 (상태를 CANCELLED로 변경)
    @Query("UPDATE TaskAssignment ta SET ta.status = 'CANCELLED', ta.updatedAt = CURRENT_TIMESTAMP " +
            "WHERE ta.task.taskId = :taskId AND ta.user.userId = :userId AND ta.status = 'ACTIVE'")
    int cancelAssignment(@Param("taskId") Integer taskId, @Param("userId") Long userId);

    // 🆕 특정 업무의 모든 활성 할당 취소
    @Query("UPDATE TaskAssignment ta SET ta.status = 'CANCELLED', ta.updatedAt = CURRENT_TIMESTAMP " +
            "WHERE ta.task.taskId = :taskId AND ta.status = 'ACTIVE'")
    int cancelAllActiveAssignmentsByTaskId(@Param("taskId") Integer taskId);

    // 🆕 최근 할당된 업무들 (전체 사용자 대상)
    @Query("SELECT ta FROM TaskAssignment ta " +
            "WHERE ta.status = 'ACTIVE' " +
            "ORDER BY ta.assignedAt DESC")
    List<TaskAssignment> findRecentActiveAssignments();

    // 🆕 마감일이 임박한 업무의 할당들
    @Query("SELECT ta FROM TaskAssignment ta " +
            "WHERE ta.status = 'ACTIVE' " +
            "AND ta.task.endDate IS NOT NULL " +
            "AND ta.task.endDate <= :deadlineDate " +
            "ORDER BY ta.task.endDate ASC")
    List<TaskAssignment> findAssignmentsWithUpcomingDeadline(@Param("deadlineDate") java.time.LocalDate deadlineDate);
}
