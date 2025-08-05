package com.example.companycoreserver.controller;

import com.example.companycoreserver.dto.NoticeRequest;
import com.example.companycoreserver.dto.NoticeResponse;
import com.example.companycoreserver.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notices")
@CrossOrigin(origins = "*")
public class NoticeController {

    private final NoticeService noticeService;

    @Autowired
    public NoticeController(NoticeService noticeService) {
        this.noticeService = noticeService;
    }

    /**
     * ✅ 공지사항 생성
     * POST /api/notices
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createNotice(@RequestBody NoticeRequest requestDto) {
        try {
            System.out.println("공지사항 생성 API 호출: " + requestDto.getTitle());
            // 첨부파일 정보 로그 (내용은 생략)
            if (requestDto.getAttachmentFilename() != null) {
                System.out.println("첨부파일 정보: " + requestDto.getAttachmentFilename() + 
                    " (크기: " + requestDto.getAttachmentSize() + " bytes) - Base64 내용 생략");
            }

            NoticeResponse response = noticeService.createNotice(requestDto);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "공지사항이 성공적으로 생성되었습니다.");
            result.put("data", response);

            return ResponseEntity.status(HttpStatus.CREATED).body(result);

        } catch (Exception e) {
            System.err.println("공지사항 생성 실패: " + e.getMessage());

            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "공지사항 생성에 실패했습니다: " + e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResult);
        }
    }

    /**
     * ✅ 공지사항 전체 조회 (페이징)
     * GET /api/notices?page=0&size=10
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllNotices(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            System.out.println("공지사항 전체 조회 API 호출: page=" + page + ", size=" + size);

            Page<NoticeResponse> noticePage = noticeService.getAllNotices(page, size);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "공지사항 목록을 성공적으로 조회했습니다.");
            result.put("data", noticePage.getContent());
            result.put("totalElements", noticePage.getTotalElements());
            result.put("totalPages", noticePage.getTotalPages());
            result.put("currentPage", page);
            result.put("size", size);

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            System.err.println("공지사항 조회 실패: " + e.getMessage());

            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "공지사항 조회에 실패했습니다: " + e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResult);
        }
    }

    /**
     * ✅ 공지사항 단건 조회
     * GET /api/notices/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getNoticeById(@PathVariable Long id) {
        try {
            System.out.println("공지사항 단건 조회 API 호출: ID=" + id);

            NoticeResponse response = noticeService.getNoticeById(id);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "공지사항을 성공적으로 조회했습니다.");
            result.put("data", response);

            return ResponseEntity.ok(result);

        } catch (IllegalArgumentException e) {
            System.err.println("공지사항 조회 실패 - 존재하지 않음: " + e.getMessage());

            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResult);

        } catch (Exception e) {
            System.err.println("공지사항 조회 실패: " + e.getMessage());

            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "공지사항 조회에 실패했습니다: " + e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResult);
        }
    }

    /**
     * ✅ 공지사항 수정
     * PUT /api/notices/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateNotice(
            @PathVariable Long id,
            @RequestBody NoticeRequest requestDto) {
        try {
            System.out.println("공지사항 수정 API 호출: ID=" + id + ", 제목=" + requestDto.getTitle());

            NoticeResponse response = noticeService.updateNotice(id, requestDto);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "공지사항이 성공적으로 수정되었습니다.");
            result.put("data", response);

            return ResponseEntity.ok(result);

        } catch (IllegalArgumentException e) {
            System.err.println("공지사항 수정 실패 - 존재하지 않음: " + e.getMessage());

            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResult);

        } catch (Exception e) {
            System.err.println("공지사항 수정 실패: " + e.getMessage());

            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "공지사항 수정에 실패했습니다: " + e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResult);
        }
    }

    /**
     * ✅ 공지사항 삭제
     * DELETE /api/notices/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteNotice(@PathVariable Long id) {
        try {
            System.out.println("공지사항 삭제 API 호출: ID=" + id);

            noticeService.deleteNotice(id);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "공지사항이 성공적으로 삭제되었습니다.");

            return ResponseEntity.ok(result);

        } catch (IllegalArgumentException e) {
            System.err.println("공지사항 삭제 실패 - 존재하지 않음: " + e.getMessage());

            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResult);

        } catch (Exception e) {
            System.err.println("공지사항 삭제 실패: " + e.getMessage());

            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "공지사항 삭제에 실패했습니다: " + e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResult);
        }
    }

    /**
     * 🔧 첨부파일 업로드
     * POST /api/notices/{id}/attachment
     */
    @PostMapping(value = "/{id}/attachment", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> uploadAttachment(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        try {
            System.out.println("첨부파일 업로드 API 호출: 공지사항 ID=" + id + ", 파일명=" + file.getOriginalFilename());

            if (file.isEmpty()) {
                Map<String, Object> errorResult = new HashMap<>();
                errorResult.put("success", false);
                errorResult.put("message", "업로드할 파일이 없습니다.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResult);
            }

            String filename = file.getOriginalFilename();
            String contentType = file.getContentType();
            byte[] fileData = file.getBytes();

            NoticeResponse response = noticeService.uploadAttachment(id, filename, contentType, fileData);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "첨부파일이 성공적으로 업로드되었습니다.");
            result.put("data", response);

            return ResponseEntity.ok(result);

        } catch (IllegalArgumentException e) {
            System.err.println("첨부파일 업로드 실패 - 공지사항 없음: " + e.getMessage());

            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResult);

        } catch (Exception e) {
            System.err.println("첨부파일 업로드 실패: " + e.getMessage());

            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "첨부파일 업로드에 실패했습니다: " + e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResult);
        }
    }

    /**
     * ✅ 제목으로 검색
     * GET /api/notices/search/title?title=검색어&page=0&size=10
     */
    @GetMapping("/search/title")
    public ResponseEntity<Map<String, Object>> searchByTitle(
            @RequestParam String title,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            System.out.println("제목 검색 API 호출: title=" + title + ", page=" + page + ", size=" + size);

            Page<NoticeResponse> noticePage = noticeService.searchByTitle(title, page, size);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "제목 검색이 완료되었습니다.");
            result.put("data", noticePage.getContent());
            result.put("totalElements", noticePage.getTotalElements());
            result.put("totalPages", noticePage.getTotalPages());
            result.put("currentPage", page);
            result.put("size", size);
            result.put("searchKeyword", title);

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            System.err.println("제목 검색 실패: " + e.getMessage());

            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "제목 검색에 실패했습니다: " + e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResult);
        }
    }

    /**
     * ✅ 작성자로 검색
     * GET /api/notices/search/author?authorName=작성자&page=0&size=10
     */
    @GetMapping("/search/author")
    public ResponseEntity<Map<String, Object>> searchByAuthor(
            @RequestParam String authorName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            System.out.println("작성자 검색 API 호출: authorName=" + authorName + ", page=" + page + ", size=" + size);

            Page<NoticeResponse> noticePage = noticeService.searchByAuthor(authorName, page, size);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "작성자 검색이 완료되었습니다.");
            result.put("data", noticePage.getContent());
            result.put("totalElements", noticePage.getTotalElements());
            result.put("totalPages", noticePage.getTotalPages());
            result.put("currentPage", page);
            result.put("size", size);
            result.put("searchAuthor", authorName);

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            System.err.println("작성자 검색 실패: " + e.getMessage());

            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "작성자 검색에 실패했습니다: " + e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResult);
        }
    }

    /**
     * ✅ 키워드로 검색 (제목 + 내용)
     * GET /api/notices/search/keyword?keyword=검색어&page=0&size=10
     */
    @GetMapping("/search/keyword")
    public ResponseEntity<Map<String, Object>> searchByKeyword(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            System.out.println("키워드 검색 API 호출: keyword=" + keyword + ", page=" + page + ", size=" + size);

            Page<NoticeResponse> noticePage = noticeService.searchByKeyword(keyword, page, size);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "키워드 검색이 완료되었습니다.");
            result.put("data", noticePage.getContent());
            result.put("totalElements", noticePage.getTotalElements());
            result.put("totalPages", noticePage.getTotalPages());
            result.put("currentPage", page);
            result.put("size", size);
            result.put("searchKeyword", keyword);

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            System.err.println("키워드 검색 실패: " + e.getMessage());

            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "키워드 검색에 실패했습니다: " + e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResult);
        }
    }

    /**
     * ✅ 키워드 + 첨부파일 여부로 복합 검색
     * GET /api/notices/search/complex?keyword=검색어&hasAttachment=true&page=0&size=10
     */
    @GetMapping("/search/complex")
    public ResponseEntity<Map<String, Object>> searchByKeywordAndAttachment(
            @RequestParam String keyword,
            @RequestParam boolean hasAttachment,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            System.out.println("복합 검색 API 호출: keyword=" + keyword + ", hasAttachment=" + hasAttachment + ", page=" + page + ", size=" + size);

            Page<NoticeResponse> noticePage = noticeService.searchByKeywordAndAttachment(keyword, hasAttachment, page, size);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "복합 검색이 완료되었습니다.");
            result.put("data", noticePage.getContent());
            result.put("totalElements", noticePage.getTotalElements());
            result.put("totalPages", noticePage.getTotalPages());
            result.put("currentPage", page);
            result.put("size", size);
            result.put("searchKeyword", keyword);
            result.put("hasAttachment", hasAttachment);

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            System.err.println("복합 검색 실패: " + e.getMessage());

            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "복합 검색에 실패했습니다: " + e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResult);
        }
    }

    /**
     * ✅ 최근 공지사항 5개
     * GET /api/notices/recent
     */
    @GetMapping("/recent")
    public ResponseEntity<Map<String, Object>> getRecentNotices() {
        try {
            System.out.println("최근 공지사항 조회 API 호출");

            List<NoticeResponse> notices = noticeService.getRecentNotices();

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "최근 공지사항을 성공적으로 조회했습니다.");
            result.put("data", notices);
            result.put("count", notices.size());

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            System.err.println("최근 공지사항 조회 실패: " + e.getMessage());

            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "최근 공지사항 조회에 실패했습니다: " + e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResult);
        }
    }

    /**
     * ✅ 작성자 ID로 공지사항 조회
     * GET /api/notices/author/{authorId}?page=0&size=10
     */
    @GetMapping("/author/{authorId}")
    public ResponseEntity<Map<String, Object>> getNoticesByAuthorId(
            @PathVariable Long authorId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            System.out.println("작성자 ID별 공지사항 조회 API 호출: authorId=" + authorId + ", page=" + page + ", size=" + size);

            Page<NoticeResponse> noticePage = noticeService.getNoticesByAuthorId(authorId, page, size);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "작성자별 공지사항을 성공적으로 조회했습니다.");
            result.put("data", noticePage.getContent());
            result.put("totalElements", noticePage.getTotalElements());
            result.put("totalPages", noticePage.getTotalPages());
            result.put("currentPage", page);
            result.put("size", size);
            result.put("authorId", authorId);

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            System.err.println("작성자 ID별 공지사항 조회 실패: " + e.getMessage());

            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "작성자별 공지사항 조회에 실패했습니다: " + e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResult);
        }
    }

    /**
     * ✅ 부서별 공지사항 조회
     * GET /api/notices/department/{department}?page=0&size=10
     */
    @GetMapping("/department/{department}")
    public ResponseEntity<Map<String, Object>> getNoticesByDepartment(
            @PathVariable String department,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            System.out.println("부서별 공지사항 조회 API 호출: department=" + department + ", page=" + page + ", size=" + size);

            Page<NoticeResponse> noticePage = noticeService.getNoticesByDepartment(department, page, size);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "부서별 공지사항을 성공적으로 조회했습니다.");
            result.put("data", noticePage.getContent());
            result.put("totalElements", noticePage.getTotalElements());
            result.put("totalPages", noticePage.getTotalPages());
            result.put("currentPage", page);
            result.put("size", size);
            result.put("department", department);

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            System.err.println("부서별 공지사항 조회 실패: " + e.getMessage());

            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "부서별 공지사항 조회에 실패했습니다: " + e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResult);
        }
    }

    /**
     * ✅ 날짜 범위로 공지사항 조회
     * GET /api/notices/date-range?startDate=2024-01-01T00:00:00&endDate=2024-12-31T23:59:59&page=0&size=10
     */
    @GetMapping("/date-range")
    public ResponseEntity<Map<String, Object>> getNoticesByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            System.out.println("날짜 범위별 공지사항 조회 API 호출: startDate=" + startDate + ", endDate=" + endDate + ", page=" + page + ", size=" + size);

            Page<NoticeResponse> noticePage = noticeService.getNoticesByDateRange(startDate, endDate, page, size);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "날짜 범위별 공지사항을 성공적으로 조회했습니다.");
            result.put("data", noticePage.getContent());
            result.put("totalElements", noticePage.getTotalElements());
            result.put("totalPages", noticePage.getTotalPages());
            result.put("currentPage", page);
            result.put("size", size);
            result.put("startDate", startDate);
            result.put("endDate", endDate);

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            System.err.println("날짜 범위별 공지사항 조회 실패: " + e.getMessage());

            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "날짜 범위별 공지사항 조회에 실패했습니다: " + e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResult);
        }
    }
}
