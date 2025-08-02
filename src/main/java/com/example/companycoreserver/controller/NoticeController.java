package com.example.companycoreserver.controller;


import com.example.companycoreserver.dto.NoticeRequest;
import com.example.companycoreserver.dto.NoticeResponse;
import com.example.companycoreserver.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/notices")
public class NoticeController {

    private NoticeService noticeService;

    // 생성자 주입
    @Autowired
    public NoticeController(NoticeService noticeService) {
        this.noticeService = noticeService;
    }

    // Getter
    public NoticeService getNoticeService() {
        return noticeService;
    }

    // Setter
    public void setNoticeService(NoticeService noticeService) {
        this.noticeService = noticeService;
    }

    /**
     * 공지사항 생성
     */
    @PostMapping
    public ResponseEntity<NoticeResponse> createNotice(@RequestBody NoticeRequest requestDto) {
        System.out.println("POST /api/notices - 공지사항 생성 요청");

        NoticeResponse responseDto = noticeService.createNotice(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    /**
     * 공지사항 전체 조회 (페이징)
     */
    @GetMapping
    public ResponseEntity<Page<NoticeResponse>> getAllNotices(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        System.out.println("GET /api/notices - 공지사항 전체 조회 (page=" + page + ", size=" + size + ")");

        Page<NoticeResponse> notices = noticeService.getAllNotices(page, size);
        return ResponseEntity.ok(notices);
    }

    /**
     * 공지사항 단건 조회
     */
    @GetMapping("/{id}")
    public ResponseEntity<NoticeResponse> getNoticeById(@PathVariable Long id) {
        System.out.println("GET /api/notices/" + id + " - 공지사항 단건 조회");

        NoticeResponse notice = noticeService.getNoticeById(id);
        return ResponseEntity.ok(notice);
    }

    /**
     * 공지사항 수정
     */
    @PutMapping("/{id}")
    public ResponseEntity<NoticeResponse> updateNotice(
            @PathVariable Long id,
            @RequestBody NoticeRequest requestDto) {

        System.out.println("PUT /api/notices/" + id + " - 공지사항 수정 요청");

        NoticeResponse updatedNotice = noticeService.updateNotice(id, requestDto);
        return ResponseEntity.ok(updatedNotice);
    }

    /**
     * 공지사항 삭제
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotice(@PathVariable Long id) {
        System.out.println("DELETE /api/notices/" + id + " - 공지사항 삭제 요청");

        noticeService.deleteNotice(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * 제목으로 검색
     */
    @GetMapping("/search/title")
    public ResponseEntity<Page<NoticeResponse>> searchByTitle(
            @RequestParam String title,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        System.out.println("GET /api/notices/search/title - 제목 검색: " + title);

        Page<NoticeResponse> notices = noticeService.searchByTitle(title, page, size);
        return ResponseEntity.ok(notices);
    }

    /**
     * 작성자로 검색
     */
    @GetMapping("/search/author")
    public ResponseEntity<Page<NoticeResponse>> searchByAuthor(
            @RequestParam String author,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        System.out.println("GET /api/notices/search/author - 작성자 검색: " + author);

        Page<NoticeResponse> notices = noticeService.searchByAuthor(author, page, size);
        return ResponseEntity.ok(notices);
    }

    /**
     * 키워드로 검색 (제목 + 내용)
     */
    @GetMapping("/search")
    public ResponseEntity<Page<NoticeResponse>> searchByKeyword(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        System.out.println("GET /api/notices/search - 키워드 검색: " + keyword);

        Page<NoticeResponse> notices = noticeService.searchByKeyword(keyword, page, size);
        return ResponseEntity.ok(notices);
    }

//    /**
//     * 최근 공지사항 5개
//     */
//    @GetMapping("/recent")
//    public ResponseEntity<List<NoticeResponse>> getRecentNotices() {
//        System.out.println("GET /api/notices/recent - 최근 공지사항 조회");
//
//        List<NoticeResponse> notices = noticeService.getRecentNotices();
//        return ResponseEntity.ok(notices);
//    }

    /**
     * 작성자 ID로 공지사항 조회
     */
    @GetMapping("/author/{authorId}")
    public ResponseEntity<Page<NoticeResponse>> getNoticesByAuthorId(
            @PathVariable Long authorId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        System.out.println("GET /api/notices/author/" + authorId + " - 작성자별 공지사항 조회");

        Page<NoticeResponse> notices = noticeService.getNoticesByAuthorId(authorId, page, size);
        return ResponseEntity.ok(notices);
    }

    /**
     * 부서별 공지사항 조회
     */
    @GetMapping("/department")
    public ResponseEntity<Page<NoticeResponse>> getNoticesByDepartment(
            @RequestParam String department,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        System.out.println("GET /api/notices/department - 부서별 공지사항 조회: " + department);

        Page<NoticeResponse> notices = noticeService.getNoticesByDepartment(department, page, size);
        return ResponseEntity.ok(notices);
    }

    /**
     * 첨부파일 있는 공지사항 조회
     */
    @GetMapping("/attachments")
    public ResponseEntity<Page<NoticeResponse>> getNoticesWithAttachments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        System.out.println("GET /api/notices/attachments - 첨부파일 있는 공지사항 조회");

        Page<NoticeResponse> notices = noticeService.getNoticesWithAttachments(page, size);
        return ResponseEntity.ok(notices);
    }

//    /**
//     * 날짜 범위로 공지사항 조회
//     */
//    @GetMapping("/date-range")
//    public ResponseEntity<Page<NoticeResponse>> getNoticesByDateRange(
//            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
//            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "10") int size) {
//
//        System.out.println("GET /api/notices/date-range - 날짜 범위 조회: " + startDate + " ~ " + endDate);
//
//        Page<NoticeResponse> notices = noticeService.getNoticesByDateRange(startDate, endDate, page, size);
//        return ResponseEntity.ok(notices);
//    }
}

