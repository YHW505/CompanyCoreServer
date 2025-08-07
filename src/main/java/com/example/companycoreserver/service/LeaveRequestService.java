package com.example.companycoreserver.service;

import com.example.companycoreserver.dto.LeaveRequestResponse;
import com.example.companycoreserver.entity.User;
import com.example.companycoreserver.entity.LeaveRequest;
import com.example.companycoreserver.entity.Enum.LeaveStatus;
import com.example.companycoreserver.entity.Enum.LeaveType;
import com.example.companycoreserver.repository.UserRepository;
import com.example.companycoreserver.repository.LeaveRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class LeaveRequestService {

    @Autowired
    private LeaveRequestRepository leaveRequestRepository;

    @Autowired
    private UserRepository userRepository;

    // DTO 변환 메서드
    private LeaveRequestResponse convertToDTO(LeaveRequest leaveRequest) {
        User user = userRepository.findById(leaveRequest.getUserId()).orElse(null);
        User approver = null;
        if (leaveRequest.getApprovedBy() != null) {
            approver = userRepository.findById(leaveRequest.getApprovedBy()).orElse(null);
        }

        return new LeaveRequestResponse(
                leaveRequest.getLeaveId().longValue(),
                leaveRequest.getUserId(),
                user != null ? user.getUsername() : "Unknown",
                user != null ? user.getDepartmentId() : null,
                user != null ? user.getPositionId() : null,
                leaveRequest.getLeaveType(),
                leaveRequest.getStartDate(),
                leaveRequest.getEndDate(),
                leaveRequest.getReason(),
                leaveRequest.getStatus(),
                leaveRequest.getApprovedBy(),
                approver != null ? approver.getUsername() : null,
                approver != null ? approver.getPositionId().toString() : null,
                leaveRequest.getApprovedAt(),
                leaveRequest.getAppliedAt()
        );
    }

    // 1. 휴가 신청
    public LeaveRequestResponse applyLeave(Long userId, LeaveType leaveType, LocalDate startDate,
                                           LocalDate endDate, String reason) {
        System.out.println("휴가 신청 처리 시작 - 사용자: " + userId + ", 유형: " + leaveType);
        
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("존재하지 않는 사용자입니다.");
        }
        if (startDate.isAfter(endDate)) {
            throw new RuntimeException("시작일이 종료일보다 늦을 수 없습니다.");
        }
        if (startDate.isBefore(LocalDate.now())) {
            throw new RuntimeException("과거 날짜로는 휴가를 신청할 수 없습니다.");
        }

        try {
            LeaveRequest leaveRequest = new LeaveRequest(userId, leaveType, startDate, endDate, reason, LeaveStatus.PENDING);
            LeaveRequest savedRequest = leaveRequestRepository.save(leaveRequest);
            System.out.println("휴가 신청 저장 성공 - ID: " + savedRequest.getLeaveId());
            return convertToDTO(savedRequest);
        } catch (Exception e) {
            System.err.println("휴가 신청 저장 실패: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    // 2. 휴가 신청 조회 (필터링 가능)
    public List<LeaveRequestResponse> getLeaveRequests(Long userId, LeaveStatus status, LeaveType leaveType,
                                                       LocalDate startDate, LocalDate endDate) {
        List<LeaveRequest> requests;

        // 조건에 따른 조회
        if (userId != null && status != null) {
            requests = leaveRequestRepository.findByUserIdAndStatus(userId, status);
        } else if (userId != null && leaveType != null) {
            requests = leaveRequestRepository.findByUserIdAndLeaveType(userId, leaveType);
        } else if (userId != null) {
            requests = leaveRequestRepository.findByUserIdOrderByAppliedAtDesc(userId);
        } else if (status != null) {
            requests = leaveRequestRepository.findByStatusOrderByAppliedAtAsc(status);
        } else if (leaveType != null) {
            requests = leaveRequestRepository.findByLeaveType(leaveType);
        } else if (startDate != null && endDate != null) {
            requests = leaveRequestRepository.findByStartDateBetween(startDate, endDate);
        } else {
            requests = leaveRequestRepository.findAll();
        }

        return requests.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // 3. 특정 휴가 신청 조회
    public Optional<LeaveRequestResponse> getLeaveRequestById(Integer leaveId) {
        Optional<LeaveRequest> request = leaveRequestRepository.findById(leaveId);
        return request.map(this::convertToDTO);
    }

    // 4. 휴가 승인/거부
// 기존 메서드 (승인용)
    public LeaveRequestResponse processLeave(Integer leaveId, Long approverId, LeaveStatus newStatus) {
        return processLeave(leaveId, approverId, newStatus, null);
    }

    // 거부 사유 포함 메서드
    public LeaveRequestResponse processLeave(Integer leaveId, Long approverId, LeaveStatus newStatus, String rejectionReason) {
        System.out.println("휴가 처리 시작 - 휴가ID: " + leaveId + ", 처리자ID: " + approverId + ", 상태: " + newStatus);
        
        if (!userRepository.existsById(approverId)) {
            throw new RuntimeException("존재하지 않는 승인자입니다.");
        }

        Optional<LeaveRequest> requestOpt = leaveRequestRepository.findById(leaveId);
        if (!requestOpt.isPresent()) {
            throw new RuntimeException("휴가 신청을 찾을 수 없습니다.");
        }

        LeaveRequest request = requestOpt.get();
        System.out.println("휴가 신청 찾음 - 현재 상태: " + request.getStatus());
        
        if (request.getStatus() != LeaveStatus.PENDING) {
            throw new RuntimeException("이미 처리된 휴가 신청입니다.");
        }

        try {
            request.setStatus(newStatus);
            request.setApprovedBy(approverId);
            request.setApprovedAt(LocalDateTime.now());

            // 거부인 경우 거부 관련 정보 설정
            if (newStatus == LeaveStatus.REJECTED) {
                request.setRejectedBy(approverId);
                request.setRejectionReason(rejectionReason);
                request.setRejectedAt(LocalDateTime.now());
            }

            LeaveRequest savedRequest = leaveRequestRepository.save(request);
            System.out.println("휴가 처리 완료 - ID: " + savedRequest.getLeaveId() + ", 상태: " + savedRequest.getStatus());
            return convertToDTO(savedRequest);
        } catch (Exception e) {
            System.err.println("휴가 처리 실패: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }


    // 5. 휴가 신청 삭제
    public boolean deleteLeaveRequest(Integer leaveId, Long userId) {
        Optional<LeaveRequest> requestOpt = leaveRequestRepository.findById(leaveId);
        if (!requestOpt.isPresent()) {
            throw new RuntimeException("휴가 신청을 찾을 수 없습니다.");
        }

        LeaveRequest request = requestOpt.get();
        if (!request.getUserId().equals(userId)) {
            throw new RuntimeException("본인의 휴가 신청만 삭제할 수 있습니다.");
        }
        if (request.getStatus() != LeaveStatus.PENDING) {
            throw new RuntimeException("대기 중인 휴가 신청만 삭제할 수 있습니다.");
        }

        leaveRequestRepository.deleteById(leaveId);
        return true;
    }

    // 6. 승인자별 처리한 휴가 조회
    public List<LeaveRequestResponse> getLeaveRequestsByApprover(Long approverId) {
        List<LeaveRequest> requests = leaveRequestRepository.findByApprovedByOrderByApprovedAtDesc(approverId);
        return requests.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public LeaveRequestResponse updateLeaveRequest(Integer leaveId, String leaveType, String startDate, String endDate, String reason) {
        Optional<LeaveRequest> requestOpt = leaveRequestRepository.findById(leaveId);
        if (!requestOpt.isPresent()) {
            throw new RuntimeException("휴가 신청을 찾을 수 없습니다.");
        }

        LeaveRequest request = requestOpt.get();

        // PENDING 상태만 수정 가능
        if (request.getStatus() != LeaveStatus.PENDING) {
            throw new RuntimeException("대기 중인 휴가 신청만 수정할 수 있습니다.");
        }

        // 데이터 업데이트
        request.setLeaveType(LeaveType.valueOf(leaveType));
        request.setStartDate(LocalDate.parse(startDate));
        request.setEndDate(LocalDate.parse(endDate));
        request.setReason(reason);
//        request.setUpdatedAt(LocalDateTime.now()); // 수정 시간 업데이트

        LeaveRequest savedRequest = leaveRequestRepository.save(request);
        return convertToDTO(savedRequest);
    }


    // 7. 현재 휴가 중인 사용자들 조회
    public List<LeaveRequestResponse> getCurrentLeaves() {
        List<LeaveRequest> requests = leaveRequestRepository.findCurrentLeaves(LeaveStatus.APPROVED, LocalDate.now());
        return requests.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

//    // 8. 특정 사용자의 연간 사용 휴가 일수 조회
//    public Long getUserUsedLeaveDays(Long userId, int year) {
//        Long usedDays = leaveRequestRepository.getUsedLeaveDaysForUser(userId, LeaveStatus.APPROVED, year);
//        return usedDays != null ? usedDays : 0L;
//    }
//
//    // 9. 승인 대기 시간이 긴 휴가들 조회
//    public List<LeaveRequestResponse> getLongPendingLeaves(int days) {
//        List<LeaveRequest> requests = leaveRequestRepository.findLongPendingLeaves(LeaveStatus.PENDING, days);
//        return requests.stream()
//                .map(this::convertToDTO)
//                .collect(Collectors.toList());
//    }
//
//    // 10. 특정 연도의 특정 사용자 휴가 목록
//    public List<LeaveRequestResponse> getUserLeavesByYear(Long userId, int year) {
//        List<LeaveRequest> requests = leaveRequestRepository.findByUserIdAndYear(userId, year);
//        return requests.stream()
//                .map(this::convertToDTO)
//                .collect(Collectors.toList());
//    }
}
