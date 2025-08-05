package com.example.companycoreserver.controller;

import com.example.companycoreserver.dto.ApprovalResponse;
import com.example.companycoreserver.entity.Approval;
import com.example.companycoreserver.mapper.ApprovalMapper;
import com.example.companycoreserver.service.ApprovalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/approvals")
public class ApprovalController {

    @Autowired
    private ApprovalService approvalService;

    @Autowired
    private ApprovalMapper approvalMapper;

    // ✅ 내가 요청한 결재 목록 - DTO 변환
    @GetMapping("/my-requests/{userId}")
    public ResponseEntity<List<ApprovalResponse>> getMyRequests(@PathVariable Long userId) {
        List<Approval> approvals = approvalService.getMyRequests(userId);

        // Entity → DTO 변환
        List<ApprovalResponse> responses = new ArrayList<>();
        for (Approval approval : approvals) {
            responses.add(approvalMapper.toResponse(approval));
        }

        return ResponseEntity.ok(responses);
    }

    // ✅ 내가 결재해야 할 목록 - DTO 변환
    @GetMapping("/my-approvals/{userId}")
    public ResponseEntity<List<ApprovalResponse>> getMyApprovals(@PathVariable Long userId) {
        List<Approval> approvals = approvalService.getMyApprovals(userId);

        // Entity → DTO 변환
        List<ApprovalResponse> responses = new ArrayList<>();
        for (Approval approval : approvals) {
            responses.add(approvalMapper.toResponse(approval));
        }

        return ResponseEntity.ok(responses);
    }

    // ✅ 내가 결재해야 할 대기중인 목록 - DTO 변환
    @GetMapping("/pending/{userId}")
    public ResponseEntity<List<ApprovalResponse>> getPendingApprovals(@PathVariable Long userId) {
        List<Approval> approvals = approvalService.getPendingApprovals(userId);

        // Entity → DTO 변환
        List<ApprovalResponse> responses = new ArrayList<>();
        for (Approval approval : approvals) {
            responses.add(approvalMapper.toResponse(approval));
        }

        return ResponseEntity.ok(responses);
    }

    // 🔄 결재 요청 생성 - 첨부파일 메타데이터 처리
    @PostMapping("/create")
    public ResponseEntity<?> createApproval(@RequestBody Map<String, Object> request) {
        try {
            System.out.println("=== 결재 생성 요청 받음 ===");
            System.out.println("Request body: " + request);

            // 필수 필드 null 체크
            String title = (String) request.get("title");
            if (title == null || title.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("제목은 필수입니다.");
            }

            String content = (String) request.get("content");

            // 🔄 첨부파일 메타데이터 추출
            String attachmentFilename = (String) request.get("attachmentFilename");
            String attachmentContentType = (String) request.get("attachmentContentType");
            Long attachmentSize = null;

            // attachmentSize 안전한 변환
            Object attachmentSizeObj = request.get("attachmentSize");
            if (attachmentSizeObj != null) {
                try {
                    attachmentSize = Long.valueOf(attachmentSizeObj.toString());
                } catch (NumberFormatException e) {
                    return ResponseEntity.badRequest().body("첨부파일 크기는 숫자여야 합니다.");
                }
            }

            // requesterId null 체크
            Object requesterIdObj = request.get("requesterId");
            if (requesterIdObj == null) {
                return ResponseEntity.badRequest().body("요청자 ID는 필수입니다.");
            }

            // approverId null 체크
            Object approverIdObj = request.get("approverId");
            if (approverIdObj == null) {
                return ResponseEntity.badRequest().body("승인자 ID는 필수입니다.");
            }

            // 안전한 Long 변환
            Long requesterId = Long.valueOf(requesterIdObj.toString());
            Long approverId = Long.valueOf(approverIdObj.toString());

            System.out.println("파라미터 파싱 완료 - title: " + title + ", requesterId: " + requesterId +
                    ", approverId: " + approverId + ", attachmentFilename: " + attachmentFilename);

            // 🔄 새로운 Service 메서드 호출
            Approval approval;
            if (attachmentFilename != null && !attachmentFilename.trim().isEmpty()) {
                // 첨부파일 있는 경우
                approval = approvalService.createApproval(title, content, requesterId, approverId,
                        attachmentFilename, attachmentContentType, attachmentSize);
            } else {
                // 첨부파일 없는 경우
                approval = approvalService.createApproval(title, content, requesterId, approverId);
            }

            // Entity → DTO 변환
            ApprovalResponse response = approvalMapper.toResponse(approval);

            System.out.println("결재 생성 성공: " + approval.getId());
            return ResponseEntity.ok(response);

        } catch (NumberFormatException e) {
            System.err.println("ID 형식 오류: " + e.getMessage());
            return ResponseEntity.badRequest().body("requesterId와 approverId는 숫자여야 합니다.");

        } catch (Exception e) {
            System.err.println("결재 생성 실패: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("결재 생성 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // ✅ 결재 승인 - DTO 변환
    @PostMapping("/approve/{approvalId}")
    public ResponseEntity<?> approveRequest(@PathVariable Long approvalId, @RequestBody Map<String, Object> request) {
        try {
            Object approverIdObj = request.get("approverId");
            if (approverIdObj == null) {
                return ResponseEntity.badRequest().body("승인자 ID는 필수입니다.");
            }

            Long approverId = Long.valueOf(approverIdObj.toString());
            Approval approval = approvalService.approveRequest(approvalId, approverId);

            // Entity → DTO 변환
            ApprovalResponse response = approvalMapper.toResponse(approval);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("결재 승인 실패: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("결재 승인 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // ✅ 결재 거부 - DTO 변환
    @PostMapping("/reject/{approvalId}")
    public ResponseEntity<?> rejectRequest(@PathVariable Long approvalId, @RequestBody Map<String, Object> request) {
        try {
            Object approverIdObj = request.get("approverId");
            if (approverIdObj == null) {
                return ResponseEntity.badRequest().body("승인자 ID는 필수입니다.");
            }

            Long approverId = Long.valueOf(approverIdObj.toString());
            String rejectionReason = (String) request.get("rejectionReason");

            Approval approval = approvalService.rejectRequest(approvalId, approverId, rejectionReason);

            // Entity → DTO 변환
            ApprovalResponse response = approvalMapper.toResponse(approval);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("결재 거부 실패: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("결재 거부 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // ✅ 내가 요청한 결재 삭제 (요청자만 가능)
    @DeleteMapping("/my-request/{approvalId}")
    public ResponseEntity<?> deleteMyRequest(@PathVariable Long approvalId, @RequestBody Map<String, Object> request) {
        try {
            Object requesterIdObj = request.get("requesterId");
            if (requesterIdObj == null) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "요청자 ID는 필수입니다.",
                        "approvalId", approvalId
                ));
            }

            Long requesterId = Long.valueOf(requesterIdObj.toString());

            approvalService.deleteMyRequest(approvalId, requesterId);

            return ResponseEntity.ok()
                    .body(Map.of(
                            "success", true,
                            "message", "결재 요청이 성공적으로 삭제되었습니다.",
                            "deletedApprovalId", approvalId
                    ));

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "success", false,
                            "message", e.getMessage(),
                            "approvalId", approvalId
                    ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "message", "결재 삭제 중 오류가 발생했습니다.",
                            "error", e.getMessage()
                    ));
        }
    }


    // ✅ 결재 상세 조회 - DTO 변환
    @GetMapping("/{approvalId}")
    public ResponseEntity<ApprovalResponse> getApprovalDetail(@PathVariable Long approvalId) {
        try {
            Approval approval = approvalService.getApprovalById(approvalId);
            ApprovalResponse response = approvalMapper.toResponse(approval);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // 🆕 제목으로 검색 - DTO 변환
    @GetMapping("/search")
    public ResponseEntity<List<ApprovalResponse>> searchByTitle(@RequestParam String title) {
        List<Approval> approvals = approvalService.searchByTitle(title);

        List<ApprovalResponse> responses = new ArrayList<>();
        for (Approval approval : approvals) {
            responses.add(approvalMapper.toResponse(approval));
        }

        return ResponseEntity.ok(responses);
    }

    // 🆕 최근 7일간의 결재 목록 - DTO 변환
    @GetMapping("/recent")
    public ResponseEntity<List<ApprovalResponse>> getRecentApprovals() {
        List<Approval> approvals = approvalService.getRecentApprovals();

        List<ApprovalResponse> responses = new ArrayList<>();
        for (Approval approval : approvals) {
            responses.add(approvalMapper.toResponse(approval));
        }

        return ResponseEntity.ok(responses);
    }
}
