package com.example.companycoreserver.controller;

import com.example.companycoreserver.dto.MessageRequest;
import com.example.companycoreserver.dto.MessageResponse;
import com.example.companycoreserver.dto.MessageSummaryResponse;
import com.example.companycoreserver.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    // ✅ 1. 메시지 전송
    @PostMapping
    public ResponseEntity<Map<String, Object>> sendMessage(
            @RequestBody MessageRequest request,
            @RequestHeader("User-Id") Long userId) {

        Map<String, Object> response = new HashMap<>();

        try {
            MessageResponse message = messageService.sendMessage(userId, request);

            response.put("success", true);
            response.put("data", message);
            response.put("message", "메시지가 성공적으로 전송되었습니다.");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "메시지 전송 실패: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
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

    // ✅ 10. 첨부파일 다운로드 (Base64 반환)
    @GetMapping("/{messageId}/attachment")
    public ResponseEntity<Map<String, Object>> downloadAttachment(
            @PathVariable Integer messageId,
            @RequestHeader("User-Id") Long userId) {

        Map<String, Object> response = new HashMap<>();

        try {
            Map<String, Object> attachment = messageService.downloadAttachment(messageId, userId);

            response.put("success", true);
            response.put("data", attachment);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "첨부파일 다운로드 실패: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
