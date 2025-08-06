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
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface TaskRepository extends JpaRepository<Task, Integer> {

    // 특정 사용자가 할당받은 작업 조회
    List<Task> findByAssignedTo(Long assignedTo);

    // 특정 사용자가 할당한 작업 조회
    List<Task> findByAssignedBy(Long assignedBy);

    // 상태별 작업 조회
    List<Task> findByStatus(TaskStatus status);

    // 작업 타입별 조회
    List<Task> findByTaskType(TaskType taskType);

    // 특정 사용자의 특정 상태 작업 조회
    List<Task> findByAssignedToAndStatus(Long assignedTo, TaskStatus status);

    // 특정 사용자의 특정 타입 작업 조회
    List<Task> findByAssignedToAndTaskType(Long assignedTo, TaskType taskType);

    // 날짜 범위로 작업 조회
    @Query("SELECT t FROM Task t WHERE t.startDate >= :startDate AND t.endDate <= :endDate")
    List<Task> findByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    // 특정 사용자의 날짜 범위 작업 조회
    @Query("SELECT t FROM Task t WHERE t.assignedTo = :assignedTo AND t.startDate >= :startDate AND t.endDate <= :endDate")
    List<Task> findByAssignedToAndDateRange(@Param("assignedTo") Long assignedTo,
                                            @Param("startDate") LocalDate startDate,
                                            @Param("endDate") LocalDate endDate);

    // 제목으로 검색
    @Query("SELECT t FROM Task t WHERE t.title LIKE %:keyword%")
    List<Task> findByTitleContaining(@Param("keyword") String keyword);

    // 복합 조건 검색
    @Query("SELECT t FROM Task t WHERE " +
            "(:assignedTo IS NULL OR t.assignedTo = :assignedTo) AND " +
            "(:status IS NULL OR t.status = :status) AND " +
            "(:taskType IS NULL OR t.taskType = :taskType)")
    List<Task> findByMultipleConditions(@Param("assignedTo") Long assignedTo,
                                        @Param("status") TaskStatus status,
                                        @Param("taskType") TaskType taskType);

    // 🆕 특정 사용자의 특정 타입 작업 조회 (페이지네이션 포함)
    @Query("SELECT t FROM Task t WHERE t.assignedTo = :assignedTo AND t.taskType = :taskType")
    Page<Task> findByAssignedToAndTaskType(@Param("assignedTo") Long assignedTo, 
                                           @Param("taskType") TaskType taskType, 
                                           Pageable pageable);
}
