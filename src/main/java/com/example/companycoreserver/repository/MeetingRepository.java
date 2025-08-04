package com.example.companycoreserver.repository;

import com.example.companycoreserver.entity.Meeting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MeetingRepository extends JpaRepository<Meeting, Long> {

    // 특정 날짜의 회의 조회
    List<Meeting> findByStartTimeBetween(LocalDateTime startDate, LocalDateTime endDate);

    // 회의실별 회의 조회
    List<Meeting> findByLocationContaining(String location);

    // 제목으로 검색
    List<Meeting> findByTitleContaining(String title);

    // 최신순 정렬
    List<Meeting> findAllByOrderByStartTimeDesc();
}
