package com.example.companycoreserver.controller;

import com.example.companycoreserver.dto.MessageRequest;
import com.example.companycoreserver.dto.MessageResponse;
import com.example.companycoreserver.dto.MessageSummaryResponse;
import com.example.companycoreserver.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/messages")
@CrossOrigin(origins = "*")
public class MessageController {

    @Autowired
    private MessageService messageService;

    // ✅ 1. 메시지 전송
    @PostMapping("/send")
    public ResponseEntity<Map<String, Object>> sendMessage(
            @RequestBody MessageRequest request,
            @RequestHeader("User-Id") Long userId) {

        Map<String, Object> response = new HashMap<>();

        try {
            MessageResponse messageResponse = messageService.sendMessage(request, userId);

            response.put("success", true);
            response.put("message", "메시지가 성공적으로 전송되었습니다");
            response.put("data", messageResponse);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "메시지 전송 실패: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // ✅ 2. 메시지 목록 조회
    @GetMapping
    public ResponseEntity<Map<String, Object>> getMessages(
            @RequestParam(defaultValue = "received") String type,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "false") boolean unreadOnly,
            @RequestHeader("User-Id") Long userId) {

        Map<String, Object> response = new HashMap<>();

        try {
            List<MessageSummaryResponse> messages = messageService.getMessages(userId, type, keyword, unreadOnly);

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
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getMessageById(
            @PathVariable Integer id,
            @RequestHeader("User-Id") Long userId) {

        Map<String, Object> response = new HashMap<>();

        try {
            MessageResponse message = messageService.getMessageById(id);

            // 권한 확인
            if (!message.getSenderId().equals(userId) && !message.getReceiverId().equals(userId)) {
                response.put("success", false);
                response.put("message", "메시지를 조회할 권한이 없습니다");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }

            response.put("success", true);
            response.put("data", message);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "메시지 조회 실패: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // ✅ 4. 메시지 읽음 처리
    @PutMapping("/{id}/read")
    public ResponseEntity<Map<String, Object>> markAsRead(
            @PathVariable Integer id,
            @RequestHeader("User-Id") Long userId) {

        Map<String, Object> response = new HashMap<>();

        try {
            MessageResponse message = messageService.markAsRead(id, userId);

            response.put("success", true);
            response.put("message", "메시지를 읽음으로 처리했습니다");
            response.put("data", message);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "읽음 처리 실패: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // ✅ 5. 메시지 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteMessage(
            @PathVariable Integer id,
            @RequestHeader("User-Id") Long userId) {

        Map<String, Object> response = new HashMap<>();

        try {
            messageService.deleteMessage(id, userId);

            response.put("success", true);
            response.put("message", "메시지가 삭제되었습니다");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "메시지 삭제 실패: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // ✅ 6. 첨부파일 다운로드
    @GetMapping("/{id}/attachment")
    public ResponseEntity<Map<String, Object>> downloadAttachment(
            @PathVariable Integer id,
            @RequestHeader("User-Id") Long userId) {

        Map<String, Object> response = new HashMap<>();

        try {
            MessageResponse message = messageService.getMessageById(id);

            if (!message.getSenderId().equals(userId) && !message.getReceiverId().equals(userId)) {
                response.put("success", false);
                response.put("message", "첨부파일을 다운로드할 권한이 없습니다");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }

            if (!message.isHasAttachment()) {
                response.put("success", false);
                response.put("message", "첨부파일이 없습니다");
                return ResponseEntity.badRequest().body(response);
            }

            String base64Content = messageService.downloadAttachment(id, userId);

            Map<String, Object> attachmentData = new HashMap<>();
            attachmentData.put("filename", message.getAttachmentFileName());
            attachmentData.put("contentType", message.getAttachmentContentType());
            attachmentData.put("content", base64Content);
            attachmentData.put("size", message.getAttachmentSize());

            response.put("success", true);
            response.put("data", attachmentData);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "첨부파일 다운로드 실패: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // ✅ 7. 읽지 않은 메시지 개수
    @GetMapping("/unread/count")
    public ResponseEntity<Map<String, Object>> getUnreadCount(
            @RequestHeader("User-Id") Long userId) {

        Map<String, Object> response = new HashMap<>();

        try {
            Long unreadCount = messageService.getUnreadCount(userId);

            response.put("success", true);
            response.put("unreadCount", unreadCount);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "읽지 않은 메시지 개수 조회 실패: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // ✅ 8. 메시지 대시보드
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getMessageDashboard(
            @RequestHeader("User-Id") Long userId) {

        Map<String, Object> response = new HashMap<>();

        try {
            Map<String, Object> dashboard = messageService.getMessageDashboard(userId);

            response.put("success", true);
            response.put("data", dashboard);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "대시보드 조회 실패: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
