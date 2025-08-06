package com.example.companycoreserver.controller;

import com.example.companycoreserver.entity.Meeting;
import com.example.companycoreserver.repository.MeetingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Optional;

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
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) String period // "current", "upcoming", "past", "thisweek"
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

    // 회의 생성
    @PostMapping
    public ResponseEntity<Meeting> createMeeting(@RequestBody Meeting meeting) {
        // 생성 시간 자동 설정 (Entity 생성자에서 처리되지만 명시적으로)
        if (meeting.getCreatedAt() == null) {
            meeting.setCreatedAt(LocalDateTime.now());
        }

        Meeting savedMeeting = meetingRepository.save(meeting);
        return ResponseEntity.status(201).body(savedMeeting); // 201 Created
    }

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

            // 첨부파일 정보 업데이트 (Entity 필드에 맞게 수정)
            if (meetingDetails.getAttachmentFilename() != null) {
                meeting.setAttachmentFilename(meetingDetails.getAttachmentFilename());
            }
            if (meetingDetails.getAttachmentContentType() != null) {
                meeting.setAttachmentContentType(meetingDetails.getAttachmentContentType());
            }
            if (meetingDetails.getAttachmentSize() != null) {
                meeting.setAttachmentSize(meetingDetails.getAttachmentSize());
            }
            if (meetingDetails.getAttachmentContent() != null) {
                meeting.setAttachmentContent(meetingDetails.getAttachmentContent());
            }

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
    public ResponseEntity<Meeting> downloadMeetingAttachment(@PathVariable Long id) {
        Optional<Meeting> meeting = meetingRepository.findById(id);
        if (meeting.isPresent()) {
            return ResponseEntity.ok(meeting.get());
        }
        return ResponseEntity.notFound().build();
    }

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
}
