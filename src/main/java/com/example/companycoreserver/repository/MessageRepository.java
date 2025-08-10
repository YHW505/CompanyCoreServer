// MessageRepository.java
package com.example.companycoreserver.repository;

import com.example.companycoreserver.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    // ✅ 메시지 ID로 조회 (추가!!)
    Optional<Message> findByMessageId(Integer messageId);

    // ✅ 수신자별 메시지 조회 (최신순)
    List<Message> findByReceiverIdOrderBySentAtDesc(Long receiverId);

    // ✅ 발신자별 메시지 조회 (최신순)
    List<Message> findBySenderIdOrderBySentAtDesc(Long senderId);

    // ✅ 읽지 않은 메시지 조회
    List<Message> findByReceiverIdAndIsReadFalseOrderBySentAtDesc(Long receiverId);

    // ✅ 읽은 메시지 조회
    List<Message> findByReceiverIdAndIsReadTrueOrderBySentAtDesc(Long receiverId);

    // ✅ 메시지 타입별 조회
    List<Message> findByReceiverIdAndMessageTypeOrderBySentAtDesc(Long receiverId, String messageType);

    // ✅ 발신자와 수신자 간의 대화 조회
    @Query("SELECT m FROM Message m WHERE " +
            "(m.senderId = :userId1 AND m.receiverId = :userId2) OR " +
            "(m.senderId = :userId2 AND m.receiverId = :userId1) " +
            "ORDER BY m.sentAt ASC")
    List<Message> findConversationBetweenUsers(@Param("userId1") Long userId1,
                                               @Param("userId2") Long userId2);

    // ✅ 읽지 않은 메시지 개수 조회
    @Query("SELECT COUNT(m) FROM Message m WHERE m.receiverId = :receiverId AND m.isRead = false")
    Long countUnreadMessages(@Param("receiverId") Long receiverId);

    // ✅ 특정 기간 내 메시지 조회
    @Query("SELECT m FROM Message m WHERE m.receiverId = :receiverId " +
            "AND m.sentAt BETWEEN :startDate AND :endDate " +
            "ORDER BY m.sentAt DESC")
    List<Message> findMessagesBetweenDates(@Param("receiverId") Long receiverId,
                                           @Param("startDate") java.time.LocalDateTime startDate,
                                           @Param("endDate") java.time.LocalDateTime endDate);

    // ✅ 메시지 내용으로 검색
    @Query("SELECT m FROM Message m WHERE m.receiverId = :receiverId " +
            "AND m.content LIKE %:keyword% " +
            "ORDER BY m.sentAt DESC")
    List<Message> searchMessages(@Param("receiverId") Long receiverId,
                                 @Param("keyword") String keyword);
}
