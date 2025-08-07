package com.example.companycoreserver.service;

import com.example.companycoreserver.dto.MessageRequest;
import com.example.companycoreserver.dto.MessageResponse;
import com.example.companycoreserver.dto.MessageSummaryResponse;
import com.example.companycoreserver.entity.Enum.MessageType;
import com.example.companycoreserver.entity.Message;
import com.example.companycoreserver.entity.User;
import com.example.companycoreserver.repository.MessageRepository;
import com.example.companycoreserver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    // ✅ 1. 메시지 전송
    // MessageService의 sendMessage 메소드 수정
    @Transactional
    public MessageResponse sendMessage(MessageRequest request, Long senderId) {
        System.out.println("메시지 전송 요청: 제목=" + request.getTitle() + ", 발신자ID=" + senderId);

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("발신자를 찾을 수 없습니다"));

        User receiver = userRepository.findByEmail(request.getReceiverEmail())
                .orElseThrow(() -> new RuntimeException("수신자를 찾을 수 없습니다"));



        Message message = new Message(
                senderId,
                receiver.getUserId(),
                request.getMessageType(), // ✅ enum 사용
                request.getTitle(),
                request.getContent()
        );

        // 📎 Base64 첨부파일 처리
        if (request.getAttachmentContent() != null && !request.getAttachmentContent().trim().isEmpty()) {
            try {
                byte[] fileData = java.util.Base64.getDecoder().decode(request.getAttachmentContent());

                message.setAttachmentFilename(request.getAttachmentFileName());
                message.setAttachmentContentType(request.getAttachmentContentType());
                message.setAttachmentContent(request.getAttachmentContent());
                message.setAttachmentSize((long) fileData.length);

                System.out.println("첨부파일 처리 완료: " + request.getAttachmentFileName());
            } catch (Exception e) {
                System.err.println("첨부파일 Base64 디코딩 실패: " + e.getMessage());
            }
        }

        Message savedMessage = messageRepository.save(message);
        return convertToMessageResponse(savedMessage, sender, receiver);
    }


    // ✅ 2. 메시지 목록 조회
    public List<MessageSummaryResponse> getMessages(Long userId, String type, String keyword, boolean unreadOnly) {
        List<Message> messages;

        switch (type.toLowerCase()) {
            case "sent":
                messages = messageRepository.findBySenderIdOrderBySentAtDesc(userId);
                break;
            case "unread":
                messages = messageRepository.findByReceiverIdAndIsReadFalseOrderBySentAtDesc(userId);
                break;
            case "received":
            default:
                messages = messageRepository.findByReceiverIdOrderBySentAtDesc(userId);
                break;
        }

        if (keyword != null && !keyword.trim().isEmpty()) {
            messages = messages.stream()
                    .filter(m -> m.getTitle().contains(keyword) || m.getContent().contains(keyword))
                    .collect(Collectors.toList());
        }

        if (unreadOnly) {
            messages = messages.stream()
                    .filter(m -> !m.getIsRead())
                    .collect(Collectors.toList());
        }

        return messages.stream()
                .map(this::convertToMessageSummaryResponse)
                .collect(Collectors.toList());
    }

    // ✅ 3. 메시지 상세 조회
    public MessageResponse getMessageById(Integer id) {
        Message message = messageRepository.findByMessageId(id)
                .orElseThrow(() -> new RuntimeException("메시지를 찾을 수 없습니다"));

        User sender = userRepository.findById(message.getSenderId())
                .orElseThrow(() -> new RuntimeException("발신자 정보를 찾을 수 없습니다"));

        User receiver = userRepository.findById(message.getReceiverId())
                .orElseThrow(() -> new RuntimeException("수신자 정보를 찾을 수 없습니다"));

        return convertToMessageResponse(message, sender, receiver);
    }

    // ✅ 4. 메시지 읽음 처리
    @Transactional
    public MessageResponse markAsRead(Integer id, Long userId) {
        Message message = messageRepository.findByMessageId(id)
                .orElseThrow(() -> new RuntimeException("메시지를 찾을 수 없습니다"));

        if (!message.getReceiverId().equals(userId)) {
            throw new RuntimeException("메시지를 읽을 권한이 없습니다");
        }

        message.setIsRead(true);
        Message savedMessage = messageRepository.save(message);

        User sender = userRepository.findById(message.getSenderId()).orElse(null);
        User receiver = userRepository.findById(message.getReceiverId()).orElse(null);

        return convertToMessageResponse(savedMessage, sender, receiver);
    }

    // ✅ 5. 메시지 삭제
    @Transactional
    public void deleteMessage(Integer id, Long userId) {
        Message message = messageRepository.findByMessageId(id)
                .orElseThrow(() -> new RuntimeException("메시지를 찾을 수 없습니다"));

        if (!message.getSenderId().equals(userId) && !message.getReceiverId().equals(userId)) {
            throw new RuntimeException("메시지를 삭제할 권한이 없습니다");
        }

        messageRepository.delete(message);
    }

    // ✅ 6. 첨부파일 다운로드
    public String downloadAttachment(Integer messageId, Long userId) {
        Message message = messageRepository.findByMessageId(messageId)
                .orElseThrow(() -> new RuntimeException("메시지를 찾을 수 없습니다"));

        if (!message.getSenderId().equals(userId) && !message.getReceiverId().equals(userId)) {
            throw new RuntimeException("첨부파일을 다운로드할 권한이 없습니다");
        }

        if (!message.hasAttachment()) {
            throw new RuntimeException("첨부파일이 없습니다");
        }

        return message.getAttachmentContent();
    }

    // ✅ 7. 읽지 않은 메시지 개수
    public Long getUnreadCount(Long userId) {
        return messageRepository.countUnreadMessages(userId);
    }

    // ✅ 8. 메시지 대시보드
    public Map<String, Object> getMessageDashboard(Long userId) {
        Map<String, Object> dashboard = new HashMap<>();

        Long totalReceived = (long) messageRepository.findByReceiverIdOrderBySentAtDesc(userId).size();
        Long totalSent = (long) messageRepository.findBySenderIdOrderBySentAtDesc(userId).size();
        Long unreadCount = messageRepository.countUnreadMessages(userId);

        dashboard.put("totalReceived", totalReceived);
        dashboard.put("totalSent", totalSent);
        dashboard.put("unreadCount", unreadCount);
        dashboard.put("totalMessages", totalReceived + totalSent);

        return dashboard;
    }

    // ========== 변환 메소드들 ==========
    private MessageResponse convertToMessageResponse(Message message, User sender, User receiver) {
        MessageResponse response = new MessageResponse();

        // Message 엔티티 필드 매핑
        response.setMessageId(message.getMessageId());
        response.setSenderId(message.getSenderId());
        response.setReceiverId(message.getReceiverId());
        response.setMessageType(message.getMessageType().toString());
        response.setTitle(message.getTitle());
        response.setContent(message.getContent());
        response.setRead(message.getIsRead());
        response.setSentAt(message.getSentAt());

        // 첨부파일 정보
        response.setHasAttachment(message.hasAttachment());
        response.setAttachmentContentType(message.getAttachmentContentType());
        response.setAttachmentSize(message.getAttachmentSize());
        response.setAttachmentFileName(message.getAttachmentFilename());

        // 발신자 정보 - ✅ getUsername() 사용
        if (sender != null) {
            response.setSenderName(sender.getUsername()); // ✅ getName() → getUsername()
            response.setSenderEmployeeCode(sender.getEmployeeCode());
            response.setSenderPositionName(sender.getPosition() != null ? sender.getPosition().getPositionName() : "");
            response.setSenderDepartmentName(sender.getDepartment() != null ? sender.getDepartment().getDepartmentName() : "");
            response.setSenderEmail(sender.getEmail());
        }

        // 수신자 정보 - ✅ getUsername() 사용
        if (receiver != null) {
            response.setReceiverName(receiver.getUsername()); // ✅ getName() → getUsername()
            response.setReceiverEmployeeCode(receiver.getEmployeeCode());
            response.setReceiverPositionName(receiver.getPosition() != null ? receiver.getPosition().getPositionName() : "");
            response.setReceiverDepartmentName(receiver.getDepartment() != null ? receiver.getDepartment().getDepartmentName() : "");
            response.setReceiverEmail(receiver.getEmail());
        }

        return response;
    }

    private MessageSummaryResponse convertToMessageSummaryResponse(Message message) {
        User sender = userRepository.findById(message.getSenderId()).orElse(null);
        User receiver = userRepository.findById(message.getReceiverId()).orElse(null);

        return new MessageSummaryResponse(
                message.getMessageId(),
                message.getTitle(),
                message.getContent(),
                sender != null ? sender.getUsername() : "알 수 없음", // ✅ getName() → getUsername()
                receiver != null ? receiver.getUsername() : "알 수 없음", // ✅ getName() → getUsername()
                message.getIsRead(),
                message.getSentAt(),
                message.getMessageType().toString(),
                message.hasAttachment(),
                message.getAttachmentFilename()
        );
    }
}
