package com.example.companycoreserver.controller;

import com.example.companycoreserver.entity.Meeting;
import com.example.companycoreserver.repository.MeetingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/meetings")
@CrossOrigin(origins = "*")
public class MeetingController {

    @Autowired
    private MeetingRepository meetingRepository;

    // 모든 회의 조회
    @GetMapping
    public ResponseEntity<List<Meeting>> getAllMeetings() {
        List<Meeting> meetings = meetingRepository.findAllByOrderByStartTimeDesc();
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
        Meeting savedMeeting = meetingRepository.save(meeting);
        return ResponseEntity.ok(savedMeeting);
    }

    // 회의 수정
    @PutMapping("/{id}")
    public ResponseEntity<Meeting> updateMeeting(@PathVariable Long id, @RequestBody Meeting meetingDetails) {
        Optional<Meeting> optionalMeeting = meetingRepository.findById(id);

        if (optionalMeeting.isPresent()) {
            Meeting meeting = optionalMeeting.get();
            meeting.setTitle(meetingDetails.getTitle());
            meeting.setDescription(meetingDetails.getDescription());
            meeting.setStartTime(meetingDetails.getStartTime());
            meeting.setEndTime(meetingDetails.getEndTime());
            meeting.setLocation(meetingDetails.getLocation());
            meeting.setAttachmentPath(meetingDetails.getAttachmentPath());

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
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    // 날짜별 회의 조회
    @GetMapping("/date")
    public ResponseEntity<List<Meeting>> getMeetingsByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(23, 59, 59);

        List<Meeting> meetings = meetingRepository.findByStartTimeBetween(startOfDay, endOfDay);
        return ResponseEntity.ok(meetings);
    }

    // 회의실별 회의 조회
    @GetMapping("/location/{location}")
    public ResponseEntity<List<Meeting>> getMeetingsByLocation(@PathVariable String location) {
        List<Meeting> meetings = meetingRepository.findByLocationContaining(location);
        return ResponseEntity.ok(meetings);
    }
}
