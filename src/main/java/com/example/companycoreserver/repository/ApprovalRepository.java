package com.example.companycoreserver.repository;

import com.example.companycoreserver.entity.Approval;
import com.example.companycoreserver.entity.Enum.ApprovalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

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
}
