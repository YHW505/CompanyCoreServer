package com.example.companycoreserver.service;

import com.example.companycoreserver.dto.NoticeRequest;
import com.example.companycoreserver.dto.NoticeResponse;
import com.example.companycoreserver.entity.Notice;
import com.example.companycoreserver.repository.NoticeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

@Service
@Transactional(readOnly = true)
public class NoticeService {

    private NoticeRepository noticeRepository;

    // 생성자 주입
    @Autowired
    public NoticeService(NoticeRepository noticeRepository) {
        this.noticeRepository = noticeRepository;
    }

    // ✅ Entity를 Response DTO로 변환하는 메서드 (누락되었던 부분)
    private NoticeResponse convertToResponse(Notice notice) {
        return new NoticeResponse(
                notice.getId(),
                notice.getTitle(),
                notice.getContent(),
                notice.getAuthorId(),
                notice.getAuthorName(),
                notice.getAuthorDepartment(),
                notice.getHasAttachments(),
                notice.getCreatedAt(),
                notice.getUpdatedAt()
        );
    }

    /**
     * 공지사항 생성
     */
    @Transactional
    public NoticeResponse createNotice(NoticeRequest requestDto) {
        System.out.println("공지사항 생성 요청: 제목=" + requestDto.getTitle() + ", 작성자=" + requestDto.getAuthorName());

        Notice notice = requestDto.toEntity();
        Notice savedNotice = noticeRepository.save(notice);

        System.out.println("공지사항 생성 완료: ID=" + savedNotice.getId());
        return convertToResponse(savedNotice); // ✅ 일관성을 위해 수정
    }

    /**
     * 공지사항 전체 조회 (페이징)
     */
    public Page<NoticeResponse> getAllNotices(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Notice> noticePage = noticeRepository.findAll(pageable);
        return noticePage.map(this::convertToResponse); // ✅ 일관성을 위해 수정
    }

    /**
     * 공지사항 단건 조회
     */
    public NoticeResponse getNoticeById(Long id) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 공지사항을 찾을 수 없습니다. ID: " + id));
        return convertToResponse(notice); // ✅ 일관성을 위해 수정
    }

    /**
     * 공지사항 수정
     */
    @Transactional
    public NoticeResponse updateNotice(Long id, NoticeRequest requestDto) {
        System.out.println("공지사항 수정 요청: ID=" + id + ", 제목=" + requestDto.getTitle());

        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 공지사항을 찾을 수 없습니다. ID: " + id));

        notice.updateNotice(requestDto.getTitle(), requestDto.getContent(), requestDto.getHasAttachments());

        System.out.println("공지사항 수정 완료: ID=" + id);
        return convertToResponse(notice); // ✅ 일관성을 위해 수정
    }

    /**
     * 공지사항 삭제
     */
    @Transactional
    public void deleteNotice(Long id) {
        System.out.println("공지사항 삭제 요청: ID=" + id);

        if (!noticeRepository.existsById(id)) {
            throw new IllegalArgumentException("해당 공지사항을 찾을 수 없습니다. ID: " + id);
        }

        noticeRepository.deleteById(id);
        System.out.println("공지사항 삭제 완료: ID=" + id);
    }

    /**
     * 제목으로 검색 (부분 일치로 수정)
     */
    public Page<NoticeResponse> searchByTitle(String title, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        // ✅ findByTitle -> findByTitleContaining으로 수정 (부분 일치)
        Page<Notice> noticePage = noticeRepository.findByTitleContaining(title, pageable);
        return noticePage.map(this::convertToResponse);
    }

    /**
     * 작성자로 검색
     */
    public Page<NoticeResponse> searchByAuthor(String authorName, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Notice> noticePage = noticeRepository.findByAuthorNameContaining(authorName, pageable);
        return noticePage.map(this::convertToResponse);
    }

    /**
     * 키워드로 검색 (제목 + 내용)
     */
    public Page<NoticeResponse> searchByKeyword(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        // ✅ Repository 메서드명과 일치하도록 수정
        Page<Notice> noticePage = noticeRepository.findByKeyword(keyword, pageable);
        return noticePage.map(this::convertToResponse);
    }

    /**
     * 최근 공지사항 5개
     */
    public List<NoticeResponse> getRecentNotices() {
        List<Notice> notices = noticeRepository.findTop5ByOrderByCreatedAtDesc();
        List<NoticeResponse> responseDtos = new ArrayList<>();

        for (Notice notice : notices) {
            responseDtos.add(convertToResponse(notice)); // ✅ 일관성을 위해 수정
        }

        return responseDtos;
    }

    /**
     * 작성자 ID로 공지사항 조회
     */
    public Page<NoticeResponse> getNoticesByAuthorId(Long authorId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Notice> noticePage = noticeRepository.findByAuthorId(authorId, pageable);
        return noticePage.map(this::convertToResponse);
    }

    /**
     * 부서별 공지사항 조회
     */
    public Page<NoticeResponse> getNoticesByDepartment(String department, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Notice> noticePage = noticeRepository.findByAuthorDepartment(department, pageable);
        return noticePage.map(this::convertToResponse);
    }

    /**
     * 첨부파일 있는 공지사항 조회
     */
    public Page<NoticeResponse> getNoticesWithAttachments(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Notice> notices = noticeRepository.findByHasAttachmentsTrue(pageable);
        return notices.map(this::convertToResponse);
    }


    /**
     * 날짜 범위로 공지사항 조회
     */
    public Page<NoticeResponse> getNoticesByDateRange(LocalDateTime startDate, LocalDateTime endDate, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Notice> noticePage = noticeRepository.findByCreatedAtBetween(startDate, endDate, pageable);
        return noticePage.map(this::convertToResponse);
    }

    // Getter & Setter (필요하다면 유지)
    public NoticeRepository getNoticeRepository() {
        return noticeRepository;
    }

    public void setNoticeRepository(NoticeRepository noticeRepository) {
        this.noticeRepository = noticeRepository;
    }
}
