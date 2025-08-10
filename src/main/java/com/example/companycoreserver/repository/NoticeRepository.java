package com.example.companycoreserver.repository;

import com.example.companycoreserver.entity.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {

    // ✅ 제목으로 검색 (부분 일치)
    Page<Notice> findByTitleContaining(String title, Pageable pageable);

    // ✅ 작성자로 검색 (부분 일치)
    Page<Notice> findByAuthorNameContaining(String authorName, Pageable pageable);

    // ✅ 작성자 ID로 검색
    Page<Notice> findByAuthorId(Long authorId, Pageable pageable);

    // ✅ 부서로 검색 (정확히 일치)
    Page<Notice> findByAuthorDepartment(String authorDepartment, Pageable pageable);

    // ✅ 날짜 범위로 검색
    @Query("SELECT n FROM Notice n WHERE n.createdAt BETWEEN :startDate AND :endDate ORDER BY n.createdAt DESC")
    Page<Notice> findByCreatedAtBetween(@Param("startDate") LocalDateTime startDate,
                                        @Param("endDate") LocalDateTime endDate,
                                        Pageable pageable);

    // ✅ 최근 공지사항 5개
    List<Notice> findTop5ByOrderByCreatedAtDesc();

    // ✅ 키워드 검색 (제목 + 내용)
    @Query("SELECT n FROM Notice n WHERE " +
            "(LOWER(n.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(n.content) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "ORDER BY n.createdAt DESC")
    Page<Notice> findByKeyword(@Param("keyword") String keyword, Pageable pageable);

    // ✅ 기존 메서드도 유지 (호환성을 위해)
    @Query("SELECT n FROM Notice n WHERE " +
            "(LOWER(n.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(n.content) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "ORDER BY n.createdAt DESC")
    Page<Notice> findByTitleOrContentContaining(@Param("keyword") String keyword, Pageable pageable);


    // 🆕 추가: 복합 검색 (키워드 + 첨부파일 여부)
    @Query("SELECT n FROM Notice n WHERE " +
            "(LOWER(n.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(n.content) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND (:hasAttachment = false OR n.attachmentFilename IS NOT NULL) " +
            "ORDER BY n.createdAt DESC")
    Page<Notice> findByKeywordAndAttachment(@Param("keyword") String keyword,
                                            @Param("hasAttachment") boolean hasAttachment,
                                            Pageable pageable);
}
