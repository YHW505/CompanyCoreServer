package com.example.companycoreserver.service;

import com.example.companycoreserver.entity.Approval;
import com.example.companycoreserver.entity.User;
import com.example.companycoreserver.entity.Enum.ApprovalStatus;
import com.example.companycoreserver.repository.ApprovalRepository;
import com.example.companycoreserver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Service
@Transactional
public class ApprovalService {

    @Autowired
    private ApprovalRepository approvalRepository;

    @Autowired
    private UserRepository userRepository;

    // ✅ 내가 요청한 결재 목록
    public List<Approval> getMyRequests(Long userId) {
        return approvalRepository.findByRequesterId(userId);
    }

    // ✅ 내가 결재해야 할 목록
    public List<Approval> getMyApprovals(Long userId) {
        return approvalRepository.findByApproverId(userId);
    }

    // ✅ 내가 결재해야 할 대기중인 목록
    public List<Approval> getPendingApprovals(Long userId) {
        return approvalRepository.findPendingApprovalsByApproverId(userId);
    }

    // 🆕 부서별 결재 목록 조회 (기본)
    public List<Approval> getApprovalsByDepartment(String department) {
        return approvalRepository.findByRequesterDepartmentOrderByRequestDateDesc(department);
    }

    // 🆕 부서별 결재 목록 조회 (페이지네이션 포함)
    public Map<String, Object> getApprovalsByDepartmentWithPagination(String department, int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Approval> approvalPage = approvalRepository.findByRequesterDepartment(department, pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("content", approvalPage.getContent());
        response.put("totalElements", approvalPage.getTotalElements());
        response.put("totalPages", approvalPage.getTotalPages());
        response.put("currentPage", page);
        response.put("size", size);
        response.put("department", department);

        return response;
    }

    // 🔄 결재 요청 생성 - 첨부파일 파라미터 수정
        public Approval createApproval(String title, String content, Long requesterId, Long approverId,
                                 String attachmentFilename, String attachmentContentType, Long attachmentSize,
                                 String attachmentContent) {
        User requester = userRepository.findById(requesterId)
                .orElseThrow(() -> new RuntimeException("요청자를 찾을 수 없습니다."));

        User approver = userRepository.findById(approverId)
                .orElseThrow(() -> new RuntimeException("결재자를 찾을 수 없습니다."));

        // 🔄 새로운 생성자 사용 (첨부파일 메타데이터 포함)
        Approval approval = new Approval(title, content, requester, approver,
                attachmentFilename, attachmentContentType, attachmentSize, attachmentContent);
        
        return approvalRepository.save(approval);
    }

    // 🆕 첨부파일 없는 결재 요청 생성 (오버로드)
    public Approval createApproval(String title, String content, Long requesterId, Long approverId) {
        return createApproval(title, content, requesterId, approverId, null, null, null, null);
    }

    // ✅ 결재 승인
    public Approval approveRequest(Long approvalId, Long approverId) {
        Approval approval = approvalRepository.findById(approvalId)
                .orElseThrow(() -> new RuntimeException("결재 요청을 찾을 수 없습니다."));

        if (!approval.getApprover().getUserId().equals(approverId)) {
            throw new RuntimeException("결재 권한이 없습니다.");
        }

        if (!approval.isPending()) {
            throw new RuntimeException("이미 처리된 결재입니다.");
        }

        approval.setStatus(ApprovalStatus.APPROVED);
        approval.setProcessedDate(LocalDateTime.now());

        return approvalRepository.save(approval);
    }

    // ✅ 결재 거부
    public Approval rejectRequest(Long approvalId, Long approverId, String rejectionReason) {
        Approval approval = approvalRepository.findById(approvalId)
                .orElseThrow(() -> new RuntimeException("결재 요청을 찾을 수 없습니다."));

        if (!approval.getApprover().getUserId().equals(approverId)) {
            throw new RuntimeException("결재 권한이 없습니다.");
        }

        if (!approval.isPending()) {
            throw new RuntimeException("이미 처리된 결재입니다.");
        }

        approval.setStatus(ApprovalStatus.REJECTED);
        approval.setRejectionReason(rejectionReason);
        approval.setProcessedDate(LocalDateTime.now());

        return approvalRepository.save(approval);
    }

    // 🆕 결재 요청 수정
    public Approval updateApproval(Long approvalId, String title, String content,
                                 String attachmentFilename, String attachmentContentType, 
                                 Long attachmentSize, String attachmentContent) {
        Approval approval = approvalRepository.findById(approvalId)
                .orElseThrow(() -> new RuntimeException("결재 요청을 찾을 수 없습니다."));

        // 이미 처리된 결재는 수정 불가
        if (!approval.isPending()) {
            throw new RuntimeException("이미 처리된 결재는 수정할 수 없습니다.");
        }

        // 기본 정보 업데이트
        approval.setTitle(title);
        approval.setContent(content);

        // 첨부파일 정보 업데이트 (null 값도 허용하여 삭제 처리)
        approval.setAttachmentFilename(attachmentFilename);
        approval.setAttachmentContentType(attachmentContentType);
        approval.setAttachmentSize(attachmentSize);
        approval.setAttachmentContent(attachmentContent);

        // 수정 시간 업데이트
        approval.setUpdatedAt(LocalDateTime.now());

        return approvalRepository.save(approval);
    }


    // ✅ 내가 요청한 결재 삭제 (요청자만 가능)
    public void deleteMyRequest(Long approvalId, Long requesterId) {
        Approval approval = approvalRepository.findById(approvalId)
                .orElseThrow(() -> new RuntimeException("결재 요청을 찾을 수 없습니다."));

        // 요청자만 삭제 가능
        if (!approval.getRequester().getUserId().equals(requesterId)) {
            throw new RuntimeException("본인이 요청한 결재만 삭제할 수 있습니다.");
        }

        // 대기중인 결재만 삭제 가능
        if (!approval.isPending()) {
            throw new RuntimeException("처리된 결재는 삭제할 수 없습니다.");
        }

        approvalRepository.delete(approval);
    }

    // ✅ 결재 상세 조회 (Optional 버전)
    public Optional<Approval> getApprovalDetail(Long approvalId) {
        return approvalRepository.findById(approvalId);
    }

    // ✅ 결재 상세 조회 (Entity 직접 반환)
    public Approval getApprovalById(Long approvalId) {
        return approvalRepository.findById(approvalId)
                .orElseThrow(() -> new RuntimeException("결재 요청을 찾을 수 없습니다."));
    }

    // ✅ 제목으로 검색
    public List<Approval> searchByTitle(String title) {
        return approvalRepository.findByTitleContainingIgnoreCaseOrderByRequestDateDesc(title);
    }

    // ✅ 최근 7일간의 결재 목록
    public List<Approval> getRecentApprovals() {
        LocalDateTime weekAgo = LocalDateTime.now().minusDays(7);
        return approvalRepository.findRecentApprovals(weekAgo);
    }

    // 🆕 내가 요청한 결재 목록 (페이지네이션 포함)
    public Map<String, Object> getMyRequestsWithPagination(Long userId, int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Approval> approvalPage = approvalRepository.findByRequesterId(userId, pageable);
        
        Map<String, Object> response = new HashMap<>();
        response.put("content", approvalPage.getContent());
        response.put("totalElements", approvalPage.getTotalElements());
        response.put("totalPages", approvalPage.getTotalPages());
        response.put("currentPage", page);
        response.put("size", size);
        
        return response;
    }

    // 🆕 내가 결재해야 할 목록 (페이지네이션 포함)
    public Map<String, Object> getMyApprovalsWithPagination(Long userId, int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Approval> approvalPage = approvalRepository.findByApproverId(userId, pageable);
        
        Map<String, Object> response = new HashMap<>();
        response.put("content", approvalPage.getContent());
        response.put("totalElements", approvalPage.getTotalElements());
        response.put("totalPages", approvalPage.getTotalPages());
        response.put("currentPage", page);
        response.put("size", size);
        
        return response;
    }

    // 🆕 내가 결재해야 할 대기중인 목록 (페이지네이션 포함)
    public Map<String, Object> getPendingApprovalsWithPagination(Long userId, int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Approval> approvalPage = approvalRepository.findPendingApprovalsByApproverId(userId, pageable);
        
        Map<String, Object> response = new HashMap<>();
        response.put("content", approvalPage.getContent());
        response.put("totalElements", approvalPage.getTotalElements());
        response.put("totalPages", approvalPage.getTotalPages());
        response.put("currentPage", page);
        response.put("size", size);
        
        return response;
    }
}
