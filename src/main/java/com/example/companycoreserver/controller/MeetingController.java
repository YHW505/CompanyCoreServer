package com.example.companycoreserver.controller;

import com.example.companycoreserver.dto.MeetingRequest;
import com.example.companycoreserver.entity.Meeting;
import com.example.companycoreserver.repository.MeetingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

@RestController
@RequestMapping("/api/meetings")
@CrossOrigin(origins = "*")
public class MeetingController {

    @Autowired
    private MeetingRepository meetingRepository;

    // 🔄 통합 회의 조회 (쿼리 파라미터로 다양한 조건 지원)
    @GetMapping
    public ResponseEntity<List<Meeting>> getMeetings(
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) String period, // "current", "upcoming", "past", "thisweek"
            @RequestParam(required = false, defaultValue = "false") boolean simple // 간단한 버전 여부
    ) {
        List<Meeting> meetings;
        LocalDateTime now = LocalDateTime.now();

        // 기간별 조회
        if ("current".equals(period)) {
            meetings = meetingRepository.findCurrentMeetings(now);
        } else if ("upcoming".equals(period)) {
            meetings = meetingRepository.findByStartTimeAfterOrderByStartTimeAsc(now);
        } else if ("past".equals(period)) {
            meetings = meetingRepository.findByEndTimeBeforeOrderByStartTimeDesc(now);
        } else if ("thisweek".equals(period)) {
            LocalDateTime weekStart = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).withHour(0).withMinute(0).withSecond(0);
            LocalDateTime weekEnd = weekStart.plusDays(7);
            meetings = meetingRepository.findMeetingsThisWeek(weekStart, weekEnd);
        }
        // 특정 날짜 조회
        else if (date != null) {
            meetings = meetingRepository.findMeetingsByDate(date.atStartOfDay());
        }
        // 날짜 범위 조회
        else if (startDate != null && endDate != null) {
            meetings = meetingRepository.findByStartTimeBetween(startDate, endDate);
        }
        // 회의실 검색
        else if (location != null && !location.trim().isEmpty()) {
            meetings = meetingRepository.findByLocationContaining(location);
        }
        // 제목 검색
        else if (title != null && !title.trim().isEmpty()) {
            meetings = meetingRepository.findByTitleContaining(title);
        }
        // 기본: 전체 조회 (최신순)
        else {
            meetings = meetingRepository.findAllByOrderByStartTimeDesc();
        }

        return ResponseEntity.ok(meetings);
    }

    // 특정 회의 조회
    @GetMapping("/{id}")
    public ResponseEntity<Meeting> getMeetingById(@PathVariable Long id) {
        Optional<Meeting> meeting = meetingRepository.findById(id);
        return meeting.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 🆕 간단한 회의 목록 조회 (첨부파일 제외)
    @GetMapping("/simple")
    public ResponseEntity<List<Map<String, Object>>> getMeetingsSimple() {
        List<Meeting> meetings = meetingRepository.findAllByOrderByStartTimeDesc();
        List<Map<String, Object>> simpleMeetings = new ArrayList<>();
        
        for (Meeting meeting : meetings) {
            Map<String, Object> simpleMeeting = new HashMap<>();
            simpleMeeting.put("meetingId", meeting.getMeetingId());
            simpleMeeting.put("title", meeting.getTitle());
            simpleMeeting.put("description", meeting.getDescription());
            simpleMeeting.put("startTime", meeting.getStartTime());
            simpleMeeting.put("endTime", meeting.getEndTime());
            simpleMeeting.put("location", meeting.getLocation());
            simpleMeeting.put("department", meeting.getDepartment());
            simpleMeeting.put("author", meeting.getAuthor());
            simpleMeeting.put("createdAt", meeting.getCreatedAt());
            simpleMeeting.put("updatedAt", meeting.getUpdatedAt());
            // 첨부파일 정보는 제외 (상세보기에서만 확인)
            
            simpleMeetings.add(simpleMeeting);
        }
        
        return ResponseEntity.ok(simpleMeetings);
    }

    // 회의 생성
    @PostMapping
    public ResponseEntity<Map<String, Object>> createMeeting(@RequestBody MeetingRequest request) {
        try {
            System.out.println("회의 생성 요청: " + request.getTitle());

            // === 🔍 첨부파일 디버깅 정보 ===
            System.out.println("=== 첨부파일 디버깅 정보 ===");
            System.out.println("첨부파일명: " + request.getAttachmentFilename());
            System.out.println("첨부파일 타입: " + request.getAttachmentContentType());
            System.out.println("첨부파일 크기: " + request.getAttachmentSize());
            System.out.println("hasAttachment(): " + request.hasAttachment());
            if (request.getAttachmentContent() != null) {
                System.out.println("첨부파일 내용 길이: " + request.getAttachmentContent().length());
            }
            System.out.println("================================");

            // Meeting 엔티티 생성
            Meeting meeting = new Meeting();
            meeting.setTitle(request.getTitle());
            meeting.setDescription(request.getDescription());
            meeting.setStartTime(request.getStartTime());
            meeting.setEndTime(request.getEndTime());
            meeting.setLocation(request.getLocation());
            meeting.setAuthor(request.getAuthor());
            meeting.setDepartment(request.getDepartment());

            // 생성 시간 자동 설정
            if (meeting.getCreatedAt() == null) {
                meeting.setCreatedAt(LocalDateTime.now());
            }

            // 🆕 첨부파일 처리
            if (request.hasAttachment()) {
                try {
                    // Base64 디코딩으로 실제 파일 크기 확인
                    byte[] fileData = java.util.Base64.getDecoder().decode(request.getAttachmentContent());

                    // 파일 크기 제한 체크 (예: 10MB)
                    long maxFileSize = 10 * 1024 * 1024; // 10MB
                    if (fileData.length > maxFileSize) {
                        Map<String, Object> errorResult = new HashMap<>();
                        errorResult.put("success", false);
                        errorResult.put("message", "파일 크기가 너무 큽니다. (최대 10MB)");
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResult);
                    }

                    // 첨부파일 정보 설정
                    meeting.setAttachmentFilename(request.getAttachmentFilename());
                    meeting.setAttachmentContentType(request.getAttachmentContentType());
                    meeting.setAttachmentContent(request.getAttachmentContent()); // Base64 문자열 저장
                    meeting.setAttachmentSize((long) fileData.length); // 실제 파일 크기로 설정

                    System.out.println("첨부파일 처리 완료: " + request.getAttachmentFilename() +
                            " (" + fileData.length + " bytes)");
                } catch (Exception e) {
                    System.err.println("첨부파일 처리 실패: " + e.getMessage());
                    Map<String, Object> errorResult = new HashMap<>();
                    errorResult.put("success", false);
                    errorResult.put("message", "첨부파일 처리에 실패했습니다: " + e.getMessage());
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResult);
                }
            }

            // 회의 저장
            Meeting savedMeeting = meetingRepository.save(meeting);

            System.out.println("회의 생성 완료: ID=" + savedMeeting.getMeetingId());

            // 성공 응답
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "회의가 성공적으로 생성되었습니다.");
            response.put("data", savedMeeting);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            System.err.println("회의 생성 실패: " + e.getMessage());
            e.printStackTrace();

            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "회의 생성에 실패했습니다: " + e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResult);
        }
    }
//    @PostMapping
//    public ResponseEntity<Meeting> createMeeting(@RequestBody Meeting meeting) {
//        // 생성 시간 자동 설정 (Entity 생성자에서 처리되지만 명시적으로)
//        if (meeting.getCreatedAt() == null) {
//            meeting.setCreatedAt(LocalDateTime.now());
//        }
//
//        Meeting savedMeeting = meetingRepository.save(meeting);
//        return ResponseEntity.status(201).body(savedMeeting); // 201 Created
//    }

    // 회의 수정
    @PutMapping("/{id}")
    public ResponseEntity<Meeting> updateMeeting(@PathVariable Long id, @RequestBody Meeting meetingDetails) {
        Optional<Meeting> optionalMeeting = meetingRepository.findById(id);

        if (optionalMeeting.isPresent()) {
            Meeting meeting = optionalMeeting.get();

            // null이 아닌 필드만 업데이트
            if (meetingDetails.getTitle() != null) {
                meeting.setTitle(meetingDetails.getTitle());
            }
            if (meetingDetails.getDescription() != null) {
                meeting.setDescription(meetingDetails.getDescription());
            }
            if (meetingDetails.getStartTime() != null) {
                meeting.setStartTime(meetingDetails.getStartTime());
            }
            if (meetingDetails.getEndTime() != null) {
                meeting.setEndTime(meetingDetails.getEndTime());
            }
            if (meetingDetails.getLocation() != null) {
                meeting.setLocation(meetingDetails.getLocation());
            }
            // 🆕 author와 department 업데이트 추가
            if (meetingDetails.getAuthor() != null) {
                meeting.setAuthor(meetingDetails.getAuthor());
            }
            if (meetingDetails.getDepartment() != null) {
                meeting.setDepartment(meetingDetails.getDepartment());
            }

            // 첨부파일 정보 업데이트 (null 값도 허용하여 삭제 처리)
            meeting.setAttachmentFilename(meetingDetails.getAttachmentFilename());
            meeting.setAttachmentContentType(meetingDetails.getAttachmentContentType());
            meeting.setAttachmentSize(meetingDetails.getAttachmentSize());
            meeting.setAttachmentContent(meetingDetails.getAttachmentContent());

            // 수정 시간은 @PreUpdate에서 자동 처리되지만 명시적으로 설정
            meeting.setUpdatedAt(LocalDateTime.now());

            Meeting updatedMeeting = meetingRepository.save(meeting);
            return ResponseEntity.ok(updatedMeeting);
        }

        return ResponseEntity.notFound().build();
    }

    // 회의 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMeeting(@PathVariable Long id) {
        if (meetingRepository.existsById(id)) {
            meetingRepository.deleteById(id);
            return ResponseEntity.noContent().build(); // 204 No Content
        }
        return ResponseEntity.notFound().build();
    }

    // 🆕 현재 진행중인 회의 조회
    @GetMapping("/current")
    public ResponseEntity<List<Meeting>> getCurrentMeetings() {
        List<Meeting> meetings = meetingRepository.findCurrentMeetings(LocalDateTime.now());
        return ResponseEntity.ok(meetings);
    }

    // 🆕 예정된 회의 조회
    @GetMapping("/upcoming")
    public ResponseEntity<List<Meeting>> getUpcomingMeetings() {
        List<Meeting> meetings = meetingRepository.findByStartTimeAfterOrderByStartTimeAsc(LocalDateTime.now());
        return ResponseEntity.ok(meetings);
    }

    // 🆕 완료된 회의 조회
    @GetMapping("/past")
    public ResponseEntity<List<Meeting>> getPastMeetings() {
        List<Meeting> meetings = meetingRepository.findByEndTimeBeforeOrderByStartTimeDesc(LocalDateTime.now());
        return ResponseEntity.ok(meetings);
    }

    // 🆕 이번 주 회의 조회
    @GetMapping("/thisweek")
    public ResponseEntity<List<Meeting>> getThisWeekMeetings() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime weekStart = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                .withHour(0).withMinute(0).withSecond(0);
        LocalDateTime weekEnd = weekStart.plusDays(7);

        List<Meeting> meetings = meetingRepository.findMeetingsThisWeek(weekStart, weekEnd);
        return ResponseEntity.ok(meetings);
    }

    // 🔄 개선된 날짜별 회의 조회 (기존 유지 + 새 메서드 활용)
    @GetMapping("/date")
    public ResponseEntity<List<Meeting>> getMeetingsByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        List<Meeting> meetings = meetingRepository.findMeetingsByDate(date.atStartOfDay());
        return ResponseEntity.ok(meetings);
    }

    // 회의실별 회의 조회 (기존 유지)
    @GetMapping("/location/{location}")
    public ResponseEntity<List<Meeting>> getMeetingsByLocation(@PathVariable String location) {
        List<Meeting> meetings = meetingRepository.findByLocationContaining(location);
        return ResponseEntity.ok(meetings);
    }

    // 🆕 제목으로 회의 검색
    @GetMapping("/search")
    public ResponseEntity<List<Meeting>> searchMeetingsByTitle(@RequestParam String title) {
        List<Meeting> meetings = meetingRepository.findByTitleContaining(title);
        return ResponseEntity.ok(meetings);
    }


// 🆕 회의록 첨부파일 다운로드
@GetMapping("/{id}/attachment")
public ResponseEntity<?> downloadMeetingAttachment(@PathVariable Long id) {
    try {
        System.out.println("첨부파일 다운로드 API 호출: 회의 ID=" + id);

        Optional<Meeting> meetingOpt = meetingRepository.findById(id);

        if (!meetingOpt.isPresent()) {
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "회의를 찾을 수 없습니다.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResult);
        }

        Meeting meeting = meetingOpt.get();

        if (meeting.getAttachmentContent() == null ||
                meeting.getAttachmentContent().trim().isEmpty()) {
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "첨부파일이 없습니다.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResult);
        }

        // Base64 디코딩
        byte[] fileData = Base64.getDecoder().decode(meeting.getAttachmentContent());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(meeting.getAttachmentContentType()));
        headers.setContentDispositionFormData("attachment", meeting.getAttachmentFilename());
        headers.setContentLength(fileData.length);

        return ResponseEntity.ok()
                .headers(headers)
                .body(fileData);

    } catch (Exception e) {
        System.err.println("첨부파일 다운로드 실패: " + e.getMessage());

        Map<String, Object> errorResult = new HashMap<>();
        errorResult.put("success", false);
        errorResult.put("message", "첨부파일 다운로드에 실패했습니다: " + e.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResult);
    }
}



//    @GetMapping("/{id}/attachment")
//    public ResponseEntity<Meeting> downloadMeetingAttachment(@PathVariable Long id) {
//        Optional<Meeting> meeting = meetingRepository.findById(id);
//        if (meeting.isPresent()) {
//            return ResponseEntity.ok(meeting.get());
//        }
//        return ResponseEntity.notFound().build();
//    }

    // 🆕 회의록 첨부파일 업로드
    @PutMapping("/{id}/attachment")
    public ResponseEntity<Meeting> uploadMeetingAttachment(@PathVariable Long id, @RequestBody Meeting attachmentData) {
        Optional<Meeting> optionalMeeting = meetingRepository.findById(id);

        if (optionalMeeting.isPresent()) {
            Meeting meeting = optionalMeeting.get();

            // 첨부파일 정보 업데이트
            if (attachmentData.getAttachmentFilename() != null) {
                meeting.setAttachmentFilename(attachmentData.getAttachmentFilename());
            }
            if (attachmentData.getAttachmentContentType() != null) {
                meeting.setAttachmentContentType(attachmentData.getAttachmentContentType());
            }
            if (attachmentData.getAttachmentSize() != null) {
                meeting.setAttachmentSize(attachmentData.getAttachmentSize());
            }
            if (attachmentData.getAttachmentContent() != null) {
                meeting.setAttachmentContent(attachmentData.getAttachmentContent());
            }

            // Base64 문자열을 바이트 배열로 변환하는 경우를 위한 처리
            // (클라이언트에서 Base64로 전송하는 경우)
            if (attachmentData.getAttachmentFilename() != null &&
                    attachmentData.getAttachmentContentType() != null) {
                meeting.setUpdatedAt(LocalDateTime.now());
            }

            Meeting updatedMeeting = meetingRepository.save(meeting);
            return ResponseEntity.ok(updatedMeeting);
        }

        return ResponseEntity.notFound().build();
    }

    // 🆕 회의록 첨부파일 삭제
    @DeleteMapping("/{id}/attachment")
    public ResponseEntity<Meeting> deleteMeetingAttachment(@PathVariable Long id) {
        Optional<Meeting> optionalMeeting = meetingRepository.findById(id);

        if (optionalMeeting.isPresent()) {
            Meeting meeting = optionalMeeting.get();
            meeting.removeAttachment();

            Meeting updatedMeeting = meetingRepository.save(meeting);
            return ResponseEntity.ok(updatedMeeting);
        }

        return ResponseEntity.notFound().build();
    }

    // 🆕 부서별 회의 목록 조회
    @GetMapping("/department/{department}")
    public ResponseEntity<List<Meeting>> getMeetingsByDepartment(@PathVariable String department) {
        try {
            List<Meeting> meetings = meetingRepository.findByDepartmentOrderByStartTimeDesc(department);
            return ResponseEntity.ok(meetings);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 🆕 부서별 현재 진행중인 회의 조회
    @GetMapping("/department/{department}/current")
    public ResponseEntity<List<Meeting>> getCurrentMeetingsByDepartment(@PathVariable String department) {
        try {
            LocalDateTime now = LocalDateTime.now();
            List<Meeting> meetings = meetingRepository.findCurrentMeetingsByDepartment(department, now);
            return ResponseEntity.ok(meetings);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 🆕 부서별 예정된 회의 조회
    @GetMapping("/department/{department}/upcoming")
    public ResponseEntity<List<Meeting>> getUpcomingMeetingsByDepartment(@PathVariable String department) {
        try {
            LocalDateTime now = LocalDateTime.now();
            List<Meeting> meetings = meetingRepository.findUpcomingMeetingsByDepartment(department, now);
            return ResponseEntity.ok(meetings);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 🆕 부서별 완료된 회의 조회
    @GetMapping("/department/{department}/past")
    public ResponseEntity<List<Meeting>> getPastMeetingsByDepartment(@PathVariable String department) {
        try {
            LocalDateTime now = LocalDateTime.now();
            List<Meeting> meetings = meetingRepository.findPastMeetingsByDepartment(department, now);
            return ResponseEntity.ok(meetings);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
