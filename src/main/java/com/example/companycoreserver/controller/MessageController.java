package com.example.companycoreserver.controller;

import com.example.companycoreserver.dto.MessageRequest;
import com.example.companycoreserver.dto.MessageResponse;
import com.example.companycoreserver.dto.MessageSummaryResponse;
import com.example.companycoreserver.entity.Enum.MessageType;
import com.example.companycoreserver.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/messages")
@CrossOrigin(origins = "*")
public class MessageController {

    @Autowired
    private MessageService messageService;

    // ✅ 1. 메시지 전송
    @PostMapping
    public ResponseEntity<?> sendMessage(@RequestBody MessageRequest request,
                                         @RequestHeader("User-Id") Long senderId) {
        try {
            MessageResponse response = messageService.sendMessage(request, senderId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "메시지 전송 실패", "message", e.getMessage()
            ));
        }
    }

    // ✅ 2. 메시지 조회 (통합)
    @GetMapping
    public ResponseEntity<List<MessageSummaryResponse>> getMessages(
            @RequestHeader("User-Id") Long userId,
            @RequestParam(defaultValue = "received") String type,
            @RequestParam(required = false) String messageType,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "false") boolean unreadOnly) {

        List<MessageSummaryResponse> messages = messageService.getMessages(
                userId, type, messageType, keyword, unreadOnly);
        return ResponseEntity.ok(messages);
    }

    // ✅ 3. 메시지 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<?> getMessageById(@PathVariable Long id) {
        try {
            MessageResponse message = messageService.getMessageById(id);
            return ResponseEntity.ok(message);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ✅ 4. 메시지 상태 변경 (읽음/삭제)
    @PutMapping("/{id}")
    public ResponseEntity<?> updateMessage(@PathVariable Long id,
                                           @RequestBody Map<String, String> request,
                                           @RequestHeader("User-Id") Long userId) {
        try {
            String action = request.get("action");
            if ("read".equals(action)) {
                MessageResponse message = messageService.markAsRead(id, userId);
                return ResponseEntity.ok(message);
            } else if ("delete".equals(action)) {
                messageService.deleteMessage(id, userId);
                return ResponseEntity.ok(Map.of("success", true, "message", "메시지가 삭제되었습니다."));
            } else {
                return ResponseEntity.badRequest().body(Map.of("error", "잘못된 액션"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ✅ 5. 메시지 일괄 처리
    @PutMapping("/bulk")
    public ResponseEntity<?> bulkUpdateMessages(@RequestBody Map<String, Object> request,
                                                @RequestHeader("User-Id") Long userId) {
        try {
            @SuppressWarnings("unchecked")
            List<Long> messageIds = (List<Long>) request.get("messageIds");
            String action = request.get("action").toString();

            int successCount = messageService.bulkUpdateMessages(messageIds, action, userId);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "processedCount", successCount,
                    "totalCount", messageIds.size()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ✅ 6. 메시지 답장
    @PostMapping("/{id}/reply")
    public ResponseEntity<?> replyMessage(@PathVariable Long id,
                                          @RequestBody Map<String, String> request,
                                          @RequestHeader("User-Id") Long senderId) {
        try {
            String title = request.get("title");
            String content = request.get("content");

            MessageResponse replyMessage = messageService.replyMessage(id, senderId, title, content);
            return ResponseEntity.ok(replyMessage);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ✅ 7. 사용자 간 대화 조회
    @GetMapping("/conversation/{otherUserId}")
    public ResponseEntity<List<MessageSummaryResponse>> getConversation(
            @PathVariable Long otherUserId,
            @RequestHeader("User-Id") Long userId) {
        List<MessageSummaryResponse> conversation = messageService.getConversation(userId, otherUserId);
        return ResponseEntity.ok(conversation);
    }

    // ✅ 8. 메시지 대시보드 (통합 통계)
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getMessageDashboard(
            @RequestHeader("User-Id") Long userId) {
        Map<String, Object> dashboard = messageService.getMessageDashboard(userId);
        return ResponseEntity.ok(dashboard);
    }
}
