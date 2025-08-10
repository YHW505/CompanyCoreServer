package com.example.companycoreserver.controller;

import com.example.companycoreserver.dto.TaskRequest;
import com.example.companycoreserver.dto.TaskResponse;
import com.example.companycoreserver.entity.Enum.TaskStatus;
import com.example.companycoreserver.entity.Enum.AssignmentRole;
import com.example.companycoreserver.service.TaskService;
import com.example.companycoreserver.service.TaskService.TaskStatsDto;
import com.example.companycoreserver.service.TaskService.AssignmentStatsDto;
import com.example.companycoreserver.service.TaskService.DashboardDto;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "*")
public class TaskController {

    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    // ========================================
    // 🎯 작업 관리 API
    // ========================================

    /**
     * 작업 생성
     * POST /api/tasks
     */
    @PostMapping
    public ResponseEntity<ApiResponse<TaskResponse>> createTask(
            @Valid @RequestBody TaskRequest request,
            @RequestHeader("User-Id") Long createdByUserId) {
        try {
            TaskResponse taskResponse = taskService.createTask(request, createdByUserId);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("작업이 성공적으로 생성되었습니다.", taskResponse));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("작업 생성 실패: " + e.getMessage()));
        }
    }

    /**
     * 작업 상세 조회
     * GET /api/tasks/{taskId}
     */
    @GetMapping("/{taskId}")
    public ResponseEntity<ApiResponse<TaskResponse>> getTask(@PathVariable Long taskId) {
        try {
            TaskResponse taskResponse = taskService.getTask(taskId);
            return ResponseEntity.ok(ApiResponse.success("작업 조회 성공", taskResponse));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("작업 조회 실패: " + e.getMessage()));
        }
    }

    /**
     * 작업 수정
     * PUT /api/tasks/{taskId}
     */
    @PutMapping("/{taskId}")
    public ResponseEntity<ApiResponse<TaskResponse>> updateTask(
            @PathVariable Long taskId,
            @Valid @RequestBody TaskRequest request,
            @RequestHeader("User-Id") Long updatedByUserId) {
        try {
            TaskResponse taskResponse = taskService.updateTask(taskId, request, updatedByUserId);
            return ResponseEntity.ok(ApiResponse.success("작업이 성공적으로 수정되었습니다.", taskResponse));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("작업 수정 실패: " + e.getMessage()));
        }
    }

    /**
     * 작업 상태 변경
     * PATCH /api/tasks/{taskId}/status
     */
    @PatchMapping("/{taskId}/status")
    public ResponseEntity<ApiResponse<TaskResponse>> updateTaskStatus(
            @PathVariable Long taskId,
            @RequestParam TaskStatus status,
            @RequestHeader("User-Id") Long updatedByUserId) {
        try {
            TaskResponse taskResponse = taskService.updateTaskStatus(taskId, status, updatedByUserId);
            return ResponseEntity.ok(ApiResponse.success("작업 상태가 성공적으로 변경되었습니다.", taskResponse));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("작업 상태 변경 실패: " + e.getMessage()));
        }
    }

    /**
     * 작업 삭제
     * DELETE /api/tasks/{taskId}
     */
    @DeleteMapping("/{taskId}")
    public ResponseEntity<ApiResponse<Void>> deleteTask(
            @PathVariable Long taskId,
            @RequestHeader("User-Id") Long deletedByUserId) {
        try {
            taskService.deleteTask(taskId, deletedByUserId);
            return ResponseEntity.ok(ApiResponse.success("작업이 성공적으로 삭제되었습니다.", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("작업 삭제 실패: " + e.getMessage()));
        }
    }

    // ========================================
    // 🎯 할당 관리 API
    // ========================================

    /**
     * 작업에 사용자 할당
     * POST /api/tasks/{taskId}/assign
     */
    @PostMapping("/{taskId}/assign")
    public ResponseEntity<ApiResponse<TaskResponse>> assignUserToTask(
            @PathVariable Long taskId,
            @RequestParam Long userId,
            @RequestParam(defaultValue = "ASSIGNEE") AssignmentRole role,
            @RequestHeader("User-Id") Long assignedByUserId) {
        try {
            TaskResponse taskResponse = taskService.assignUserToTask(taskId, userId, role, assignedByUserId);
            return ResponseEntity.ok(ApiResponse.success("사용자가 성공적으로 할당되었습니다.", taskResponse));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("사용자 할당 실패: " + e.getMessage()));
        }
    }

    /**
     * 할당 해제
     * DELETE /api/tasks/{taskId}/assign/{userId}
     */
    @DeleteMapping("/{taskId}/assign/{userId}")
    public ResponseEntity<ApiResponse<TaskResponse>> unassignUser(
            @PathVariable Long taskId,
            @PathVariable Long userId,
            @RequestHeader("User-Id") Long unassignedByUserId) {
        try {
            TaskResponse taskResponse = taskService.unassignUser(taskId, userId, unassignedByUserId);
            return ResponseEntity.ok(ApiResponse.success("할당이 성공적으로 해제되었습니다.", taskResponse));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("할당 해제 실패: " + e.getMessage()));
        }
    }

    /**
     * 할당 역할 변경
     * PATCH /api/tasks/{taskId}/assign/{userId}/role
     */
    @PatchMapping("/{taskId}/assign/{userId}/role")
    public ResponseEntity<ApiResponse<TaskResponse>> updateAssignmentRole(
            @PathVariable Long taskId,
            @PathVariable Long userId,
            @RequestParam AssignmentRole role,
            @RequestHeader("User-Id") Long updatedByUserId) {
        try {
            TaskResponse taskResponse = taskService.updateAssignmentRole(taskId, userId, role, updatedByUserId);
            return ResponseEntity.ok(ApiResponse.success("할당 역할이 성공적으로 변경되었습니다.", taskResponse));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("할당 역할 변경 실패: " + e.getMessage()));
        }
    }

    // ========================================
    // 🎯 사용자별 작업 조회 API
    // ========================================

    /**
     * 사용자의 모든 작업 조회
     * GET /api/tasks/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<TaskResponse>>> getUserTasks(
            @PathVariable Long userId,
            @RequestParam(required = false) TaskStatus status) {
        try {
            List<TaskResponse> tasks = taskService.getUserTasks(userId, status);
            String message = status != null
                    ? String.format("사용자의 %s 상태 작업 조회 성공", status.name())
                    : "사용자의 모든 작업 조회 성공";
            return ResponseEntity.ok(ApiResponse.success(message, tasks));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("사용자 작업 조회 실패: " + e.getMessage()));
        }
    }

    /**
     * 역할별 사용자 작업 조회
     * GET /api/tasks/user/{userId}/role/{role}
     */
    @GetMapping("/user/{userId}/role/{role}")
    public ResponseEntity<ApiResponse<List<TaskResponse>>> getUserTasksByRole(
            @PathVariable Long userId,
            @PathVariable AssignmentRole role) {
        try {
            List<TaskResponse> tasks = taskService.getUserTasksByRole(userId, role);
            return ResponseEntity.ok(ApiResponse.success(
                    String.format("%s 역할의 작업 조회 성공", role.name()), tasks));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("역할별 작업 조회 실패: " + e.getMessage()));
        }
    }

    /**
     * 담당자 작업 조회 (ASSIGNEE)
     * GET /api/tasks/user/{userId}/assignee
     */
    @GetMapping("/user/{userId}/assignee")
    public ResponseEntity<ApiResponse<List<TaskResponse>>> getAssigneeTasks(@PathVariable Long userId) {
        try {
            List<TaskResponse> tasks = taskService.getAssigneeTasks(userId);
            return ResponseEntity.ok(ApiResponse.success("담당 작업 조회 성공", tasks));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("담당 작업 조회 실패: " + e.getMessage()));
        }
    }

    /**
     * 리뷰 작업 조회 (REVIEWER)
     * GET /api/tasks/user/{userId}/review
     */
    @GetMapping("/user/{userId}/review")
    public ResponseEntity<ApiResponse<List<TaskResponse>>> getReviewTasks(@PathVariable Long userId) {
        try {
            List<TaskResponse> tasks = taskService.getReviewTasks(userId);
            return ResponseEntity.ok(ApiResponse.success("리뷰 작업 조회 성공", tasks));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("리뷰 작업 조회 실패: " + e.getMessage()));
        }
    }

    /**
     * 관찰 작업 조회 (OBSERVER)
     * GET /api/tasks/user/{userId}/observe
     */
    @GetMapping("/user/{userId}/observe")
    public ResponseEntity<ApiResponse<List<TaskResponse>>> getObservedTasks(@PathVariable Long userId) {
        try {
            List<TaskResponse> tasks = taskService.getObservedTasks(userId);
            return ResponseEntity.ok(ApiResponse.success("관찰 작업 조회 성공", tasks));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("관찰 작업 조회 실패: " + e.getMessage()));
        }
    }

    // ========================================
    // 🔍 검색 및 필터링 API
    // ========================================

    /**
     * 작업 검색
     * GET /api/tasks/search
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<TaskResponse>>> searchTasks(
            @RequestParam String keyword,
            @RequestParam(required = false) Long userId) {
        try {
            List<TaskResponse> tasks = taskService.searchTasks(keyword, userId);
            String message = userId != null
                    ? "사용자별 작업 검색 성공"
                    : "전체 작업 검색 성공";
            return ResponseEntity.ok(ApiResponse.success(message, tasks));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("작업 검색 실패: " + e.getMessage()));
        }
    }

    /**
     * 부서별 작업 조회
     * GET /api/tasks/department/{department}
     */
    @GetMapping("/department/{department}")
    public ResponseEntity<ApiResponse<List<TaskResponse>>> getTasksByDepartment(@PathVariable String department) {
        try {
            List<TaskResponse> tasks = taskService.getTasksByDepartment(department);
            return ResponseEntity.ok(ApiResponse.success("부서별 작업 조회 성공", tasks));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("부서별 작업 조회 실패: " + e.getMessage()));
        }
    }

    // ========================================
    // 📅 날짜 기반 조회 API
    // ========================================

    /**
     * 최근 작업 조회
     * GET /api/tasks/user/{userId}/recent
     */
    @GetMapping("/user/{userId}/recent")
    public ResponseEntity<ApiResponse<List<TaskResponse>>> getRecentTasks(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "5") int limit) {
        try {
            List<TaskResponse> tasks = taskService.getRecentTasks(userId, limit);
            return ResponseEntity.ok(ApiResponse.success("최근 작업 조회 성공", tasks));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("최근 작업 조회 실패: " + e.getMessage()));
        }
    }

    /**
     * 오늘 마감 작업 조회
     * GET /api/tasks/user/{userId}/today-deadline
     */
    @GetMapping("/user/{userId}/today-deadline")
    public ResponseEntity<ApiResponse<List<TaskResponse>>> getTodayDeadlineTasks(@PathVariable Long userId) {
        try {
            List<TaskResponse> tasks = taskService.getTodayDeadlineTasks(userId);
            return ResponseEntity.ok(ApiResponse.success("오늘 마감 작업 조회 성공", tasks));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("오늘 마감 작업 조회 실패: " + e.getMessage()));
        }
    }

    /**
     * 연체 작업 조회
     * GET /api/tasks/user/{userId}/overdue
     */
    @GetMapping("/user/{userId}/overdue")
    public ResponseEntity<ApiResponse<List<TaskResponse>>> getOverdueTasks(@PathVariable Long userId) {
        try {
            List<TaskResponse> tasks = taskService.getOverdueTasks(userId);
            return ResponseEntity.ok(ApiResponse.success("연체 작업 조회 성공", tasks));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("연체 작업 조회 실패: " + e.getMessage()));
        }
    }

    /**
     * 날짜 범위별 작업 조회
     * GET /api/tasks/user/{userId}/date-range
     */
    @GetMapping("/user/{userId}/date-range")
    public ResponseEntity<ApiResponse<List<TaskResponse>>> getUserTasksByDateRange(
            @PathVariable Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            List<TaskResponse> tasks = taskService.getUserTasksByDateRange(userId, startDate, endDate);
            return ResponseEntity.ok(ApiResponse.success("날짜 범위별 작업 조회 성공", tasks));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("날짜 범위별 작업 조회 실패: " + e.getMessage()));
        }
    }

    /**
     * 특정 마감일 작업 조회
     * GET /api/tasks/user/{userId}/end-date
     */
    @GetMapping("/user/{userId}/end-date")
    public ResponseEntity<ApiResponse<List<TaskResponse>>> getUserTasksByEndDate(
            @PathVariable Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            List<TaskResponse> tasks = taskService.getUserTasksByEndDate(userId, endDate);
            return ResponseEntity.ok(ApiResponse.success("특정 마감일 작업 조회 성공", tasks));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("특정 마감일 작업 조회 실패: " + e.getMessage()));
        }
    }

    // ========================================
    // 📊 통계 및 대시보드 API
    // ========================================

    /**
     * 작업 통계 조회
     * GET /api/tasks/user/{userId}/stats
     */
    @GetMapping("/user/{userId}/stats")
    public ResponseEntity<ApiResponse<TaskStatsDto>> getTaskStats(@PathVariable Long userId) {
        try {
            TaskStatsDto stats = taskService.getTaskStats(userId);
            return ResponseEntity.ok(ApiResponse.success("작업 통계 조회 성공", stats));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("작업 통계 조회 실패: " + e.getMessage()));
        }
    }

    /**
     * 할당 통계 조회
     * GET /api/tasks/user/{userId}/assignment-stats
     */
    @GetMapping("/user/{userId}/assignment-stats")
    public ResponseEntity<ApiResponse<AssignmentStatsDto>> getAssignmentStats(@PathVariable Long userId) {
        try {
            AssignmentStatsDto stats = taskService.getAssignmentStats(userId);
            return ResponseEntity.ok(ApiResponse.success("할당 통계 조회 성공", stats));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("할당 통계 조회 실패: " + e.getMessage()));
        }
    }

    /**
     * 대시보드 데이터 조회
     * GET /api/tasks/user/{userId}/dashboard
     */
    @GetMapping("/user/{userId}/dashboard")
    public ResponseEntity<ApiResponse<DashboardDto>> getDashboardData(@PathVariable Long userId) {
        try {
            DashboardDto dashboard = taskService.getDashboardData(userId);
            return ResponseEntity.ok(ApiResponse.success("대시보드 데이터 조회 성공", dashboard));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("대시보드 데이터 조회 실패: " + e.getMessage()));
        }
    }

    // ========================================
    // 🛠️ 공통 응답 클래스
    // ========================================

    public static class ApiResponse<T> {
        private boolean success;
        private String message;
        private T data;

        private ApiResponse(boolean success, String message, T data) {
            this.success = success;
            this.message = message;
            this.data = data;
        }

        public static <T> ApiResponse<T> success(String message, T data) {
            return new ApiResponse<>(true, message, data);
        }

        public static <T> ApiResponse<T> error(String message) {
            return new ApiResponse<>(false, message, null);
        }

        // Getters
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public T getData() { return data; }
    }
}
