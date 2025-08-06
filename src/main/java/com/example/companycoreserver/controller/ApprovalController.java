package com.example.companycoreserver.controller;

import com.example.companycoreserver.dto.ApprovalResponse;
import com.example.companycoreserver.entity.Approval;
import com.example.companycoreserver.mapper.ApprovalMapper;
import com.example.companycoreserver.service.ApprovalService;
import com.example.companycoreserver.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/approvals")
public class ApprovalController {

    @Autowired
    private ApprovalService approvalService;

    @Autowired
    private ApprovalMapper approvalMapper;

    @Autowired
    private JwtUtil jwtUtil;

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

    // 🆕 내가 요청한 결재 목록 (간단한 버전 - 성능 최적화)
    @GetMapping("/my-requests/{userId}/simple")
    public ResponseEntity<List<Map<String, Object>>> getMyRequestsSimple(@PathVariable Long userId) {
        List<Approval> approvals = approvalService.getMyRequests(userId);

        // 간단한 Map 형태로 변환하여 성능 최적화
        List<Map<String, Object>> responses = new ArrayList<>();
        for (Approval approval : approvals) {
            Map<String, Object> simpleResponse = new HashMap<>();
            simpleResponse.put("id", approval.getId());
            simpleResponse.put("title", approval.getTitle());
            simpleResponse.put("content", approval.getContent());
            simpleResponse.put("status", approval.getStatus());
            simpleResponse.put("requestDate", approval.getRequestDate());
            // 첨부파일 정보는 목록에서 제외 (상세보기에서만 확인)
            // simpleResponse.put("attachmentFilename", approval.getAttachmentFilename());
            // simpleResponse.put("attachmentSize", approval.getAttachmentSize());
            
            // 사용자 정보 (간단한 형태)
            if (approval.getRequester() != null) {
                Map<String, Object> requesterInfo = new HashMap<>();
                requesterInfo.put("username", approval.getRequester().getUsername());
                requesterInfo.put("department", approval.getRequester().getDepartment() != null ? 
                    approval.getRequester().getDepartment().getDepartmentName() : "Unknown");
                simpleResponse.put("requester", requesterInfo);
            }
            
            responses.add(simpleResponse);
        }

        return ResponseEntity.ok(responses);
    }

    // 🆕 내가 요청한 결재 목록 (페이지네이션 포함)
    @GetMapping("/my-requests/{userId}/page")
    public ResponseEntity<?> getMyRequestsWithPagination(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        try {
            var result = approvalService.getMyRequestsWithPagination(userId, page, size, sortBy, sortDir);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "내가 요청한 결재 목록 조회 실패: " + e.getMessage()));
        }
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

    // 🆕 내가 결재해야 할 목록 (페이지네이션 포함)
    @GetMapping("/my-approvals/{userId}/page")
    public ResponseEntity<?> getMyApprovalsWithPagination(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        try {
            var result = approvalService.getMyApprovalsWithPagination(userId, page, size, sortBy, sortDir);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "내가 결재해야 할 목록 조회 실패: " + e.getMessage()));
        }
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

    // 🆕 내가 결재해야 할 대기중인 목록 (페이지네이션 포함)
    @GetMapping("/pending/{userId}/page")
    public ResponseEntity<?> getPendingApprovalsWithPagination(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        try {
            var result = approvalService.getPendingApprovalsWithPagination(userId, page, size, sortBy, sortDir);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "대기중인 결재 목록 조회 실패: " + e.getMessage()));
        }
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
            String attachmentContent = (String) request.get("attachmentContent"); // Base64 인코딩된 첨부파일 내용
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
                        attachmentFilename, attachmentContentType, attachmentSize, attachmentContent);
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
    public ResponseEntity<?> deleteMyRequest(@PathVariable Long approvalId, HttpServletRequest request) {
        try {
            // JWT 토큰에서 사용자 ID 추출
            String token = extractTokenFromRequest(request);
            if (token == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of(
                                "success", false,
                                "message", "인증 토큰이 필요합니다."
                        ));
            }

            Long requesterId = jwtUtil.getUserIdFromToken(token);
            if (requesterId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of(
                                "success", false,
                                "message", "유효하지 않은 토큰입니다."
                        ));
            }

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

    // JWT 토큰 추출 헬퍼 메서드
    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }


    // ✅ 결재 상세 조회 - DTO 변환
    @GetMapping("/{approvalId}")
    public ResponseEntity<ApprovalResponse> getApprovalDetail(@PathVariable Long approvalId) {
        try {
            System.out.println("=== 결재 상세 조회 요청 받음 - ID: " + approvalId + " ===");
            Approval approval = approvalService.getApprovalById(approvalId);
            ApprovalResponse response = approvalMapper.toResponse(approval);
            
            System.out.println("결재 상세 조회 성공: " + approvalId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            System.err.println("결재 상세 조회 실패: " + e.getMessage());
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

    // 🆕 결재 요청 수정
    @PutMapping("/{approvalId}")
    public ResponseEntity<?> updateApproval(@PathVariable Long approvalId, @RequestBody Map<String, Object> request) {
        try {
            System.out.println("=== 결재 수정 요청 받음 - ID: " + approvalId + " ===");
            
            // 필수 필드 null 체크
            String title = (String) request.get("title");
            if (title == null || title.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("제목은 필수입니다.");
            }

            String content = (String) request.get("content");

            // 첨부파일 메타데이터 추출
            String attachmentFilename = (String) request.get("attachmentFilename");
            String attachmentContentType = (String) request.get("attachmentContentType");
            String attachmentContent = (String) request.get("attachmentContent");
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

            // 결재 수정
            Approval updatedApproval = approvalService.updateApproval(approvalId, title, content, 
                    attachmentFilename, attachmentContentType, attachmentSize, attachmentContent);

            // Entity → DTO 변환
            ApprovalResponse response = approvalMapper.toResponse(updatedApproval);

            System.out.println("결재 수정 성공: " + approvalId);
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            System.err.println("결재 수정 실패: " + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            System.err.println("결재 수정 중 오류 발생: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("결재 수정 중 오류가 발생했습니다: " + e.getMessage());
        }
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
