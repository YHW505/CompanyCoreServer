package com.example.companycoreserver.service;

import com.example.companycoreserver.dto.MessageRequest;
import com.example.companycoreserver.dto.MessageResponse;
import com.example.companycoreserver.dto.MessageSummaryResponse;
import com.example.companycoreserver.entity.Message;
import com.example.companycoreserver.entity.User;
import com.example.companycoreserver.entity.Enum.MessageType;
import com.example.companycoreserver.repository.MessageRepository;
import com.example.companycoreserver.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    // ✅ 1. 메시지 전송
    public MessageResponse sendMessage(Long senderId, MessageRequest request) {
        // 수신자 조회
        User receiver = userRepository.findByEmail(request.getReceiverEmail())
                .orElseThrow(() -> new RuntimeException("수신자를 찾을 수 없습니다: " + request.getReceiverEmail()));

        // 발신자 조회
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("발신자를 찾을 수 없습니다: " + senderId));

        // 메시지 생성
        Message message;
        if (request.hasAttachment()) {
            message = new Message(
                    senderId,
                    receiver.getUserId(),
                    request.getMessageType(),
                    request.getTitle(),
                    request.getContent(),
                    request.getAttachmentContentType(),
                    request.getAttachmentSize(),
                    request.getAttachmentContent(),
                    request.getAttachmentFileName()
            );
        } else {
            message = new Message(
                    senderId,
                    receiver.getUserId(),
                    request.getMessageType(),
                    request.getTitle(),
                    request.getContent()
            );
        }

        Message savedMessage = messageRepository.save(message);

        return convertToMessageResponse(savedMessage, sender, receiver);
    }

    // ✅ 2. 메시지 조회 (통합)
    @Transactional(readOnly = true)
    public List<MessageSummaryResponse> getMessages(Long userId, String type, String messageType,
                                                    String keyword, boolean unreadOnly) {
        List<Message> messages = new ArrayList<>();

        switch (type.toLowerCase()) {
            case "received":
                if (unreadOnly) {
                    messages = messageRepository.findByReceiverIdAndIsReadFalseOrderBySentAtDesc(userId);
                } else {
                    messages = messageRepository.findByReceiverIdOrderBySentAtDesc(userId);
                }
                break;
            case "sent":
                messages = messageRepository.findBySenderIdOrderBySentAtDesc(userId);
                break;
            case "all":
                List<Message> received = messageRepository.findByReceiverIdOrderBySentAtDesc(userId);
                List<Message> sent = messageRepository.findBySenderIdOrderBySentAtDesc(userId);
                messages.addAll(received);
                messages.addAll(sent);
                messages.sort((m1, m2) -> m2.getSentAt().compareTo(m1.getSentAt()));
                break;
        }

        // 메시지 타입 필터링
        if (messageType != null && !messageType.isEmpty()) {
            messages = messages.stream()
                    .filter(m -> m.getMessageType().name().equals(messageType))
                    .collect(Collectors.toList());
        }

        // 키워드 검색
        if (keyword != null && !keyword.isEmpty()) {
            messages = messages.stream()
                    .filter(m -> m.getTitle().contains(keyword) || m.getContent().contains(keyword))
                    .collect(Collectors.toList());
        }

        return messages.stream()
                .map(this::convertToMessageSummaryResponse)
                .collect(Collectors.toList());
    }

    // ✅ 3. 메시지 상세 조회
    @Transactional(readOnly = true)
    public MessageResponse getMessageDetail(Integer messageId, Long userId) {
        Message message = messageRepository.findByMessageId(messageId)
                .orElseThrow(() -> new RuntimeException("메시지를 찾을 수 없습니다: " + messageId));

        // 권한 체크 (발신자 또는 수신자만 조회 가능)
        if (!message.getSenderId().equals(userId) && !message.getReceiverId().equals(userId)) {
            throw new RuntimeException("메시지 조회 권한이 없습니다.");
        }

        // 수신자가 조회할 때 읽음 처리
        if (message.getReceiverId().equals(userId) && !message.getIsRead()) {
            message.setIsRead(true);
            messageRepository.save(message);
        }

        User sender = userRepository.findById(message.getSenderId())
                .orElseThrow(() -> new RuntimeException("발신자 정보를 찾을 수 없습니다."));
        User receiver = userRepository.findById(message.getReceiverId())
                .orElseThrow(() -> new RuntimeException("수신자 정보를 찾을 수 없습니다."));

        return convertToMessageResponse(message, sender, receiver);
    }

    // ✅ 4. 메시지 상태 변경
    public void updateMessageStatus(Integer messageId, Long userId, String action) {
        Message message = messageRepository.findByMessageId(messageId)
                .orElseThrow(() -> new RuntimeException("메시지를 찾을 수 없습니다: " + messageId));

        // 권한 체크
        if (!message.getReceiverId().equals(userId)) {
            throw new RuntimeException("메시지 상태 변경 권한이 없습니다.");
        }

        switch (action.toLowerCase()) {
            case "read":
                message.setIsRead(true);
                messageRepository.save(message);
                break;
            case "delete":
                messageRepository.delete(message);
                break;
            default:
                throw new RuntimeException("지원하지 않는 액션입니다: " + action);
        }
    }

    // ✅ 5. 메시지 일괄 처리
    public int bulkUpdateMessages(List<Integer> messageIds, Long userId, String action) {
        List<Message> messages = messageIds.stream()
                .map(id -> messageRepository.findByMessageId(id)
                        .orElseThrow(() -> new RuntimeException("메시지를 찾을 수 없습니다: " + id)))
                .filter(message -> message.getReceiverId().equals(userId)) // 권한 체크
                .collect(Collectors.toList());

        switch (action.toLowerCase()) {
            case "read":
                messages.forEach(message -> message.setIsRead(true));
                messageRepository.saveAll(messages);
                break;
            case "delete":
                messageRepository.deleteAll(messages);
                break;
            default:
                throw new RuntimeException("지원하지 않는 액션입니다: " + action);
        }

        return messages.size();
    }

    // ✅ 6. 메시지 답장
    public MessageResponse replyMessage(Integer originalMessageId, Long userId, String title, String content) {
        Message originalMessage = messageRepository.findByMessageId(originalMessageId)
                .orElseThrow(() -> new RuntimeException("원본 메시지를 찾을 수 없습니다: " + originalMessageId));

        // 권한 체크 (수신자만 답장 가능)
        if (!originalMessage.getReceiverId().equals(userId)) {
            throw new RuntimeException("답장 권한이 없습니다.");
        }

        User sender = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("발신자를 찾을 수 없습니다: " + userId));
        User receiver = userRepository.findById(originalMessage.getSenderId())
                .orElseThrow(() -> new RuntimeException("수신자를 찾을 수 없습니다: " + originalMessage.getSenderId()));

        // 답장 메시지 생성
        Message replyMessage = new Message(
                userId,
                originalMessage.getSenderId(),
                MessageType.MESSAGE,
                title,
                content
        );

        Message savedMessage = messageRepository.save(replyMessage);
        return convertToMessageResponse(savedMessage, sender, receiver);
    }

    // ✅ 7. 사용자 간 대화 조회
    @Transactional(readOnly = true)
    public List<MessageSummaryResponse> getConversation(Long userId, Long otherUserId) {
        List<Message> messages = messageRepository.findConversationBetweenUsers(userId, otherUserId);

        return messages.stream()
                .map(this::convertToMessageSummaryResponse)
                .collect(Collectors.toList());
    }

    // ✅ 8. 메시지 대시보드
    @Transactional(readOnly = true)
    public Map<String, Object> getDashboard(Long userId) {
        Map<String, Object> dashboard = new HashMap<>();

        // 읽지 않은 메시지 개수
        Long unreadCount = messageRepository.countUnreadMessages(userId);
        dashboard.put("unreadCount", unreadCount);

        // 전체 받은 메시지 개수
        List<Message> receivedMessages = messageRepository.findByReceiverIdOrderBySentAtDesc(userId);
        dashboard.put("totalReceivedCount", receivedMessages.size());

        // 전체 보낸 메시지 개수
        List<Message> sentMessages = messageRepository.findBySenderIdOrderBySentAtDesc(userId);
        dashboard.put("totalSentCount", sentMessages.size());

        // 오늘 받은 메시지 개수
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endOfDay = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59).withNano(999999999);
        List<Message> todayMessages = messageRepository.findMessagesBetweenDates(userId, startOfDay, endOfDay);
        dashboard.put("todayReceivedCount", todayMessages.size());

        // 메시지 타입별 개수
        Map<String, Long> typeCount = receivedMessages.stream()
                .collect(Collectors.groupingBy(
                        message -> message.getMessageType().name(),
                        Collectors.counting()
                ));
        dashboard.put("messageTypeCount", typeCount.getOrDefault("MESSAGE", 0L));
        dashboard.put("emailTypeCount", typeCount.getOrDefault("EMAIL", 0L));
        dashboard.put("noticeTypeCount", typeCount.getOrDefault("NOTICE", 0L));

        // 최근 메시지 (최대 5개)
        List<MessageSummaryResponse> recentMessages = receivedMessages.stream()
                .limit(5)
                .map(this::convertToMessageSummaryResponse)
                .collect(Collectors.toList());
        dashboard.put("recentMessages", recentMessages);

        // 통계 정보
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("totalMessages", receivedMessages.size() + sentMessages.size());
        statistics.put("readRate", receivedMessages.isEmpty() ? 0 :
                (double)(receivedMessages.size() - unreadCount) / receivedMessages.size() * 100);
        dashboard.put("statistics", statistics);

        return dashboard;
    }

    // ✅ 9. 메시지 삭제
    public void deleteMessage(Integer messageId, Long userId) {
        Message message = messageRepository.findByMessageId(messageId)
                .orElseThrow(() -> new RuntimeException("메시지를 찾을 수 없습니다: " + messageId));

        // 권한 체크 (발신자 또는 수신자만 삭제 가능)
        if (!message.getSenderId().equals(userId) && !message.getReceiverId().equals(userId)) {
            throw new RuntimeException("메시지 삭제 권한이 없습니다.");
        }

        messageRepository.delete(message);
    }

    // ✅ 첨부파일 다운로드 (Base64 반환)
    @Transactional(readOnly = true)
    public Map<String, Object> downloadAttachment(Integer messageId, Long userId) {
        Message message = messageRepository.findByMessageId(messageId)
                .orElseThrow(() -> new RuntimeException("메시지를 찾을 수 없습니다: " + messageId));

        // 권한 체크
        if (!message.getSenderId().equals(userId) && !message.getReceiverId().equals(userId)) {
            throw new RuntimeException("첨부파일 다운로드 권한이 없습니다.");
        }

        if (!message.hasAttachment()) {
            throw new RuntimeException("첨부파일이 없습니다.");
        }

        Map<String, Object> attachment = new HashMap<>();
        attachment.put("filename", message.getAttachmentFilename());
        attachment.put("contentType", message.getAttachmentContentType());
        attachment.put("fileSize", message.getAttachmentSize());
        attachment.put("fileData", message.getAttachmentContent());

        return attachment;
    }

    // DTO 변환 메서드들
    private MessageResponse convertToMessageResponse(Message message, User sender, User receiver) {
        return new MessageResponse(
                message.getMessageId(),
                message.getSenderId(),
                message.getReceiverId(),
                message.getMessageType().name(),
                message.getTitle(),
                message.getContent(),
                message.getIsRead(),
                message.getSentAt(),
                message.hasAttachment(),
                message.getAttachmentContentType(),
                message.getAttachmentSize(),
                message.getAttachmentFilename(),
                sender.getUsername(),
                sender.getEmployeeCode(),
                sender.getPosition() != null ? sender.getPosition().getPositionName() : null,
                sender.getDepartment() != null ? sender.getDepartment().getDepartmentName() : null,
                sender.getEmail(),
                receiver.getUsername(),
                receiver.getEmployeeCode(),
                receiver.getPosition() != null ? receiver.getPosition().getPositionName() : null,
                receiver.getDepartment() != null ? receiver.getDepartment().getDepartmentName() : null,
                receiver.getEmail()
        );
    }

    private MessageSummaryResponse convertToMessageSummaryResponse(Message message) {
        User sender = userRepository.findById(message.getSenderId()).orElse(null);
        User receiver = userRepository.findById(message.getReceiverId()).orElse(null);

        return new MessageSummaryResponse(
                message.getMessageId(),
                message.getTitle(),
                message.getContent(),
                sender != null ? sender.getUsername() : "알 수 없음",
                receiver != null ? receiver.getUsername() : "알 수 없음",
                message.getIsRead(),
                message.getSentAt(),
                message.getMessageType().name(),
                message.hasAttachment(),
                message.getAttachmentFilename()
        );
    }
}
