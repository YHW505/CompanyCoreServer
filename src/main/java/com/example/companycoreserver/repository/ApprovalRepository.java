package com.example.companycoreserver.repository;

import com.example.companycoreserver.entity.Approval;
import com.example.companycoreserver.entity.Department;
import com.example.companycoreserver.entity.Enum.ApprovalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface ApprovalRepository extends JpaRepository<Approval, Long> {

    // ✅ 내가 요청한 결재 목록
    @Query("SELECT a FROM Approval a WHERE a.requester.userId = :userId ORDER BY a.requestDate DESC")
    List<Approval> findByRequesterId(@Param("userId") Long userId);

    // ✅ 내가 결재해야 할 목록
    @Query("SELECT a FROM Approval a WHERE a.approver.userId = :userId ORDER BY a.requestDate DESC")
    List<Approval> findByApproverId(@Param("userId") Long userId);

    // ✅ 내가 결재해야 할 대기중인 목록
    @Query("SELECT a FROM Approval a WHERE a.approver.userId = :userId AND a.status = 'PENDING' ORDER BY a.requestDate DESC")
    List<Approval> findPendingApprovalsByApproverId(@Param("userId") Long userId);

    // 🆕 승인자가 지정되지 않은 대기 중인 결재
    List<Approval> findByStatusAndApproverIsNull(ApprovalStatus status);

    // 🆕 특정 승인자의 대기 중인 결재
    List<Approval> findByStatusAndApprover_UserId(ApprovalStatus status, Long approverId);

    // 🆕 부서별 결재 목록 조회 (기본) - 요청일 기준 내림차순 정렬
    List<Approval> findByRequesterDepartmentOrderByRequestDateDesc(Department department);

    // 🆕 부서별 + 상태별 결재 목록 조회
    @Query("SELECT a FROM Approval a JOIN a.requester u WHERE u.department.departmentId = :department AND a.status = :status")
    Page<Approval> findByRequesterDepartmentAndStatus(@Param("department") String department,
                                                      @Param("status") ApprovalStatus status,
                                                      Pageable pageable);

    // 🆕 부서별 결재 목록 조회 (페이지네이션 포함)
    Page<Approval> findByRequesterDepartment(String department, Pageable pageable);

    // ✅ 상태별 결재 목록
    List<Approval> findByStatusOrderByRequestDateDesc(ApprovalStatus status);

    // ✅ 특정 기간 결재 목록
    List<Approval> findByRequestDateBetweenOrderByRequestDateDesc(LocalDateTime startDate, LocalDateTime endDate);

    // ✅ 제목으로 검색
    List<Approval> findByTitleContainingIgnoreCaseOrderByRequestDateDesc(String title);

    // ✅ 요청자와 결재자 조합 검색
    @Query("SELECT a FROM Approval a WHERE a.requester.userId = :requesterId AND a.approver.userId = :approverId ORDER BY a.requestDate DESC")
    List<Approval> findByRequesterAndApprover(@Param("requesterId") Long requesterId, @Param("approverId") Long approverId);

    // ✅ 최근 N일간의 결재 목록
    @Query("SELECT a FROM Approval a WHERE a.requestDate >= :fromDate ORDER BY a.requestDate DESC")
    List<Approval> findRecentApprovals(@Param("fromDate") LocalDateTime fromDate);

    // 🆕 내가 요청한 결재 목록 (페이지네이션 포함)
    @Query("SELECT a FROM Approval a WHERE a.requester.userId = :userId")
    Page<Approval> findByRequesterId(@Param("userId") Long userId, Pageable pageable);

    // 🆕 내가 결재해야 할 목록 (페이지네이션 포함)
    @Query("SELECT a FROM Approval a WHERE a.approver.userId = :userId")
    Page<Approval> findByApproverId(@Param("userId") Long userId, Pageable pageable);

    // 🆕 내가 결재해야 할 대기중인 목록 (페이지네이션 포함)
    @Query("SELECT a FROM Approval a WHERE a.approver.userId = :userId AND a.status = 'PENDING'")
    Page<Approval> findPendingApprovalsByApproverId(@Param("userId") Long userId, Pageable pageable);
    
    // 🆕 부서 정보를 포함하여 결재 조회 (권한 검증용)
    @Query("SELECT a FROM Approval a JOIN FETCH a.requester r JOIN FETCH r.department WHERE a.id = :approvalId")
    Approval findByIdWithRequesterDepartment(@Param("approvalId") Long approvalId);

    // 🆕 요청자의 부서 ID로 모든 결재 목록 조회 (상태 무관)
    @Query("SELECT a FROM Approval a JOIN a.requester r WHERE r.department.departmentId = :departmentId ORDER BY a.requestDate DESC")
    List<Approval> findAllByRequesterDepartmentId(@Param("departmentId") Integer departmentId);
}
