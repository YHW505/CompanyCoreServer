package com.example.companycoreserver.service;

import com.example.companycoreserver.dto.ApprovalResponse;
import com.example.companycoreserver.entity.Approval;
import com.example.companycoreserver.entity.Department;
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
import java.util.stream.Collectors;

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

    // 🔄 Approval → ApprovalResponse 변환 메서드
    private ApprovalResponse convertToApprovalResponse(Approval approval) {
        // RequesterInfo 생성
        ApprovalResponse.RequesterInfo requesterInfo = new ApprovalResponse.RequesterInfo(
                approval.getRequester().getUserId(),
                approval.getRequester().getEmployeeCode(),
                approval.getRequester().getUsername(),
                approval.getRequester().getPosition().getPositionName(), // Position 엔티티에서 이름 추출
                approval.getRequester().getDepartment().getDepartmentName() // Department 엔티티에서 이름 추출
        );

        // ApproverInfo 생성 (승인자가 있는 경우만)
        ApprovalResponse.ApproverInfo approverInfo = null;
        if (approval.getApprover() != null) {
            approverInfo = new ApprovalResponse.ApproverInfo(
                    approval.getApprover().getUserId(),
                    approval.getApprover().getEmployeeCode(),
                    approval.getApprover().getUsername(),
                    approval.getApprover().getPosition().getPositionName(),
                    approval.getApprover().getDepartment().getDepartmentName()
            );
        }

        // 첨부파일 Base64 인코딩 (있는 경우만)
//        String attachmentContent = null;
//        if (approval.getAttachmentData() != null) {
//            attachmentContent = Base64.getEncoder().encodeToString(approval.getAttachmentData());
//        }

        // ApprovalResponse 생성 및 반환
        return new ApprovalResponse(
                approval.getId(),
                approval.getTitle(),
                approval.getContent(),
                requesterInfo,
                approverInfo,
                approval.getRequestDate(),
                approval.getStatus(),
                approval.getRejectionReason(),
                approval.getProcessedDate(),
                approval.getAttachmentFilename(),
                approval.getAttachmentContentType(),
                approval.getAttachmentSize(),
                approval.getCreatedAt(),
                approval.getUpdatedAt()
        );
    }

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

    // ✅ ApprovalResponse 리스트 반환
    public List<ApprovalResponse> getApprovalsByDepartment(Department department) {
        // 1️⃣ Repository에서 Approval 엔티티 리스트 조회
        List<Approval> approvals = approvalRepository.findByRequesterDepartmentOrderByRequestDateDesc(department);

        // 2️⃣ Approval → ApprovalResponse 변환
        return approvals.stream()
                .map(this::convertToApprovalResponse)
                .collect(Collectors.toList());
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

    public Map<String, Object> getPendingApprovalsByDepartmentWithPagination(String department, int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        // 부서 + PENDING 상태로 필터링
        Page<Approval> approvalPage = approvalRepository.findByRequesterDepartmentAndStatus(department, ApprovalStatus.PENDING, pageable);

        // ✅ Entity를 DTO로 변환
        List<ApprovalResponse> approvalResponses = approvalPage.getContent().stream()
                .map(this::convertToApprovalResponse)  // 변환 메서드 사용
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("content", approvalResponses);  // ✅ DTO 리스트로 변경
        response.put("totalElements", approvalPage.getTotalElements());
        response.put("totalPages", approvalPage.getTotalPages());
        response.put("currentPage", page);
        response.put("size", size);
        response.put("department", department);
        response.put("status", "PENDING");

        return response;
    }


    // 🔄 결재 요청 생성 - 첨부파일 포함 (approverId null 허용)
    public Approval createApproval(String title, String content, Long requesterId,
                                   String attachmentFilename, String attachmentContentType, Long attachmentSize,
                                   String attachmentContent) {
        return createApproval(title, content, requesterId, attachmentFilename, attachmentContentType, attachmentSize, attachmentContent, false);
    }

    public Approval createApproval(String title, String content, Long requesterId,
                                   String attachmentFilename, String attachmentContentType, Long attachmentSize,
                                   String attachmentContent, Boolean hasAttachments) {

//        log.info("결재 생성 - title: {}, requesterId: {}, approverId: {}", title, requesterId, approverId);

        // 요청자 정보 조회 (부서 정보 포함)
        User requester = userRepository.findById(requesterId)
                .orElseThrow(() -> new RuntimeException("요청자를 찾을 수 없습니다."));
        
        // 요청자의 부서 정보 확인
        if (requester.getDepartment() == null) {
            System.err.println("요청자의 부서 정보가 없습니다: " + requester.getUsername());
            throw new RuntimeException("요청자의 부서 정보가 없습니다.");
        }

        // ✅ approverId가 null이면 approver도 null로 설정
        User approver = null;
//        if (approverId != null) {
//            approver = userRepository.findById(approverId)
//                    .orElseThrow(() -> new RuntimeException("결재자를 찾을 수 없습니다."));
//        }

        // 🔄 생성자 호출 (approver null 가능)
        Approval approval = new Approval(title, content, requester, approver,
                attachmentFilename, attachmentContentType, attachmentSize, attachmentContent);
        
        // hasAttachments 설정
        if (hasAttachments != null) {
            approval.setHasAttachments(hasAttachments);
        }

        return approvalRepository.save(approval);
    }

    // 🆕 첨부파일 없는 결재 요청 생성 (approverId null 허용)
    public Approval createApproval(String title, String content, Long requesterId ) {
        return createApproval(title, content, requesterId, null, null, null, null, false);
    }
    
    public Approval createApproval(String title, String content, Long requesterId, Boolean hasAttachments ) {
        return createApproval(title, content, requesterId, null, null, null, null, hasAttachments);
    }

    // ✅ 결재 승인 - approverId 설정 및 상태 변경
    public Approval approveRequest(Long approvalId, Long approverId) {
//        log.info("결재 승인 - approvalId: {}, approverId: {}", approvalId, approverId);

        Approval approval = approvalRepository.findById(approvalId)
                .orElseThrow(() -> new RuntimeException("결재 요청을 찾을 수 없습니다."));

        // ✅ 이미 처리된 결재인지 확인
        if (!approval.isPending()) {
            throw new RuntimeException("이미 처리된 결재입니다. 현재 상태: " + approval.getStatus());
        }

        // ✅ 승인자 설정 (생성 시 null이었던 경우 여기서 설정)
        User approver = userRepository.findById(approverId)
                .orElseThrow(() -> new RuntimeException("승인자를 찾을 수 없습니다."));

        // 🔄 기존 승인자가 있다면 권한 체크, 없다면 새로 설정
        if (approval.getApprover() != null) {
            if (!approval.getApprover().getUserId().equals(approverId)) {
                throw new RuntimeException("결재 권한이 없습니다.");
            }
        } else {
            // 승인자가 null이었던 경우 새로 설정
            approval.setApprover(approver);
        }

        approval.setStatus(ApprovalStatus.APPROVED);
        approval.setProcessedDate(LocalDateTime.now());

        return approvalRepository.save(approval);
    }

    // ✅ 결재 거부 - approverId 설정 및 상태 변경
    public Approval rejectRequest(Long approvalId, Long approverId, String rejectionReason) {
//        log.info("결재 거부 - approvalId: {}, approverId: {}", approvalId, approverId);

        Approval approval = approvalRepository.findById(approvalId)
                .orElseThrow(() -> new RuntimeException("결재 요청을 찾을 수 없습니다."));

        // ✅ 이미 처리된 결재인지 확인
        if (!approval.isPending()) {
            throw new RuntimeException("이미 처리된 결재입니다. 현재 상태: " + approval.getStatus());
        }

        // ✅ 승인자 설정 (생성 시 null이었던 경우 여기서 설정)
        User approver = userRepository.findById(approverId)
                .orElseThrow(() -> new RuntimeException("승인자를 찾을 수 없습니다."));

        // 🔄 기존 승인자가 있다면 권한 체크, 없다면 새로 설정
        if (approval.getApprover() != null) {
            if (!approval.getApprover().getUserId().equals(approverId)) {
                throw new RuntimeException("결재 권한이 없습니다.");
            }
        } else {
            // 승인자가 null이었던 경우 새로 설정
            approval.setApprover(approver);
        }

        approval.setStatus(ApprovalStatus.REJECTED);
        approval.setRejectionReason(rejectionReason);
        approval.setProcessedDate(LocalDateTime.now());

        return approvalRepository.save(approval);
    }

    // 🆕 승인 대기 중인 결재 목록 조회 (승인자 미지정)
    public List<Approval> getPendingApprovalsWithoutApprover() {
        return approvalRepository.findByStatusAndApproverIsNull(ApprovalStatus.PENDING);
    }

    // 🆕 특정 승인자의 대기 중인 결재 목록
    public List<Approval> getPendingApprovalsByApprover(Long approverId) {
        return approvalRepository.findByStatusAndApprover_UserId(ApprovalStatus.PENDING, approverId);
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

    // 🆕 부장이 본인 부서의 모든 결재 요청 조회 (대기, 처리 완료 모두 포함)
    public List<ApprovalResponse> getAllApprovalsForManagerDepartment(Long managerUserId) {
        User manager = userRepository.findById(managerUserId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // 부서 정보 확인
        if (manager.getDepartment() == null || manager.getDepartment().getDepartmentId() == null) {
            throw new RuntimeException("사용자의 부서 정보가 없습니다.");
        }

        Integer managerDepartmentId = manager.getDepartment().getDepartmentId();

        // 해당 부서의 모든 결재 요청 조회
        List<Approval> approvals = approvalRepository.findAllByRequesterDepartmentId(managerDepartmentId);

        // Approval 엔티티를 ApprovalResponse DTO로 변환
        return approvals.stream()
                .map(this::convertToApprovalResponse)
                .collect(Collectors.toList());
    }

    // ✅ 부서별 권한 검증 메서드
    public boolean validateDepartmentPermission(Long approvalId, Long approverId) {
        try {
            // 결재 정보 조회 (부서 정보 포함)
            Approval approval = approvalRepository.findByIdWithRequesterDepartment(approvalId);
            if (approval == null) {
                throw new RuntimeException("결재 요청을 찾을 수 없습니다.");
            }
            
            // 승인자 정보 조회 (부서 정보 포함)
            User approver = userRepository.findById(approverId)
                    .orElseThrow(() -> new RuntimeException("승인자를 찾을 수 없습니다."));
            
            // 요청자 정보 (이미 JOIN FETCH로 로드됨)
            User requester = approval.getRequester();
            
            // 부서 정보 확인
            if (requester.getDepartment() == null || approver.getDepartment() == null) {
                System.err.println("부서 정보가 없습니다. 요청자: " + requester.getUsername() + 
                                 " (부서: " + (requester.getDepartment() != null ? requester.getDepartment().getDepartmentName() : "null") + ")" +
                                 ", 승인자: " + approver.getUsername() + 
                                 " (부서: " + (approver.getDepartment() != null ? approver.getDepartment().getDepartmentName() : "null") + ")");
                return false;
            }
            
            // 같은 부서인지 확인
            boolean sameDepartment = requester.getDepartment().getDepartmentId()
                    .equals(approver.getDepartment().getDepartmentId());
            
            System.out.println("부서 권한 검증 - 요청자 부서: " + requester.getDepartment().getDepartmentName() + 
                             ", 승인자 부서: " + approver.getDepartment().getDepartmentName() + 
                             ", 권한: " + (sameDepartment ? "허용" : "거부"));
            
            return sameDepartment;
            
        } catch (Exception e) {
            System.err.println("부서 권한 검증 중 오류: " + e.getMessage());
            return false;
        }
    }
}
