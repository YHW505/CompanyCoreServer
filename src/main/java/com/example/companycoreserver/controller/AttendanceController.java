package com.example.companycoreserver.controller;

import com.example.companycoreserver.dto.AttendanceResponse;
import com.example.companycoreserver.entity.Attendance;
import com.example.companycoreserver.entity.Enum.AttendanceStatus;
import com.example.companycoreserver.service.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/attendance")
@CrossOrigin(origins = "*")
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    // ===== 📝 출근/퇴근 처리 APIs (Command) =====

    // 1. 출근 처리
    @PostMapping("/check-in")
    public ResponseEntity<AttendanceResponse> checkIn(@RequestParam Long userId) {
        try {
            System.out.println("=== POST /api/attendance/check-in 요청 받음 ===");
            System.out.println("userId: " + userId);

            Attendance attendance = attendanceService.checkIn(userId);
            AttendanceResponse responseDTO = convertToSafeDTO(attendance);

            System.out.println("=== 출근 처리 완료: " + attendance.getAttendanceId() + " ===");
            return ResponseEntity.ok(responseDTO);

        } catch (Exception e) {
            System.err.println("출근 처리 중 오류: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    // 2. 퇴근 처리
    @PostMapping("/check-out")
    public ResponseEntity<AttendanceResponse> checkOut(@RequestParam Long userId) {
        try {
            System.out.println("=== POST /api/attendance/check-out 요청 받음 ===");
            System.out.println("userId: " + userId);

            Attendance attendance = attendanceService.checkOut(userId);
            AttendanceResponse responseDTO = convertToSafeDTO(attendance);

            System.out.println("=== 퇴근 처리 완료: " + attendance.getAttendanceId() + " ===");
            return ResponseEntity.ok(responseDTO);

        } catch (Exception e) {
            System.err.println("퇴근 처리 중 오류: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    // ===== 📊 조회 APIs (Query) =====

    // 3. 사용자별 출근 기록 조회
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AttendanceResponse>> getAttendanceByUserId(@PathVariable Long userId) {
        try {
            System.out.println("=== GET /api/attendance/user/" + userId + " 요청 받음 ===");

            List<Attendance> attendanceList = attendanceService.getAttendanceByUserId(userId);
            List<AttendanceResponse> responseList = convertToSafeDTOList(attendanceList);

            return ResponseEntity.ok(responseList);

        } catch (Exception e) {
            System.err.println("Error in getAttendanceByUserId: " + e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    // 4. 날짜 범위별 출근 기록 조회
    @GetMapping("/user/{userId}/date-range")
    public ResponseEntity<List<AttendanceResponse>> getAttendanceByUserAndDateRange(
            @PathVariable Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            System.out.println("=== GET /api/attendance/user/" + userId + "/date-range 요청 받음 ===");
            System.out.println("startDate: " + startDate + ", endDate: " + endDate);

            List<Attendance> attendanceList = attendanceService.getAttendanceByUserAndDateRange(userId, startDate, endDate);
            List<AttendanceResponse> responseList = convertToSafeDTOList(attendanceList);

            return ResponseEntity.ok(responseList);

        } catch (Exception e) {
            System.err.println("Error in getAttendanceByUserAndDateRange: " + e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    // 5. 특정 날짜의 모든 출근 기록 조회
    @GetMapping("/date/{workDate}")
    public ResponseEntity<List<AttendanceResponse>> getAttendanceByWorkDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate workDate) {
        try {
            System.out.println("=== GET /api/attendance/date/" + workDate + " 요청 받음 ===");

            List<Attendance> attendanceList = attendanceService.getAttendanceByWorkDate(workDate);
            List<AttendanceResponse> responseList = convertToSafeDTOList(attendanceList);

            return ResponseEntity.ok(responseList);

        } catch (Exception e) {
            System.err.println("Error in getAttendanceByWorkDate: " + e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    // ===== 📈 통계 & 대시보드 APIs =====

    // 6. 오늘의 출근 현황 대시보드
    @GetMapping("/today/dashboard")
    public ResponseEntity<Map<String, Object>> getTodayAttendanceDashboard() {
        try {
            System.out.println("=== GET /api/attendance/today/dashboard 요청 받음 ===");

            LocalDate today = LocalDate.now();

            // 오늘의 전체 출근자
            List<Attendance> todayAttendance = attendanceService.getAttendanceByWorkDate(today);

            // 정상 출근자와 지각자 분리
            List<Attendance> presentEmployees = attendanceService.getTodayPresentEmployees();
            List<Attendance> lateEmployees = attendanceService.getTodayLateEmployees();

            // 미퇴근자
            List<Attendance> notCheckedOut = attendanceService.getNotCheckedOutAttendance();

            Map<String, Object> dashboard = new HashMap<>();
            dashboard.put("date", today);
            dashboard.put("totalAttendance", todayAttendance.size());
            dashboard.put("presentCount", presentEmployees.size());
            dashboard.put("lateCount", lateEmployees.size());
            dashboard.put("notCheckedOutCount", notCheckedOut.size());

            // 출근율 계산
            int totalEmployees = todayAttendance.size();
            if (totalEmployees > 0) {
                double presentRate = (double) presentEmployees.size() / totalEmployees * 100;
                double lateRate = (double) lateEmployees.size() / totalEmployees * 100;
                dashboard.put("presentRate", Math.round(presentRate * 100.0) / 100.0);
                dashboard.put("lateRate", Math.round(lateRate * 100.0) / 100.0);
            } else {
                dashboard.put("presentRate", 0.0);
                dashboard.put("lateRate", 0.0);
            }

            return ResponseEntity.ok(dashboard);

        } catch (Exception e) {
            System.err.println("Error in getTodayAttendanceDashboard: " + e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    // 7. 특정 사용자의 출근 통계
    @GetMapping("/user/{userId}/stats")
    public ResponseEntity<Map<String, Object>> getUserAttendanceStats(
            @PathVariable Long userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            System.out.println("=== GET /api/attendance/user/" + userId + "/stats 요청 받음 ===");
            System.out.println("startDate: " + startDate + ", endDate: " + endDate);

            // 날짜 범위가 지정되지 않으면 전체 기간
            long presentCount, lateCount;
            Long totalWorkingMinutes = null;

            if (startDate != null && endDate != null) {
                // 날짜 범위 내 통계
                List<Attendance> attendanceInRange = attendanceService.getAttendanceByUserAndDateRange(userId, startDate, endDate);
                presentCount = attendanceInRange.stream().filter(a -> "PRESENT".equals(a.getStatus().toString())).count();
                lateCount = attendanceInRange.stream().filter(a -> "LATE".equals(a.getStatus().toString())).count();
                totalWorkingMinutes = attendanceService.getTotalWorkingTime(userId, startDate, endDate);
            } else {
                // 전체 기간 통계
                presentCount = attendanceService.getUserPresentCount(userId);
                lateCount = attendanceService.getUserLateCount(userId);
                // 전체 기간의 총 근무시간은 별도 메서드 필요 (여기서는 null로 처리)
            }

            long totalCount = presentCount + lateCount;

            // 출근율 계산 (소수점 2자리)
            double presentRate = totalCount > 0 ? (double) presentCount / totalCount * 100 : 0.0;
            double lateRate = totalCount > 0 ? (double) lateCount / totalCount * 100 : 0.0;

            // 총 근무시간 포맷팅
            String totalWorkingTime = "";
            if (totalWorkingMinutes != null && totalWorkingMinutes > 0) {
                long hours = totalWorkingMinutes / 60;
                long minutes = totalWorkingMinutes % 60;
                totalWorkingTime = hours + "시간 " + minutes + "분";
            } else {
                totalWorkingTime = "0시간 0분";
            }

            Map<String, Object> response = new HashMap<>();
            response.put("userId", userId);
            response.put("startDate", startDate);
            response.put("endDate", endDate);
            response.put("totalCount", totalCount);
            response.put("presentCount", presentCount);
            response.put("lateCount", lateCount);
            response.put("presentRate", Math.round(presentRate * 100.0) / 100.0);
            response.put("lateRate", Math.round(lateRate * 100.0) / 100.0);
            response.put("totalWorkingMinutes", totalWorkingMinutes != null ? totalWorkingMinutes : 0);
            response.put("totalWorkingTime", totalWorkingTime);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("Error in getUserAttendanceStats: " + e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    // 8. 월별 출근 통계
    @GetMapping("/monthly-stats")
    public ResponseEntity<?> getMonthlyAttendanceStats(
            @RequestParam Long userId,
            @RequestParam int year,
            @RequestParam int month) {
        try {
            System.out.println("=== GET /api/attendance/monthly-stats 요청 받음 ===");
            System.out.println("userId: " + userId + ", year: " + year + ", month: " + month);

            Object stats = attendanceService.getMonthlyAttendanceStats(userId, year, month);
            return ResponseEntity.ok(stats);

        } catch (Exception e) {
            System.err.println("Error in getMonthlyAttendanceStats: " + e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    // 14. 최근 출근 기록 5개 조회 (전체)
    @GetMapping("/recent")
    public ResponseEntity<Map<String, Object>> getRecentAttendances(@RequestParam Long userId) {
        try {
            System.out.println("사용자 " + userId + "의 최근 출근 기록 조회 API 호출");

            List<Attendance> attendances = attendanceService.getRecentAttendancesByUserId(userId);
            List<AttendanceResponse> attendanceResponses = convertToSafeDTOList(attendances);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "최근 출근 기록을 성공적으로 조회했습니다.");
            result.put("data", attendanceResponses);
            result.put("count", attendanceResponses.size());
            result.put("userId", userId);

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            System.err.println("최근 출근 기록 조회 실패: " + e.getMessage());

            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "최근 출근 기록 조회에 실패했습니다: " + e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResult);
        }
    }

    // ===== 🔍 상태별 조회 APIs =====

    // 9. 상태별 출근 기록 조회
    @GetMapping("/status/{status}")
    public ResponseEntity<List<AttendanceResponse>> getAttendanceByStatus(
            @PathVariable AttendanceStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) Long userId) {
        try {
            System.out.println("=== GET /api/attendance/status/" + status + " 요청 받음 ===");
            System.out.println("date: " + date + ", userId: " + userId);

            List<Attendance> attendanceList;

            if (date != null && userId != null) {
                // 특정 날짜 + 특정 사용자 + 상태
                attendanceList = attendanceService.getUserAttendanceByStatusAndDate(userId, status, date);
            } else if (date != null) {
                // 특정 날짜 + 상태
                attendanceList = attendanceService.getAttendanceByDateAndStatus(date, status);
            } else if (userId != null) {
                // 특정 사용자 + 상태
                attendanceList = attendanceService.getUserAttendanceByStatus(userId, status);
            } else {
                // 상태만
                attendanceList = attendanceService.getAttendanceByStatus(status);
            }

            List<AttendanceResponse> responseList = convertToSafeDTOList(attendanceList);
            return ResponseEntity.ok(responseList);

        } catch (Exception e) {
            System.err.println("Error in getAttendanceByStatus: " + e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    // 10. 미퇴근 기록 조회
    @GetMapping("/not-checked-out")
    public ResponseEntity<List<AttendanceResponse>> getNotCheckedOutAttendance(
            @RequestParam(required = false) Long userId) {
        try {
            System.out.println("=== GET /api/attendance/not-checked-out 요청 받음 ===");
            System.out.println("userId: " + userId);

            List<Attendance> attendanceList;
            if (userId != null) {
                // 특정 사용자의 미퇴근 기록
                Optional<Attendance> attendance = attendanceService.getUserNotCheckedOutAttendance(userId);
                attendanceList = attendance.map(List::of).orElse(List.of());
            } else {
                // 전체 미퇴근 기록
                attendanceList = attendanceService.getNotCheckedOutAttendance();
            }

            List<AttendanceResponse> responseList = convertToSafeDTOList(attendanceList);
            return ResponseEntity.ok(responseList);

        } catch (Exception e) {
            System.err.println("Error in getNotCheckedOutAttendance: " + e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    // ===== ⚙️ 관리 APIs (Admin) =====

    // 11. 출근 기록 생성/수정
    @PostMapping
    public ResponseEntity<AttendanceResponse> createAttendance(@RequestBody Attendance attendance) {
        try {
            System.out.println("=== POST /api/attendance 요청 받음 ===");
            System.out.println("Attendance: " + attendance);

            Attendance savedAttendance = attendanceService.createAttendance(attendance);
            AttendanceResponse responseDTO = convertToSafeDTO(savedAttendance);

            return ResponseEntity.ok(responseDTO);

        } catch (Exception e) {
            System.err.println("출근 기록 생성 중 오류: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{attendanceId}")
    public ResponseEntity<AttendanceResponse> updateAttendance(
            @PathVariable Long attendanceId,
            @RequestBody Attendance attendance) {
        try {
            System.out.println("=== PUT /api/attendance/" + attendanceId + " 요청 받음 ===");

            Attendance updatedAttendance = attendanceService.updateAttendance(attendanceId, attendance);
            AttendanceResponse responseDTO = convertToSafeDTO(updatedAttendance);

            return ResponseEntity.ok(responseDTO);

        } catch (Exception e) {
            System.err.println("Error in updateAttendance: " + e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    // 12. 출근 기록 삭제
    @DeleteMapping("/{attendanceId}")
    public ResponseEntity<?> deleteAttendance(@PathVariable Long attendanceId) {
        try {
            System.out.println("=== DELETE /api/attendance/" + attendanceId + " 요청 받음 ===");

            attendanceService.deleteAttendance(attendanceId);
            return ResponseEntity.ok(Map.of("message", "출근 기록이 삭제되었습니다."));

        } catch (Exception e) {
            System.err.println("Error in deleteAttendance: " + e.getMessage());
            return ResponseEntity.status(500)
                    .body(Map.of("error", "출근 기록 삭제 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    // 13. ID로 출근 기록 조회
    @GetMapping("/{attendanceId}")
    public ResponseEntity<AttendanceResponse> getAttendanceById(@PathVariable Long attendanceId) {
        try {
            System.out.println("=== GET /api/attendance/" + attendanceId + " 요청 받음 ===");

            Optional<Attendance> attendance = attendanceService.getAttendanceById(attendanceId);
            if (attendance.isPresent()) {
                AttendanceResponse responseDTO = convertToSafeDTO(attendance.get());
                return ResponseEntity.ok(responseDTO);
            } else {
                return ResponseEntity.notFound().build();
            }

        } catch (Exception e) {
            System.err.println("Error in getAttendanceById: " + e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    // ===== 🛠️ 유틸리티 메서드 =====

    // 안전한 DTO 변환 메서드
    private AttendanceResponse convertToSafeDTO(Attendance attendance) {
        try {
            return new AttendanceResponse(attendance);
        } catch (Exception e) {
            System.err.println("DTO 변환 중 오류: " + e.getMessage());
            // 최소한의 정보만 담은 DTO 반환
            AttendanceResponse dto = new AttendanceResponse();
//            dto.setAttendanceId(attendance.getAttendanceId());
            dto.setUserId(attendance.getUserId());
            dto.setCheckIn(attendance.getCheckIn());
            dto.setCheckOut(attendance.getCheckOut());
            dto.setWorkDate(attendance.getWorkDate());
            dto.setWorkHours(attendance.getWorkHours());
            dto.setStatus(attendance.getStatus());
            return dto;
        }
    }

    // 안전한 DTO 리스트 변환 메서드
    private List<AttendanceResponse> convertToSafeDTOList(List<Attendance> attendanceList) {
        return attendanceList.stream()
                .map(this::convertToSafeDTO)
                .collect(Collectors.toList());
    }
}
