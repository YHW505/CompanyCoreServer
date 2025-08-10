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

    @Autowired
    public NoticeService(NoticeRepository noticeRepository) {
        this.noticeRepository = noticeRepository;
    }

    private NoticeResponse convertToResponse(Notice notice) {
        return new NoticeResponse(notice);
    }

    @Transactional
    public NoticeResponse createNotice(NoticeRequest requestDto) {
        System.out.println("공지사항 생성 요청: 제목=" + requestDto.getTitle() + ", 작성자=" + requestDto.getAuthorName());

        Notice notice = requestDto.toEntity();

        Boolean hasAttachmentFromRequest = false;
        if (requestDto.getAttachmentContent() != null && !requestDto.getAttachmentContent().trim().isEmpty()) {
            try {
                byte[] fileData = java.util.Base64.getDecoder().decode(requestDto.getAttachmentContent());

                notice.setAttachmentFilename(requestDto.getAttachmentFilename());
                notice.setAttachmentContentType(requestDto.getAttachmentContentType());
                notice.setAttachmentContent(requestDto.getAttachmentContent());
                notice.setAttachmentSize((long) fileData.length);
                hasAttachmentFromRequest = true;

                System.out.println("첨부파일 처리 완료: " + requestDto.getAttachmentFilename() + " (" + fileData.length + " bytes)");
            } catch (IllegalArgumentException e) {
                System.err.println("첨부파일 Base64 디코딩 실패: " + e.getMessage());
                hasAttachmentFromRequest = false;
            }
        }
        notice.setHasAttachment(hasAttachmentFromRequest);

        System.out.println("DEBUG: save 직전 notice.hasAttachment = " + notice.getHasAttachment());

        Notice savedNotice = noticeRepository.save(notice);

        System.out.println("공지사항 생성 완료: ID=" + savedNotice.getId());
        return convertToResponse(savedNotice);
    }

    public Page<NoticeResponse> getAllNotices(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Notice> noticePage = noticeRepository.findAll(pageable);
        
        List<NoticeResponse> filteredResponses = noticePage.stream()
                .map(notice -> {
                    try {
                        return convertToResponse(notice);
                    } catch (Exception e) {
                        System.err.println("❌ Notice 엔티티를 NoticeResponse DTO로 변환 중 오류 발생: " + e.getMessage());
                        e.printStackTrace();
                        return null;
                    }
                })
                .filter(response -> response != null) // null이 아닌 항목만 필터링
                .collect(java.util.stream.Collectors.toList());

        return new org.springframework.data.domain.PageImpl<>(filteredResponses, pageable, noticePage.getTotalElements());
    }

    public NoticeResponse getNoticeById(Long id) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 공지사항을 찾을 수 없습니다. ID: " + id));

        // LOB 데이터 강제 로딩 및 DTO 수동 생성
        if (notice.getAttachmentContent() != null) {
            notice.getAttachmentContent().length(); // Trigger lazy loading
        }

        NoticeResponse response = new NoticeResponse();
        response.setId(notice.getId());
        response.setTitle(notice.getTitle());
        response.setContent(notice.getContent());
        response.setAuthorId(notice.getAuthorId());
        response.setAuthorName(notice.getAuthorName());
        response.setAuthorDepartment(notice.getAuthorDepartment());
        response.setCreatedAt(notice.getCreatedAt());
        response.setUpdatedAt(notice.getUpdatedAt());

        boolean hasAttachment = notice.getAttachmentFilename() != null && !notice.getAttachmentFilename().isEmpty();
        response.setHasAttachment(hasAttachment);

        if (hasAttachment) {
            response.setAttachmentFilename(notice.getAttachmentFilename());
            response.setAttachmentContentType(notice.getAttachmentContentType());
            response.setAttachmentSize(notice.getAttachmentSize());
            response.setAttachmentContent(notice.getAttachmentContent());
        }

        return response;
    }

    @Transactional
    public NoticeResponse updateNotice(Long id, NoticeRequest requestDto) {
        System.out.println("공지사항 수정 요청: ID=" + id + ", 제목=" + requestDto.getTitle());

        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 공지사항을 찾을 수 없습니다. ID: " + id));

        notice.updateNotice(requestDto.getTitle(), requestDto.getContent());

        Boolean hasAttachmentInUpdate = notice.getHasAttachment();
        if (requestDto.getAttachmentContent() != null && !requestDto.getAttachmentContent().trim().isEmpty()) {
            try {
                byte[] fileData = java.util.Base64.getDecoder().decode(requestDto.getAttachmentContent());
                notice.updateAttachment(
                        requestDto.getAttachmentFilename(),
                        requestDto.getAttachmentContentType(),
                        requestDto.getAttachmentContent()
                );
                notice.setAttachmentSize((long) fileData.length);
                hasAttachmentInUpdate = true;
            } catch (IllegalArgumentException e) {
                System.err.println("첨부파일 Base64 디코딩 실패: " + e.getMessage());
            }
        } else if (requestDto.getAttachmentFilename() != null && requestDto.getAttachmentFilename().trim().isEmpty()) {
            notice.removeAttachment();
            hasAttachmentInUpdate = false;
        }
        notice.setHasAttachment(hasAttachmentInUpdate);

        System.out.println("공지사항 수정 완료: ID=" + id);
        return convertToResponse(notice);
    }

    @Transactional
    public void deleteNotice(Long id) {
        System.out.println("공지사항 삭제 요청: ID=" + id);

        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 공지사항을 찾을 수 없습니다. ID: " + id));

        noticeRepository.deleteById(id);
        System.out.println("공지사항 삭제 완료: ID=" + id);
    }

    @Transactional
    public NoticeResponse uploadAttachment(Long noticeId, String filename,
                                           String contentType, byte[] fileData) {
        System.out.println("첨부파일 업로드 요청: 공지사항 ID=" + noticeId + ", 파일명=" + filename);

        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new IllegalArgumentException("해당 공지사항을 찾을 수 없습니다. ID: " + noticeId));

        Long calculatedSize = (fileData != null) ? (long) fileData.length : 0L;

        String base64Content = java.util.Base64.getEncoder().encodeToString(fileData);
        notice.updateAttachment(filename, contentType, base64Content);

        System.out.println("첨부파일 업로드 완료: " + filename + " (크기: " + calculatedSize + " bytes)");
        return convertToResponse(notice);
    }

    public Page<NoticeResponse> searchByTitle(String title, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Notice> noticePage = noticeRepository.findByTitleContaining(title, pageable);
        return noticePage.map(this::convertToResponse);
    }

    public Page<NoticeResponse> searchByAuthor(String authorName, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Notice> noticePage = noticeRepository.findByAuthorNameContaining(authorName, pageable);
        return noticePage.map(this::convertToResponse);
    }

    public Page<NoticeResponse> searchByKeyword(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Notice> noticePage = noticeRepository.findByKeyword(keyword, pageable);
        return noticePage.map(this::convertToResponse);
    }

    public Page<NoticeResponse> searchByKeywordAndAttachment(String keyword, boolean hasAttachment, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Notice> noticePage = noticeRepository.findByKeywordAndAttachment(keyword, hasAttachment, pageable);
        return noticePage.map(this::convertToResponse);
    }

    public List<NoticeResponse> getRecentNotices() {
        List<Notice> notices = noticeRepository.findTop5ByOrderByCreatedAtDesc();
        List<NoticeResponse> responseDtos = new ArrayList<>();

        for (Notice notice : notices) {
            responseDtos.add(convertToResponse(notice));
        }

        return responseDtos;
    }

    public Page<NoticeResponse> getNoticesByAuthorId(Long authorId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Notice> noticePage = noticeRepository.findByAuthorId(authorId, pageable);
        return noticePage.map(this::convertToResponse);
    }

    public Page<NoticeResponse> getNoticesByDepartment(String department, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Notice> noticePage = noticeRepository.findByAuthorDepartment(department, pageable);
        return noticePage.map(this::convertToResponse);
    }

    public Page<NoticeResponse> getNoticesByDateRange(LocalDateTime startDate, LocalDateTime endDate, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Notice> noticePage = noticeRepository.findByCreatedAtBetween(startDate, endDate, pageable);
        return noticePage.map(this::convertToResponse);
    }

    public NoticeRepository getNoticeRepository() {
        return noticeRepository;
    }

    public void setNoticeRepository(NoticeRepository noticeRepository) {
        this.noticeRepository = noticeRepository;
    }
}