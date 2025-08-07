package com.example.companycoreserver.controller;

import com.example.companycoreserver.dto.MessageRequest;
import com.example.companycoreserver.dto.MessageResponse;
import com.example.companycoreserver.dto.MessageSummaryResponse;
import com.example.companycoreserver.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> sendMessage(
            @RequestHeader("User-Id") Long userId,
            @RequestBody MessageRequest requestDto) {
        try {
            System.out.println("메시지 전송 API 호출: " + requestDto.getTitle());

            MessageResponse response = messageService.createMessage(userId, requestDto);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "메시지가 성공적으로 전송되었습니다.");
            result.put("data", response);

            return ResponseEntity.status(HttpStatus.CREATED).body(result);

        } catch (Exception e) {
            System.err.println("메시지 전송 실패: " + e.getMessage());

            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "메시지 전송에 실패했습니다: " + e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResult);
        }
    }


    // ✅ 2. 메시지 조회 (통합)
    @GetMapping
    public ResponseEntity<Map<String, Object>> getMessages(
            @RequestParam(defaultValue = "received") String type,
            @RequestParam(required = false) String messageType,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "false") boolean unreadOnly,
            @RequestHeader("User-Id") Long userId) {

        Map<String, Object> response = new HashMap<>();

        try {
            List<MessageSummaryResponse> messages = messageService.getMessages(
                    userId, type, messageType, keyword, unreadOnly);

            response.put("success", true);
            response.put("data", messages);
            response.put("total", messages.size());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "메시지 조회 실패: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // ✅ 3. 메시지 상세 조회
    @GetMapping("/{messageId}")
    public ResponseEntity<Map<String, Object>> getMessageDetail(
            @PathVariable Integer messageId,
            @RequestHeader("User-Id") Long userId) {

        Map<String, Object> response = new HashMap<>();

        try {
            MessageResponse message = messageService.getMessageDetail(messageId, userId);

            response.put("success", true);
            response.put("data", message);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "메시지 조회 실패: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // ✅ 4. 메시지 상태 변경
    @PutMapping("/{messageId}")
    public ResponseEntity<Map<String, Object>> updateMessageStatus(
            @PathVariable Integer messageId,
            @RequestBody Map<String, String> request,
            @RequestHeader("User-Id") Long userId) {

        Map<String, Object> response = new HashMap<>();

        try {
            String action = request.get("action");
            messageService.updateMessageStatus(messageId, userId, action);

            response.put("success", true);
            response.put("message", action.equals("read") ? "메시지를 읽음으로 처리했습니다." : "메시지를 삭제했습니다.");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "메시지 상태 변경 실패: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // ✅ 5. 메시지 일괄 처리
    @PutMapping("/bulk")
    public ResponseEntity<Map<String, Object>> bulkUpdateMessages(
            @RequestBody Map<String, Object> request,
            @RequestHeader("User-Id") Long userId) {

        Map<String, Object> response = new HashMap<>();

        try {
            @SuppressWarnings("unchecked")
            List<Integer> messageIds = (List<Integer>) request.get("messageIds");
            String action = (String) request.get("action");

            int updatedCount = messageService.bulkUpdateMessages(messageIds, userId, action);

            response.put("success", true);
            response.put("updatedCount", updatedCount);
            response.put("message", updatedCount + "개의 메시지를 " +
                    (action.equals("read") ? "읽음으로 처리" : "삭제") + "했습니다.");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "일괄 처리 실패: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // ✅ 6. 메시지 답장
    @PostMapping("/{messageId}/reply")
    public ResponseEntity<Map<String, Object>> replyMessage(
            @PathVariable Integer messageId,
            @RequestBody Map<String, String> request,
            @RequestHeader("User-Id") Long userId) {

        Map<String, Object> response = new HashMap<>();

        try {
            String title = request.get("title");
            String content = request.get("content");

            MessageResponse reply = messageService.replyMessage(messageId, userId, title, content);

            response.put("success", true);
            response.put("data", reply);
            response.put("message", "답장이 성공적으로 전송되었습니다.");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "답장 전송 실패: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }



    // ✅ 7. 사용자 간 대화 조회
    @GetMapping("/conversation/{otherUserId}")
    public ResponseEntity<Map<String, Object>> getConversation(
            @PathVariable Long otherUserId,
            @RequestHeader("User-Id") Long userId) {

        Map<String, Object> response = new HashMap<>();

        try {
            List<MessageSummaryResponse> conversation = messageService.getConversation(userId, otherUserId);

            response.put("success", true);
            response.put("data", conversation);
            response.put("total", conversation.size());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "대화 조회 실패: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // ✅ 8. 메시지 대시보드
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboard(
            @RequestHeader("User-Id") Long userId) {

        Map<String, Object> response = new HashMap<>();

        try {
            Map<String, Object> dashboard = messageService.getDashboard(userId);

            response.put("success", true);
            response.put("data", dashboard);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "대시보드 조회 실패: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // ✅ 9. 메시지 삭제
    @DeleteMapping("/{messageId}")
    public ResponseEntity<Map<String, Object>> deleteMessage(
            @PathVariable Integer messageId,
            @RequestHeader("User-Id") Long userId) {

        Map<String, Object> response = new HashMap<>();

        try {
            messageService.deleteMessage(messageId, userId);

            response.put("success", true);
            response.put("message", "메시지가 성공적으로 삭제되었습니다.");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "메시지 삭제 실패: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * ✅ 첨부파일 업로드 (기존 메시지에 첨부파일 추가) - Base64 방식
     * POST /api/messages/{messageId}/attachment
     */
    @PostMapping("/{messageId}/attachment")
    public ResponseEntity<Map<String, Object>> uploadAttachment(
            @PathVariable Integer messageId,
            @RequestHeader("User-Id") Long userId,
            @RequestBody Map<String, Object> attachmentData) {
        try {
            System.out.println("첨부파일 업로드 API 호출: 메시지 ID=" + messageId);

            // 필수 데이터 추출
            String filename = (String) attachmentData.get("filename");
            String contentType = (String) attachmentData.get("contentType");
            String base64Content = (String) attachmentData.get("fileData");

            // 데이터 검증
            if (filename == null || filename.trim().isEmpty()) {
                Map<String, Object> errorResult = new HashMap<>();
                errorResult.put("success", false);
                errorResult.put("message", "파일명이 필요합니다.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResult);
            }

            if (base64Content == null || base64Content.trim().isEmpty()) {
                Map<String, Object> errorResult = new HashMap<>();
                errorResult.put("success", false);
                errorResult.put("message", "파일 데이터가 없습니다.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResult);
            }

            // Base64 디코딩하여 파일 크기 확인
            byte[] fileData = java.util.Base64.getDecoder().decode(base64Content);

            // 파일 크기 제한 (예: 10MB)
            long maxFileSize = 10 * 1024 * 1024; // 10MB
            if (fileData.length > maxFileSize) {
                Map<String, Object> errorResult = new HashMap<>();
                errorResult.put("success", false);
                errorResult.put("message", "파일 크기가 너무 큽니다. (최대 10MB)");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResult);
            }

            // 서비스 호출
            MessageResponse response = messageService.uploadAttachment(
                    messageId,
                    filename,
                    contentType,
                    fileData,
                    userId
            );

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "첨부파일이 성공적으로 업로드되었습니다.");
            result.put("data", response);

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            System.err.println("첨부파일 업로드 실패: " + e.getMessage());

            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "첨부파일 업로드에 실패했습니다: " + e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResult);
        }
    }


    /**
     * ✅ 첨부파일 제거
     * DELETE /api/messages/{messageId}/attachment
     */
    @DeleteMapping("/{messageId}/attachment")
    public ResponseEntity<Map<String, Object>> removeAttachment(
            @PathVariable Integer messageId,
            @RequestHeader("User-Id") Long userId) {
        try {
            System.out.println("첨부파일 제거 API 호출: 메시지 ID=" + messageId);

            MessageResponse response = messageService.removeAttachment(messageId, userId);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "첨부파일이 성공적으로 제거되었습니다.");
            result.put("data", response);

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            System.err.println("첨부파일 제거 실패: " + e.getMessage());

            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "첨부파일 제거에 실패했습니다: " + e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResult);
        }
    }

    /**
     * ✅ 첨부파일 다운로드
     * GET /api/messages/{messageId}/attachment/download
     */
    @GetMapping("/{messageId}/attachment/download")
    public ResponseEntity<?> downloadAttachment(
            @PathVariable Integer messageId,
            @RequestHeader("User-Id") Long userId) {
        try {
            System.out.println("첨부파일 다운로드 API 호출: 메시지 ID=" + messageId);

            Map<String, Object> attachment = messageService.downloadAttachment(messageId, userId);

            // Base64 디코딩
            String base64Data = (String) attachment.get("fileData");
            byte[] fileData = java.util.Base64.getDecoder().decode(base64Data);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType((String) attachment.get("contentType")));
            headers.setContentDispositionFormData("attachment", (String) attachment.get("filename"));
            headers.setContentLength(fileData.length);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(fileData);

        } catch (Exception e) {
            System.err.println("첨부파일 다운로드 실패: " + e.getMessage());

            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "첨부파일 다운로드에 실패했습니다: " + e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResult);
        }
    }

}
