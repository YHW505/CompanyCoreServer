package com.example.companycoreserver.service;

import com.example.companycoreserver.dto.MessageRequest;
import com.example.companycoreserver.dto.MessageResponse;
import com.example.companycoreserver.dto.MessageSummaryResponse;
import com.example.companycoreserver.entity.Message;
import com.example.companycoreserver.entity.User;
import com.example.companycoreserver.entity.Enum.MessageType;
import com.example.companycoreserver.repository.MessageRepository;
import com.example.companycoreserver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    // ✅ 1. 메시지 전송
    public MessageResponse sendMessage(MessageRequest request, Long senderId) {
        // ✅ 발신자는 ID로 조회 (헤더에서 받은 값)
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("발신자를 찾을 수 없습니다"));

        // ✅ 수신자는 이메일로 조회 (DTO에서 받은 값)
        User receiver = userRepository.findByEmail(request.getReceiverEmail())
                .orElseThrow(() -> new RuntimeException("수신자를 찾을 수 없습니다"));

        MessageType messageType = MessageType.valueOf(request.getMessageType());

        // ✅ 실제 ID 값들로 메시지 생성
        Message message = new Message(senderId, receiver.getUserId(), messageType,
                request.getTitle(), request.getContent());

        Message savedMessage = messageRepository.save(message);
        return convertToMessageResponse(savedMessage, sender, receiver);
    }


    // ✅ 2. 메시지 조회 (통합) - 새로 추가된 메서드
    public List<MessageSummaryResponse> getMessages(Long userId, String type, String messageType,
                                                    String keyword, boolean unreadOnly) {
        List<Message> messages;

        // 타입별 조회
        switch (type.toLowerCase()) {
            case "received":
                messages = messageRepository.findByReceiverIdOrderBySentAtDesc(userId);
                break;
            case "sent":
                messages = messageRepository.findBySenderIdOrderBySentAtDesc(userId);
                break;
            case "all":
                // 관리자용 - 모든 메시지 (받은 것 + 보낸 것)
                List<Message> received = messageRepository.findByReceiverIdOrderBySentAtDesc(userId);
                List<Message> sent = messageRepository.findBySenderIdOrderBySentAtDesc(userId);
                messages = new java.util.ArrayList<>(received);
                messages.addAll(sent);
                messages = messages.stream()
                        .sorted((m1, m2) -> m2.getSentAt().compareTo(m1.getSentAt()))
                        .collect(Collectors.toList());
                break;
            default:
                messages = messageRepository.findByReceiverIdOrderBySentAtDesc(userId);
        }

        // 메시지 타입 필터링
        if (messageType != null && !messageType.isEmpty()) {
            MessageType msgType = MessageType.valueOf(messageType.toUpperCase());
            messages = messages.stream()
                    .filter(m -> m.getMessageType() == msgType)
                    .collect(Collectors.toList());
        }

        // 키워드 검색
        if (keyword != null && !keyword.isEmpty()) {
            messages = messages.stream()
                    .filter(m -> m.getTitle().toLowerCase().contains(keyword.toLowerCase()) ||
                            m.getContent().toLowerCase().contains(keyword.toLowerCase()))
                    .collect(Collectors.toList());
        }

        // 읽지 않은 메시지만 필터링
        if (unreadOnly) {
            messages = messages.stream()
                    .filter(m -> !m.getIsRead())
                    .collect(Collectors.toList());
        }

        return messages.stream()
                .map(this::convertToMessageSummaryResponse)
                .collect(Collectors.toList());
    }

    // ✅ 3. ID로 메시지 상세 조회
    public MessageResponse getMessageById(Long messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("메시지를 찾을 수 없습니다"));

        User sender = userRepository.findById(message.getSenderId())
                .orElseThrow(() -> new RuntimeException("발신자 정보를 찾을 수 없습니다"));

        User receiver = userRepository.findById(message.getReceiverId())
                .orElseThrow(() -> new RuntimeException("수신자 정보를 찾을 수 없습니다"));

        return convertToMessageResponse(message, sender, receiver);
    }

    // ✅ 4. 메시지 읽음 처리
    public MessageResponse markAsRead(Long messageId, Long userId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("메시지를 찾을 수 없습니다"));

        if (!message.getReceiverId().equals(userId)) {
            throw new RuntimeException("메시지 읽음 권한이 없습니다.");
        }

        message.setIsRead(true);
        Message savedMessage = messageRepository.save(message);

        User sender = userRepository.findById(savedMessage.getSenderId())
                .orElseThrow(() -> new RuntimeException("발신자 정보를 찾을 수 없습니다"));

        User receiver = userRepository.findById(savedMessage.getReceiverId())
                .orElseThrow(() -> new RuntimeException("수신자 정보를 찾을 수 없습니다"));

        return convertToMessageResponse(savedMessage, sender, receiver);
    }

    // ✅ 5. 메시지 삭제
    public void deleteMessage(Long messageId, Long userId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("메시지를 찾을 수 없습니다"));

        if (!message.getSenderId().equals(userId) && !message.getReceiverId().equals(userId)) {
            throw new RuntimeException("메시지 삭제 권한이 없습니다.");
        }

        messageRepository.delete(message);
    }

    // ✅ 6. 메시지 일괄 처리 - 새로 추가된 메서드
    public int bulkUpdateMessages(List<Long> messageIds, String action, Long userId) {
        int successCount = 0;

        for (Long messageId : messageIds) {
            try {
                if ("read".equals(action)) {
                    markAsRead(messageId, userId);
                    successCount++;
                } else if ("delete".equals(action)) {
                    deleteMessage(messageId, userId);
                    successCount++;
                }
            } catch (Exception e) {
                // 개별 메시지 처리 실패 시 로그만 남기고 계속 진행
                System.err.println("메시지 " + messageId + " 처리 실패: " + e.getMessage());
            }
        }

        return successCount;
    }

    // ✅ 7. 메시지 답장
    public MessageResponse replyMessage(Long originalMessageId, Long senderId, String title, String content) {
        Message originalMessage = messageRepository.findById(originalMessageId)
                .orElseThrow(() -> new RuntimeException("원본 메시지를 찾을 수 없습니다"));

        Long receiverId = originalMessage.getSenderId();

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("발신자를 찾을 수 없습니다"));

        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new RuntimeException("수신자를 찾을 수 없습니다"));

        Message replyMessage = new Message(senderId, receiverId, MessageType.MESSAGE, title, content);
        Message savedMessage = messageRepository.save(replyMessage);

        return convertToMessageResponse(savedMessage, sender, receiver);
    }

    // ✅ 8. 사용자 간 대화 조회
    public List<MessageSummaryResponse> getConversation(Long userId1, Long userId2) {
        List<Message> messages = messageRepository.findConversationBetweenUsers(userId1, userId2);
        return messages.stream()
                .map(this::convertToMessageSummaryResponse)
                .collect(Collectors.toList());
    }

    // ✅ 9. 메시지 대시보드 (통합 통계) - 기존 메서드 확장
    public Map<String, Object> getMessageDashboard(Long userId) {
        Map<String, Object> dashboard = new HashMap<>();

        // 받은 메시지 통계
        List<Message> receivedMessages = messageRepository.findByReceiverIdOrderBySentAtDesc(userId);
        List<Message> unreadMessages = messageRepository.findByReceiverIdAndIsReadFalseOrderBySentAtDesc(userId);

        // 보낸 메시지 통계
        List<Message> sentMessages = messageRepository.findBySenderIdOrderBySentAtDesc(userId);

        // 오늘 받은 메시지
        LocalDateTime todayStart = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime todayEnd = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);
        List<Message> todayReceived = messageRepository.findMessagesBetweenDates(userId, todayStart, todayEnd);

        // 타입별 통계
        long messageCount = receivedMessages.stream().filter(m -> m.getMessageType() == MessageType.MESSAGE).count();
        long emailCount = receivedMessages.stream().filter(m -> m.getMessageType() == MessageType.EMAIL).count();
//        long noticeCount = receivedMessages.stream().filter(m -> m.getMessageType() == MessageType.NOTICE).count();

        // 최근 읽지 않은 메시지 (DTO로 변환)
        List<MessageSummaryResponse> recentMessages = unreadMessages.stream()
                .limit(5)
                .map(this::convertToMessageSummaryResponse)
                .collect(Collectors.toList());

        // 통계 정보 추가
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("readRate", receivedMessages.isEmpty() ? 0 :
                (double)(receivedMessages.size() - unreadMessages.size()) / receivedMessages.size() * 100);
        statistics.put("avgMessagesPerDay", calculateAvgMessagesPerDay(receivedMessages));

        dashboard.put("userId", userId);
        dashboard.put("date", LocalDateTime.now());
        dashboard.put("unreadCount", unreadMessages.size());
        dashboard.put("totalReceivedCount", receivedMessages.size());
        dashboard.put("totalSentCount", sentMessages.size());
        dashboard.put("todayReceivedCount", todayReceived.size());
        dashboard.put("messageTypeCount", messageCount);
        dashboard.put("emailTypeCount", emailCount);
//        dashboard.put("noticeTypeCount", noticeCount);
        dashboard.put("recentMessages", recentMessages);
        dashboard.put("statistics", statistics);

        return dashboard;
    }

    // ========== 헬퍼 메서드들 ==========

    // 일평균 메시지 수 계산
    private double calculateAvgMessagesPerDay(List<Message> messages) {
        if (messages.isEmpty()) return 0.0;

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime thirtyDaysAgo = now.minusDays(30);

        long recentMessages = messages.stream()
                .filter(m -> m.getSentAt().isAfter(thirtyDaysAgo))
                .count();

        return (double) recentMessages / 30;
    }

    // ========== DTO 변환 메서드들 ==========

    // Message -> MessageResponse 변환
    private MessageResponse convertToMessageResponse(Message message, User sender, User receiver) {
        MessageResponse response = new MessageResponse();
        response.setMessageId(message.getMessageId());
        response.setSenderId(message.getSenderId());
        response.setReceiverId(message.getReceiverId());
        response.setMessageType(message.getMessageType().name());
        response.setTitle(message.getTitle());
        response.setContent(message.getContent());
        response.setRead(message.getIsRead());
        response.setSentAt(message.getSentAt());

        // 발신자 정보
        response.setSenderName(sender.getUsername());
        response.setSenderEmployeeCode(sender.getEmployeeCode());
        response.setSenderEmail(sender.getEmail());
        if (sender.getPosition() != null) {
            response.setSenderPositionName(sender.getPosition().getPositionName());
        }
        if (sender.getDepartment() != null) {
            response.setSenderDepartmentName(sender.getDepartment().getDepartmentName());
        }

        // 수신자 정보
        response.setReceiverName(receiver.getUsername());
        response.setReceiverEmployeeCode(receiver.getEmployeeCode());
        response.setReceiverEmail(receiver.getEmail());
        if (receiver.getPosition() != null) {
            response.setReceiverPositionName(receiver.getPosition().getPositionName());
        }
        if (receiver.getDepartment() != null) {
            response.setReceiverDepartmentName(receiver.getDepartment().getDepartmentName());
        }

        return response;
    }

    // Message -> MessageSummaryResponse 변환
    private MessageSummaryResponse convertToMessageSummaryResponse(Message message) {
        MessageSummaryResponse response = new MessageSummaryResponse();
        response.setMessageId(message.getMessageId());
        response.setTitle(message.getTitle());

        // 내용 요약 (50자 제한)
        String content = message.getContent();
        if (content.length() > 50) {
            content = content.substring(0, 50) + "...";
        }
        response.setContent(content);

        response.setRead(message.getIsRead());
        response.setSentAt(message.getSentAt());
        response.setMessageType(message.getMessageType().name());

        // 발신자/수신자 이름 조회
        try {
            User sender = userRepository.findById(message.getSenderId()).orElse(null);
            User receiver = userRepository.findById(message.getReceiverId()).orElse(null);

            response.setSenderName(sender != null ? sender.getUsername() : "알 수 없음");
            response.setReceiverName(receiver != null ? receiver.getUsername() : "알 수 없음");
        } catch (Exception e) {
            response.setSenderName("알 수 없음");
            response.setReceiverName("알 수 없음");
        }

        return response;
    }

    // ========== 기존 메서드들 (호환성을 위해 유지) ==========

    // 읽지 않은 메시지 개수 조회
    public Long getUnreadCount(Long userId) {
        return messageRepository.countUnreadMessages(userId);
    }

    // 공지사항 조회
//    public List<MessageSummaryResponse> getNotices() {
//        return messageRepository.findAll().stream()
//                .filter(m -> m.getMessageType() == MessageType.NOTICE)
//                .sorted((m1, m2) -> m2.getSentAt().compareTo(m1.getSentAt()))
//                .map(this::convertToMessageSummaryResponse)
//                .collect(Collectors.toList());
//    }
}
