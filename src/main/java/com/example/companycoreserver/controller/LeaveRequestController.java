package com.example.companycoreserver.controller;

import com.example.companycoreserver.dto.LeaveRequestResponse;
import com.example.companycoreserver.entity.Enum.LeaveStatus;
import com.example.companycoreserver.entity.Enum.LeaveType;
import com.example.companycoreserver.service.LeaveRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/leave-requests")
@CrossOrigin(origins = "*")
public class LeaveRequestController {

    @Autowired
    private LeaveRequestService leaveRequestService;

    // 1. 휴가 신청
    @PostMapping
    public ResponseEntity<?> applyLeave(@RequestBody Map<String, Object> request) {
        try {
            Long userId = Long.valueOf(request.get("userId").toString());
            
            // leaveType 처리 개선 - 대소문자 구분 없이 처리
            String leaveTypeStr = request.get("leaveType").toString().toUpperCase();
            LeaveType leaveType;
            try {
                leaveType = LeaveType.valueOf(leaveTypeStr);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body("잘못된 휴가 유형입니다: " + leaveTypeStr + 
                    ". 사용 가능한 유형: ANNUAL(연차), HALF_DAY(반차), SICK(병가), PERSONAL(개인사유), " +
                    "MATERNITY(출산휴가), PATERNITY(육아휴가), SPECIAL(특별휴가), OFFICIAL(공가)");
            }
            
            LocalDate startDate = LocalDate.parse(request.get("startDate").toString());
            LocalDate endDate = LocalDate.parse(request.get("endDate").toString());
            String reason = request.get("reason").toString();

            System.out.println("휴가 신청 요청 - 사용자: " + userId + ", 유형: " + leaveType + 
                             ", 시작일: " + startDate + ", 종료일: " + endDate + ", 사유: " + reason);

            LeaveRequestResponse response = leaveRequestService.applyLeave(userId, leaveType, startDate, endDate, reason);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("휴가 신청 실패: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body("휴가 신청 실패: " + e.getMessage());
        }
    }

    // 2. 휴가 신청 목록 조회 (다양한 필터링 옵션)
    @GetMapping
    public ResponseEntity<?> getLeaveRequests(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String leaveType,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        try {
            LeaveStatus leaveStatus = status != null ? LeaveStatus.valueOf(status.toUpperCase()) : null;
            LeaveType type = leaveType != null ? LeaveType.valueOf(leaveType.toUpperCase()) : null;
            LocalDate start = startDate != null ? LocalDate.parse(startDate) : null;
            LocalDate end = endDate != null ? LocalDate.parse(endDate) : null;

            List<LeaveRequestResponse> requests = leaveRequestService.getLeaveRequests(userId, leaveStatus, type, start, end);
            return ResponseEntity.ok(requests);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("조회 실패: " + e.getMessage());
        }
    }

    // 3. 특정 휴가 신청 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<?> getLeaveRequestById(@PathVariable Integer id) {
        try {
            Optional<LeaveRequestResponse> request = leaveRequestService.getLeaveRequestById(id);
            return request.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("조회 실패: " + e.getMessage());
        }
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<?> approveLeaveRequest(@PathVariable Integer id, @RequestBody Map<String, Object> request) {
        try {
            Long approverId = Long.valueOf(request.get("approverId").toString());
            System.out.println("휴가 승인 요청 - 휴가ID: " + id + ", 승인자ID: " + approverId);
            
            LeaveRequestResponse response = leaveRequestService.processLeave(id, approverId, LeaveStatus.APPROVED);
            System.out.println("휴가 승인 성공 - 휴가ID: " + id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("휴가 승인 실패 - 휴가ID: " + id + ", 오류: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body("승인 실패: " + e.getMessage());
        }
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<?> rejectLeaveRequest(@PathVariable Integer id, @RequestBody Map<String, Object> request) {
        try {
            Long rejectedBy = Long.valueOf(request.get("rejectedBy").toString());
            String rejectionReason = request.get("rejectionReason").toString();
            
            System.out.println("휴가 거부 요청 - 휴가ID: " + id + ", 거부자ID: " + rejectedBy + ", 사유: " + rejectionReason);

            LeaveRequestResponse response = leaveRequestService.processLeave(id, rejectedBy, LeaveStatus.REJECTED, rejectionReason);
            System.out.println("휴가 거부 성공 - 휴가ID: " + id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("휴가 거부 실패 - 휴가ID: " + id + ", 오류: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body("거부 실패: " + e.getMessage());
        }
    }

    // 6. 휴가 신청 삭제 (본인만, 대기 상태만)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteLeaveRequest(@PathVariable Integer id, @RequestParam Long userId) {
        try {
            boolean deleted = leaveRequestService.deleteLeaveRequest(id, userId);
            return deleted ? ResponseEntity.ok("휴가 신청이 삭제되었습니다.")
                    : ResponseEntity.badRequest().body("삭제 실패");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("삭제 실패: " + e.getMessage());
        }
    }

    // 7. 승인자별 처리한 휴가 조회
    @GetMapping("/approver/{approverId}")
    public ResponseEntity<?> getLeaveRequestsByApprover(@PathVariable Long approverId) {
        try {
            List<LeaveRequestResponse> requests = leaveRequestService.getLeaveRequestsByApprover(approverId);
            return ResponseEntity.ok(requests);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("조회 실패: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateLeaveRequest(@PathVariable Integer id, @RequestBody Map<String, Object> request) {
        try {
            String leaveType = request.get("leaveType").toString();
            String startDate = request.get("startDate").toString();
            String endDate = request.get("endDate").toString();
            String reason = request.get("reason").toString();

            LeaveRequestResponse response = leaveRequestService.updateLeaveRequest(id, leaveType, startDate, endDate, reason);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("수정 실패: " + e.getMessage());
        }
    }

    // 8. 현재 휴가 중인 사용자들 조회
    @GetMapping("/current")
    public ResponseEntity<?> getCurrentLeaves() {
        try {
            List<LeaveRequestResponse> requests = leaveRequestService.getCurrentLeaves();
            return ResponseEntity.ok(requests);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("조회 실패: " + e.getMessage());
        }
    }

    // 9. 특정 사용자의 연간 사용 휴가 일수 조회
//    @GetMapping("/users/{userId}/used-days")
//    public ResponseEntity<?> getUserUsedLeaveDays(@PathVariable Long userId,
//                                                  @RequestParam(defaultValue = "2025") int year) {
//        try {
//            Long usedDays = leaveRequestService.getUserUsedLeaveDays(userId, year);
//            return ResponseEntity.ok(Map.of(
//                    "userId", userId,
//                    "year", year,
//                    "usedDays", usedDays
//            ));
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body("조회 실패: " + e.getMessage());
//        }
//    }

//    // 10. 승인 대기 시간이 긴 휴가들 조회
//    @GetMapping("/long-pending")
//    public ResponseEntity<?> getLongPendingLeaves(@RequestParam(defaultValue = "3") int days) {
//        try {
//            List<LeaveRequestResponse> requests = leaveRequestService.getLongPendingLeaves(days);
//            return ResponseEntity.ok(requests);
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body("조회 실패: " + e.getMessage());
//        }
//    }

    // 11. 특정 사용자의 특정 연도 휴가 목록
//    @GetMapping("/users/{userId}/years/{year}")
//    public ResponseEntity<?> getUserLeavesByYear(@PathVariable Long userId, @PathVariable int year) {
//        try {
//            List<LeaveRequestResponse> requests = leaveRequestService.getUserLeavesByYear(userId, year);
//            return ResponseEntity.ok(requests);
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body("조회 실패: " + e.getMessage());
//        }
//    }
}
