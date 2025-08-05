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

    // ✅ Entity를 Response DTO로 변환하는 메서드 (size 제외)
    private NoticeResponse convertToResponse(Notice notice) {
        // 🆕 첨부파일 내용을 Base64로 인코딩
        String attachmentContent = null;
        if (notice.hasAttachment() && notice.getAttachmentFile() != null && notice.getAttachmentFile().length > 0) {
            attachmentContent = java.util.Base64.getEncoder().encodeToString(notice.getAttachmentFile());
        }
        
        return new NoticeResponse(
                notice.getId(),
                notice.getTitle(),
                notice.getContent(),
                notice.getAuthorId(),
                notice.getAuthorName(),
                notice.getAuthorDepartment(),
                notice.hasAttachment(),                    // 첨부파일 여부
                notice.getAttachmentFilename(),            // 파일명
                notice.getAttachmentContentType(),         // MIME 타입
                notice.getAttachmentSize(),                // 🆕 파일 크기
                attachmentContent,                         // 🆕 첨부파일 내용 (Base64)
                notice.getCreatedAt(),
                notice.getUpdatedAt()
        );
    }

    /**
     * ✅ 공지사항 생성
     */
    @Transactional
    public NoticeResponse createNotice(NoticeRequest requestDto) {
        System.out.println("공지사항 생성 요청: 제목=" + requestDto.getTitle() + ", 작성자=" + requestDto.getAuthorName());

        Notice notice = requestDto.toEntity();
        
        // 🆕 첨부파일 내용 처리 (Base64 디코딩)
        if (requestDto.getAttachmentContent() != null && !requestDto.getAttachmentContent().trim().isEmpty()) {
            try {
                // Base64 디코딩
                byte[] fileData = java.util.Base64.getDecoder().decode(requestDto.getAttachmentContent());
                
                // 첨부파일 정보 설정
                notice.setAttachmentFilename(requestDto.getAttachmentFilename());
                notice.setAttachmentContentType(requestDto.getAttachmentContentType());
                notice.setAttachmentFile(fileData);
                notice.setAttachmentSize((long) fileData.length);
                
                System.out.println("첨부파일 처리 완료: " + requestDto.getAttachmentFilename() + " (" + fileData.length + " bytes) - Base64 내용 생략");
            } catch (Exception e) {
                System.err.println("첨부파일 Base64 디코딩 실패: " + e.getMessage());
                // 첨부파일 처리 실패 시 기본 정보만 저장
            }
        }
        
        Notice savedNotice = noticeRepository.save(notice);

        System.out.println("공지사항 생성 완료: ID=" + savedNotice.getId());
        return convertToResponse(savedNotice);
    }

    /**
     * ✅ 공지사항 전체 조회 (페이징)
     */
    public Page<NoticeResponse> getAllNotices(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Notice> noticePage = noticeRepository.findAll(pageable);
        return noticePage.map(this::convertToResponse);
    }

    /**
     * ✅ 공지사항 단건 조회
     */
    public NoticeResponse getNoticeById(Long id) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 공지사항을 찾을 수 없습니다. ID: " + id));
        return convertToResponse(notice);
    }

    /**
     * ✅ 공지사항 수정 - 4개 파라미터 메서드 사용
     */
    @Transactional
    public NoticeResponse updateNotice(Long id, NoticeRequest requestDto) {
        System.out.println("공지사항 수정 요청: ID=" + id + ", 제목=" + requestDto.getTitle());

        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 공지사항을 찾을 수 없습니다. ID: " + id));

        // ✅ 기본 정보 업데이트
        notice.updateNotice(requestDto.getTitle(), requestDto.getContent());

        // ✅ 첨부파일 메타데이터 업데이트 (4개 파라미터 메서드 사용)
        if (requestDto.getAttachmentFilename() != null &&
                !requestDto.getAttachmentFilename().trim().isEmpty()) {

            System.out.println("첨부파일 메타데이터 업데이트: " + requestDto.getAttachmentFilename());

            // 🔧 4개 파라미터 메서드 호출 (기존 파일 데이터와 size 유지)
            notice.updateAttachment(
                    requestDto.getAttachmentFilename(),
                    requestDto.getAttachmentContentType(),
                    notice.getAttachmentFile()         // 기존 파일 데이터 유지
            );
        } else {
            System.out.println("첨부파일 정보 없음 - 기본 정보만 업데이트");
        }

        System.out.println("공지사항 수정 완료: ID=" + id);
        return convertToResponse(notice);
    }

    /**
     * ✅ 공지사항 삭제
     */
    @Transactional
    public void deleteNotice(Long id) {
        System.out.println("공지사항 삭제 요청: ID=" + id);

        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 공지사항을 찾을 수 없습니다. ID: " + id));

        noticeRepository.deleteById(id);
        System.out.println("공지사항 삭제 완료: ID=" + id);
    }

    /**
     * 🔧 첨부파일 업로드 전용 메서드 (4개 파라미터 사용)
     */
    @Transactional
    public NoticeResponse uploadAttachment(Long noticeId, String filename,
                                           String contentType, byte[] fileData) {
        System.out.println("첨부파일 업로드 요청: 공지사항 ID=" + noticeId + ", 파일명=" + filename);

        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new IllegalArgumentException("해당 공지사항을 찾을 수 없습니다. ID: " + noticeId));

        // ✅ 파일 크기는 바이너리 데이터에서 자동 계산
        Long calculatedSize = (fileData != null) ? (long) fileData.length : 0L;

        // 🔧 4개 파라미터 메서드 호출
        notice.updateAttachment(filename, contentType, fileData);

        System.out.println("첨부파일 업로드 완료: " + filename + " (크기: " + calculatedSize + " bytes)");
        return convertToResponse(notice);
    }


    /**
     * ✅ 제목으로 검색 (부분 일치)
     */
    public Page<NoticeResponse> searchByTitle(String title, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Notice> noticePage = noticeRepository.findByTitleContaining(title, pageable);
        return noticePage.map(this::convertToResponse);
    }

    /**
     * ✅ 작성자로 검색
     */
    public Page<NoticeResponse> searchByAuthor(String authorName, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Notice> noticePage = noticeRepository.findByAuthorNameContaining(authorName, pageable);
        return noticePage.map(this::convertToResponse);
    }

    /**
     * ✅ 키워드로 검색 (제목 + 내용)
     */
    public Page<NoticeResponse> searchByKeyword(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Notice> noticePage = noticeRepository.findByKeyword(keyword, pageable);
        return noticePage.map(this::convertToResponse);
    }

    /**
     * ✅ 키워드 + 첨부파일 여부로 복합 검색
     */
    public Page<NoticeResponse> searchByKeywordAndAttachment(String keyword, boolean hasAttachment, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Notice> noticePage = noticeRepository.findByKeywordAndAttachment(keyword, hasAttachment, pageable);
        return noticePage.map(this::convertToResponse);
    }

    /**
     * ✅ 최근 공지사항 5개
     */
    public List<NoticeResponse> getRecentNotices() {
        List<Notice> notices = noticeRepository.findTop5ByOrderByCreatedAtDesc();
        List<NoticeResponse> responseDtos = new ArrayList<>();

        for (Notice notice : notices) {
            responseDtos.add(convertToResponse(notice));
        }

        return responseDtos;
    }

    /**
     * ✅ 작성자 ID로 공지사항 조회
     */
    public Page<NoticeResponse> getNoticesByAuthorId(Long authorId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Notice> noticePage = noticeRepository.findByAuthorId(authorId, pageable);
        return noticePage.map(this::convertToResponse);
    }

    /**
     * ✅ 부서별 공지사항 조회
     */
    public Page<NoticeResponse> getNoticesByDepartment(String department, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Notice> noticePage = noticeRepository.findByAuthorDepartment(department, pageable);
        return noticePage.map(this::convertToResponse);
    }

    /**
     * ✅ 날짜 범위로 공지사항 조회
     */
    public Page<NoticeResponse> getNoticesByDateRange(LocalDateTime startDate, LocalDateTime endDate, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Notice> noticePage = noticeRepository.findByCreatedAtBetween(startDate, endDate, pageable);
        return noticePage.map(this::convertToResponse);
    }




    // Getter & Setter
    public NoticeRepository getNoticeRepository() {
        return noticeRepository;
    }

    public void setNoticeRepository(NoticeRepository noticeRepository) {
        this.noticeRepository = noticeRepository;
    }
}
