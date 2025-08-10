package com.example.companycoreserver.service;

import com.example.companycoreserver.entity.Attendance;
import com.example.companycoreserver.entity.Enum.AttendanceStatus;
import com.example.companycoreserver.entity.User;
import com.example.companycoreserver.repository.AttendanceRepository;
import com.example.companycoreserver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class AttendanceService {

    @Autowired
    private AttendanceRepository attendanceRepository;
    @Autowired
    private UserRepository userRepository;

    // 🕘 출근시간 기준 (9시)
    private static final LocalTime WORK_START_TIME = LocalTime.of(9, 0);

    // ===== 📝 출근/퇴근 처리 메서드 =====

    // 1. 출근 처리 - Status 자동 설정
    public Attendance checkIn(Long userId) {
        try {
            System.out.println("=== AttendanceService.checkIn 시작 ===");
            System.out.println("userId: " + userId);

            // 사용자 조회 (Department, Position 함께 로드)
            User user = userRepository.findByIdWithDetails(userId)
                    .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다. userId: " + userId));

            LocalDate today = LocalDate.now();
            LocalDateTime checkInTime = LocalDateTime.now();

            // 오늘 이미 출근했는지 확인
            Optional<Attendance> existingAttendance = attendanceRepository
                    .findByUserIdAndWorkDate(userId, today);

            if (existingAttendance.isPresent()) {
                throw new RuntimeException("오늘 이미 출근 처리되었습니다.");
            }

            // 새로운 출근 기록 생성
            Attendance attendance = new Attendance();
            attendance.setUser(user);
            attendance.setUserId(userId);
            attendance.setCheckIn(checkInTime);
            attendance.setWorkDate(today);

            // 🕘 Status 자동 설정 (9시 기준)
            AttendanceStatus calculatedStatus = calculateAttendanceStatus(checkInTime);
            System.out.println("🔍 계산된 Status: " + calculatedStatus);

            System.out.println("🔍 setStatus 호출 전 - attendance.getStatus(): " + attendance.getStatus());
            attendance.setStatus(calculatedStatus);
            System.out.println("🔍 setStatus 호출 후 - attendance.getStatus(): " + attendance.getStatus());

            // 저장 직전 상태 확인
            System.out.println("🔍 저장 직전 - attendance 전체: " + attendance);
            System.out.println("🔍 저장 직전 - attendance.getStatus(): " + attendance.getStatus());

            Attendance savedAttendance = attendanceRepository.save(attendance);

            System.out.println("🔍 저장 직후 - savedAttendance.getStatus(): " + savedAttendance.getStatus());

            // User 정보 강제 로드 (Lazy Loading 해결)
            savedAttendance.getUser().getUsername();
            if (savedAttendance.getUser().getDepartment() != null) {
                savedAttendance.getUser().getDepartment().getDepartmentName();
            }
            if (savedAttendance.getUser().getPosition() != null) {
                savedAttendance.getUser().getPosition().getPositionName();
            }

            System.out.println("출근 처리 완료: " + savedAttendance.getAttendanceId());
            System.out.println("User 정보: " + savedAttendance.getUser().getUsername());
            System.out.println("출근 시간: " + checkInTime.toLocalTime());
            System.out.println("Status: " + savedAttendance.getStatus());

            return savedAttendance;

        } catch (Exception e) {
            System.err.println("출근 처리 중 오류: " + e.getMessage());
            throw e;
        }
    }

    // 2. 퇴근 처리
    public Attendance checkOut(Long userId) {
        try {
            System.out.println("=== AttendanceService.checkOut 시작 ===");
            System.out.println("userId: " + userId);

            // 사용자 조회 (Department, Position 함께 로드)
            User user = userRepository.findByIdWithDetails(userId)
                    .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다. userId: " + userId));

            LocalDate today = LocalDate.now();

            // 오늘 날짜의 미퇴근 기록 찾기
            Optional<Attendance> attendanceOpt = attendanceRepository
                    .findByUserIdAndWorkDateAndCheckOutIsNull(userId, today);

            if (!attendanceOpt.isPresent()) {
                throw new RuntimeException("오늘 퇴근 처리할 출근 기록이 없습니다.");
            }

            Attendance attendance = attendanceOpt.get();
            LocalDateTime checkOutTime = LocalDateTime.now();

            // 퇴근 시간 설정
            attendance.setCheckOut(checkOutTime);

            // 근무 시간 계산 (0분일 때 생략)
            if (attendance.getCheckIn() != null) {
                Duration duration = Duration.between(attendance.getCheckIn(), checkOutTime);
                long totalMinutes = duration.toMinutes();

                long hours = totalMinutes / 60;
                long minutes = totalMinutes % 60;

                String workHoursText;
                if (minutes == 0) {
                    workHoursText = hours + "시간";
                } else {
                    workHoursText = hours + "시간 " + minutes + "분";
                }

                attendance.setWorkHours(workHoursText);
            }

            Attendance savedAttendance = attendanceRepository.save(attendance);

            // User 정보 강제 로드 (Lazy Loading 해결)
            savedAttendance.getUser().getUsername();
            if (savedAttendance.getUser().getDepartment() != null) {
                savedAttendance.getUser().getDepartment().getDepartmentName();
            }
            if (savedAttendance.getUser().getPosition() != null) {
                savedAttendance.getUser().getPosition().getPositionName();
            }

            System.out.println("퇴근 처리 완료: " + savedAttendance.getAttendanceId());
            System.out.println("User 정보: " + savedAttendance.getUser().getUsername());
            System.out.println("근무 시간: " + savedAttendance.getWorkHours());
            System.out.println("Status: " + savedAttendance.getStatus());

            return savedAttendance;

        } catch (Exception e) {
            System.err.println("퇴근 처리 중 오류: " + e.getMessage());
            throw e;
        }
    }

    // ===== 📊 조회 메서드 =====

    // 3. 사용자별 출근 기록 조회
    public List<Attendance> getAttendanceByUserId(Long userId) {
        try {
            System.out.println("=== AttendanceService.getAttendanceByUserId 시작 ===");
            System.out.println("사용자 ID: " + userId);

            List<Attendance> attendanceList = attendanceRepository.findByUserIdOrderByWorkDateDesc(userId);
            System.out.println("사용자별 출근 기록 조회 완료: " + attendanceList.size() + "개");

            return attendanceList;
        } catch (Exception e) {
            System.err.println("AttendanceService.getAttendanceByUserId 오류: " + e.getMessage());
            throw new RuntimeException("사용자별 출근 기록 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // 4. 날짜 범위별 출근 기록 조회
    public List<Attendance> getAttendanceByUserAndDateRange(Long userId, LocalDate startDate, LocalDate endDate) {
        try {
            System.out.println("=== AttendanceService.getAttendanceByUserAndDateRange 시작 ===");
            System.out.println("사용자 ID: " + userId + ", 시작일: " + startDate + ", 종료일: " + endDate);

            List<Attendance> attendanceList = attendanceRepository.findByUserIdAndWorkDateBetween(userId, startDate, endDate);
            System.out.println("날짜 범위별 출근 기록 조회 완료: " + attendanceList.size() + "개");

            return attendanceList;
        } catch (Exception e) {
            System.err.println("AttendanceService.getAttendanceByUserAndDateRange 오류: " + e.getMessage());
            throw new RuntimeException("날짜 범위별 출근 기록 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // 5. 특정 날짜의 모든 출근 기록 조회
    public List<Attendance> getAttendanceByWorkDate(LocalDate workDate) {
        try {
            System.out.println("=== AttendanceService.getAttendanceByWorkDate 시작 ===");
            System.out.println("근무 날짜: " + workDate);

            List<Attendance> attendanceList = attendanceRepository.findByWorkDate(workDate);
            System.out.println("날짜별 출근 기록 조회 완료: " + attendanceList.size() + "개");

            return attendanceList;
        } catch (Exception e) {
            System.err.println("AttendanceService.getAttendanceByWorkDate 오류: " + e.getMessage());
            throw new RuntimeException("날짜별 출근 기록 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // ===== 📈 통계 & 대시보드 메서드 =====

    // 6. 오늘의 출근 현황 대시보드 (Controller에서 구현)

    // 7. 특정 사용자의 출근 통계 (Controller에서 구현)

    // 8. 월별 출근 통계
    public Map<String, Object> getMonthlyAttendanceStats(Long userId, int year, int month) {
        try {
            System.out.println("=== AttendanceService.getMonthlyAttendanceStats 시작 ===");
            System.out.println("사용자 ID: " + userId + ", 년도: " + year + ", 월: " + month);

            Map<String, Object> stats = new HashMap<>();

            // 출근 일수
            Long attendanceDays = attendanceRepository.countAttendanceDaysByUserAndMonth(userId, year, month);

            // 평균 근무시간 (분 단위)
            Double avgWorkingMinutes = attendanceRepository.getAverageWorkingMinutesByUserAndMonth(userId, year, month);

            // 해당 월의 정상출근/지각 횟수
            LocalDate startDate = LocalDate.of(year, month, 1);
            LocalDate endDate = startDate.plusMonths(1).minusDays(1);

            List<Attendance> monthlyAttendance = getAttendanceByUserAndDateRange(userId, startDate, endDate);
            long presentCount = monthlyAttendance.stream()
                    .filter(a -> a.getStatus() != null && "PRESENT".equals(a.getStatus().toString()))
                    .count();
            long lateCount = monthlyAttendance.stream()
                    .filter(a -> a.getStatus() != null && "LATE".equals(a.getStatus().toString()))
                    .count();

            stats.put("userId", userId);
            stats.put("year", year);
            stats.put("month", month);
            stats.put("attendanceDays", attendanceDays != null ? attendanceDays : 0);
            stats.put("presentCount", presentCount);
            stats.put("lateCount", lateCount);
            stats.put("averageWorkingMinutes", avgWorkingMinutes != null ? avgWorkingMinutes : 0);
            stats.put("averageWorkingHours", avgWorkingMinutes != null ? avgWorkingMinutes / 60.0 : 0);

            System.out.println("월별 출근 통계 조회 완료");
            return stats;
        } catch (Exception e) {
            System.err.println("AttendanceService.getMonthlyAttendanceStats 오류: " + e.getMessage());
            throw new RuntimeException("월별 출근 통계 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // 최근 5개 출근 기록 조회 (전체)
    public List<Attendance> getRecentAttendancesByUserId(Long userId) {
        return attendanceRepository.findTop5ByUserIdOrderByCheckInDesc(userId);
    }

    // ===== 🔍 상태별 조회 메서드 =====

    // 9. 상태별 출근 기록 조회
    public List<Attendance> getAttendanceByStatus(AttendanceStatus status) {
        try {
            System.out.println("=== AttendanceService.getAttendanceByStatus 시작 ===");
            System.out.println("Status: " + status);

            List<Attendance> attendanceList = attendanceRepository.findByStatus(status);
            System.out.println("Status별 출근 기록 조회 완료: " + attendanceList.size() + "개");

            return attendanceList;
        } catch (Exception e) {
            System.err.println("AttendanceService.getAttendanceByStatus 오류: " + e.getMessage());
            throw new RuntimeException("Status별 출근 기록 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // 특정 사용자의 특정 상태 출근 기록 조회
    public List<Attendance> getUserAttendanceByStatus(Long userId, AttendanceStatus status) {
        try {
            System.out.println("=== AttendanceService.getUserAttendanceByStatus 시작 ===");
            System.out.println("userId: " + userId + ", Status: " + status);

            List<Attendance> attendanceList = attendanceRepository.findByUserIdAndStatus(userId, status);
            System.out.println("사용자별 Status 출근 기록 조회 완료: " + attendanceList.size() + "개");

            return attendanceList;
        } catch (Exception e) {
            System.err.println("AttendanceService.getUserAttendanceByStatus 오류: " + e.getMessage());
            throw new RuntimeException("사용자별 Status 출근 기록 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // 특정 날짜의 특정 상태 출근 기록 조회
    public List<Attendance> getAttendanceByDateAndStatus(LocalDate workDate, AttendanceStatus status) {
        try {
            System.out.println("=== AttendanceService.getAttendanceByDateAndStatus 시작 ===");
            System.out.println("workDate: " + workDate + ", Status: " + status);

            List<Attendance> attendanceList = attendanceRepository.findByWorkDateAndStatus(workDate, status);
            System.out.println("날짜별 Status 출근 기록 조회 완료: " + attendanceList.size() + "개");

            return attendanceList;
        } catch (Exception e) {
            System.err.println("AttendanceService.getAttendanceByDateAndStatus 오류: " + e.getMessage());
            throw new RuntimeException("날짜별 Status 출근 기록 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // 🆕 특정 사용자의 특정 날짜 + 상태 출근 기록 조회 (Controller에서 필요)
    public List<Attendance> getUserAttendanceByStatusAndDate(Long userId, AttendanceStatus status, LocalDate date) {
        try {
            System.out.println("=== AttendanceService.getUserAttendanceByStatusAndDate 시작 ===");
            System.out.println("userId: " + userId + ", Status: " + status + ", date: " + date);

            List<Attendance> attendanceList = attendanceRepository.findByUserIdAndStatusAndWorkDate(userId, status, date);
            System.out.println("사용자별 날짜별 Status 출근 기록 조회 완료: " + attendanceList.size() + "개");

            return attendanceList;
        } catch (Exception e) {
            System.err.println("AttendanceService.getUserAttendanceByStatusAndDate 오류: " + e.getMessage());
            throw new RuntimeException("사용자별 날짜별 Status 출근 기록 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // 10. 미퇴근 기록 조회
    public List<Attendance> getNotCheckedOutAttendance() {
        try {
            System.out.println("=== AttendanceService.getNotCheckedOutAttendance 시작 ===");

            List<Attendance> attendanceList = attendanceRepository.findByCheckOutIsNull();
            System.out.println("미퇴근 기록 조회 완료: " + attendanceList.size() + "개");

            return attendanceList;
        } catch (Exception e) {
            System.err.println("AttendanceService.getNotCheckedOutAttendance 오류: " + e.getMessage());
            throw new RuntimeException("미퇴근 기록 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // 특정 사용자의 미퇴근 기록 조회
    public Optional<Attendance> getUserNotCheckedOutAttendance(Long userId) {
        try {
            System.out.println("=== AttendanceService.getUserNotCheckedOutAttendance 시작 ===");
            System.out.println("사용자 ID: " + userId);

            Optional<Attendance> attendance = attendanceRepository.findByUserIdAndCheckOutIsNull(userId);
            System.out.println("사용자별 미퇴근 기록 조회 완료");

            return attendance;
        } catch (Exception e) {
            System.err.println("AttendanceService.getUserNotCheckedOutAttendance 오류: " + e.getMessage());
            throw new RuntimeException("사용자별 미퇴근 기록 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // ===== ⚙️ 관리 메서드 =====

    // 11. 출근 기록 생성 - Status 자동 설정
    public Attendance createAttendance(Attendance attendance) {
        try {
            System.out.println("=== AttendanceService.createAttendance 시작 ===");
            System.out.println("생성할 출근 기록: " + attendance);

            // 기본값 설정
            if (attendance.getWorkDate() == null) {
                attendance.setWorkDate(LocalDate.now());
            }

            // Status 자동 설정
            if (attendance.getCheckIn() != null && attendance.getStatus() == null) {
                attendance.setStatus(calculateAttendanceStatus(attendance.getCheckIn()));
            }

            // User 객체 설정
            if (attendance.getUser() == null && attendance.getUserId() != null) {
                Optional<User> userOptional = userRepository.findById(attendance.getUserId());
                if (userOptional.isPresent()) {
                    attendance.setUser(userOptional.get());
                    System.out.println("User 설정 완료: " + userOptional.get().getUsername());
                } else {
                    throw new RuntimeException("사용자를 찾을 수 없습니다. userId: " + attendance.getUserId());
                }
            }

            Attendance savedAttendance = attendanceRepository.save(attendance);
            System.out.println("출근 기록 생성 완료: " + savedAttendance.getUserId() + ", Status: " + savedAttendance.getStatus());

            return savedAttendance;
        } catch (Exception e) {
            System.err.println("AttendanceService.createAttendance 오류: " + e.getMessage());
            throw new RuntimeException("출근 기록 생성 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // 출근 기록 수정
    public Attendance updateAttendance(Long attendanceId, Attendance attendanceDetails) {
        try {
            System.out.println("=== AttendanceService.updateAttendance 시작 ===");
            System.out.println("출근 기록 ID: " + attendanceId);

            Optional<Attendance> attendanceOpt = attendanceRepository.findById(attendanceId);
            if (!attendanceOpt.isPresent()) {
                throw new RuntimeException("출근 기록을 찾을 수 없습니다. ID: " + attendanceId);
            }

            Attendance attendance = attendanceOpt.get();

            // 수정 가능한 필드들 업데이트
            if (attendanceDetails.getWorkDate() != null) {
                attendance.setWorkDate(attendanceDetails.getWorkDate());
            }
            if (attendanceDetails.getCheckIn() != null) {
                attendance.setCheckIn(attendanceDetails.getCheckIn());
                // 출근 시간이 변경되면 Status 재계산
                attendance.setStatus(calculateAttendanceStatus(attendanceDetails.getCheckIn()));
            }
            if (attendanceDetails.getCheckOut() != null) {
                attendance.setCheckOut(attendanceDetails.getCheckOut());
            }

            Attendance updatedAttendance = attendanceRepository.save(attendance);
            System.out.println("출근 기록 수정 완료: " + updatedAttendance.getUserId());

            return updatedAttendance;
        } catch (Exception e) {
            System.err.println("AttendanceService.updateAttendance 오류: " + e.getMessage());
            throw new RuntimeException("출근 기록 수정 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // 12. 출근 기록 삭제
    public void deleteAttendance(Long attendanceId) {
        try {
            System.out.println("=== AttendanceService.deleteAttendance 시작 ===");
            System.out.println("출근 기록 ID: " + attendanceId);

            if (!attendanceRepository.existsById(attendanceId)) {
                throw new RuntimeException("출근 기록을 찾을 수 없습니다. ID: " + attendanceId);
            }

            attendanceRepository.deleteById(attendanceId);
            System.out.println("출근 기록 삭제 완료: " + attendanceId);

        } catch (Exception e) {
            System.err.println("AttendanceService.deleteAttendance 오류: " + e.getMessage());
            throw new RuntimeException("출근 기록 삭제 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // 13. ID로 출근 기록 조회
    public Optional<Attendance> getAttendanceById(Long attendanceId) {
        try {
            System.out.println("=== AttendanceService.getAttendanceById 시작 ===");
            System.out.println("출근 기록 ID: " + attendanceId);

            Optional<Attendance> attendance = attendanceRepository.findById(attendanceId);
            System.out.println("ID별 출근 기록 조회 완료");

            return attendance;
        } catch (Exception e) {
            System.err.println("AttendanceService.getAttendanceById 오류: " + e.getMessage());
            throw new RuntimeException("출근 기록 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // ===== 🛠️ 통계 지원 메서드 =====

    // 오늘의 정상 출근자 조회
    public List<Attendance> getTodayPresentEmployees() {
        LocalDate today = LocalDate.now();
        return attendanceRepository.findByWorkDateAndStatus(today, AttendanceStatus.PRESENT);
    }

    // 오늘의 지각자 조회
    public List<Attendance> getTodayLateEmployees() {
        LocalDate today = LocalDate.now();
        return attendanceRepository.findByWorkDateAndStatus(today, AttendanceStatus.LATE);
    }

    // 특정 사용자의 지각 횟수 조회
    public long getUserLateCount(Long userId) {
        List<Attendance> lateRecords = attendanceRepository.findByUserIdAndStatus(userId, AttendanceStatus.LATE);
        return lateRecords.size();
    }

    // 특정 사용자의 정상 출근 횟수 조회
    public long getUserPresentCount(Long userId) {
        List<Attendance> presentRecords = attendanceRepository.findByUserIdAndStatus(userId, AttendanceStatus.PRESENT);
        return presentRecords.size();
    }

    // 사용자별 총 근무시간 조회
    public Long getTotalWorkingTime(Long userId, LocalDate startDate, LocalDate endDate) {
        try {
            System.out.println("=== AttendanceService.getTotalWorkingTime 시작 ===");
            System.out.println("사용자 ID: " + userId + ", 시작일: " + startDate + ", 종료일: " + endDate);

            Long totalMinutes = attendanceRepository.getTotalWorkingMinutesByUserAndPeriod(userId, startDate, endDate);
            System.out.println("총 근무시간 조회 완료: " + (totalMinutes != null ? totalMinutes : 0) + "분");

            return totalMinutes != null ? totalMinutes : 0L;
        } catch (Exception e) {
            System.err.println("AttendanceService.getTotalWorkingTime 오류: " + e.getMessage());
            throw new RuntimeException("총 근무시간 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // 사용자의 특정 날짜 출근 기록 조회
    public Optional<Attendance> getAttendanceByUserAndWorkDate(Long userId, LocalDate workDate) {
        try {
            System.out.println("=== AttendanceService.getAttendanceByUserAndWorkDate 시작 ===");
            System.out.println("사용자 ID: " + userId + ", 근무 날짜: " + workDate);

            Optional<Attendance> attendance = attendanceRepository.findByUserIdAndWorkDate(userId, workDate);
            System.out.println("사용자-날짜별 출근 기록 조회 완료");

            return attendance;
        } catch (Exception e) {
            System.err.println("AttendanceService.getAttendanceByUserAndWorkDate 오류: " + e.getMessage());
            throw new RuntimeException("사용자-날짜별 출근 기록 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // ===== 🛠️ 유틸리티 메서드 =====

    // 🕘 출근 상태 계산 메서드
    private AttendanceStatus calculateAttendanceStatus(LocalDateTime checkInTime) {
        LocalTime checkInTimeOnly = checkInTime.toLocalTime();

        System.out.println("=== 출근 상태 계산 ===");
        System.out.println("출근 시간: " + checkInTimeOnly);
        System.out.println("기준 시간: " + WORK_START_TIME);

        if (checkInTimeOnly.isAfter(WORK_START_TIME)) {
            System.out.println("결과: LATE (지각)");
            return AttendanceStatus.LATE;
        } else {
            System.out.println("결과: PRESENT (정상출근)");
            return AttendanceStatus.PRESENT;
        }
    }

    // ===== 🗑️ 제거된 중복 메서드들 =====
    // - getLateArrivals (상태별 조회로 통합)
    // - getRecentAttendanceByUserId (사용자별 조회에서 정렬로 해결)
    // - getAllAttendance (사용하지 않음)
}
