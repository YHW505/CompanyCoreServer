package com.example.companycoreserver.controller;

import com.example.companycoreserver.entity.Task;
import com.example.companycoreserver.entity.TaskAssignment;
import com.example.companycoreserver.entity.Enum.TaskStatus;
import com.example.companycoreserver.entity.Enum.TaskType;
import com.example.companycoreserver.entity.Enum.AssignmentRole;
import com.example.companycoreserver.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Map;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "*")
public class TaskController {

    @Autowired
    private TaskService taskService;

    // JWT 토큰 검증 (비활성화 상태)
    private boolean isValidToken(String token) {
        return true; // JWT 비활성화 상태이므로 항상 true
    }

    // 🔍 모든 작업 조회
    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks(
            @RequestHeader("Authorization") String token) {
        try {
            if (!isValidToken(token)) {
                return ResponseEntity.status(401).build();
            }

            List<Task> tasks = taskService.getAllTasks();
            return ResponseEntity.ok(tasks);

        } catch (Exception e) {
            System.err.println("Error in getAllTasks: " + e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    // 🔍 ID로 작업 조회
    @GetMapping("/{taskId}")
    public ResponseEntity<Task> getTaskById(
            @RequestHeader("Authorization") String token,
            @PathVariable Integer taskId) {
        try {
            if (!isValidToken(token)) {
                return ResponseEntity.status(401).build();
            }

            Optional<Task> task = taskService.getTaskById(taskId);
            if (task.isPresent()) {
                return ResponseEntity.ok(task.get());
            } else {
                return ResponseEntity.notFound().build();
            }

        } catch (Exception e) {
            System.err.println("Error in getTaskById: " + e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    // 🔍 특정 사용자가 할당받은 작업 조회 (TaskAssignment 기반)
    @GetMapping("/assigned-to/{userId}")
    public ResponseEntity<List<Task>> getTasksByAssignedTo(
            @RequestHeader("Authorization") String token,
            @PathVariable Long userId) {
        try {
            if (!isValidToken(token)) {
                return ResponseEntity.status(401).build();
            }

            List<Task> tasks = taskService.getTasksByAssignedTo(userId);
            return ResponseEntity.ok(tasks);

        } catch (Exception e) {
            System.err.println("Error in getTasksByAssignedTo: " + e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    // 🔍 특정 사용자가 생성한 작업 조회
    @GetMapping("/created-by/{userId}")
    public ResponseEntity<List<Task>> getTasksByCreatedBy(
            @RequestHeader("Authorization") String token,
            @PathVariable Long userId) {
        try {
            if (!isValidToken(token)) {
                return ResponseEntity.status(401).build();
            }

            List<Task> tasks = taskService.getTasksByCreatedBy(userId);
            return ResponseEntity.ok(tasks);

        } catch (Exception e) {
            System.err.println("Error in getTasksByCreatedBy: " + e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    // 🔍 상태별 작업 조회
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Task>> getTasksByStatus(
            @RequestHeader("Authorization") String token,
            @PathVariable TaskStatus status) {
        try {
            if (!isValidToken(token)) {
                return ResponseEntity.status(401).build();
            }

            List<Task> tasks = taskService.getTasksByStatus(status);
            return ResponseEntity.ok(tasks);

        } catch (Exception e) {
            System.err.println("Error in getTasksByStatus: " + e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    // 🔍 작업 타입별 조회
    @GetMapping("/type/{taskType}")
    public ResponseEntity<List<Task>> getTasksByType(
            @RequestHeader("Authorization") String token,
            @PathVariable TaskType taskType) {
        try {
            if (!isValidToken(token)) {
                return ResponseEntity.status(401).build();
            }

            List<Task> tasks = taskService.getTasksByType(taskType);
            return ResponseEntity.ok(tasks);

        } catch (Exception e) {
            System.err.println("Error in getTasksByType: " + e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    // 🔍 날짜 범위로 작업 조회 (시작일/마감일 통합)
    @GetMapping("/date-range")
    public ResponseEntity<List<Task>> getTasksByDateRange(
            @RequestHeader("Authorization") String token,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "start") String dateType) { // start 또는 end
        try {
            if (!isValidToken(token)) {
                return ResponseEntity.status(401).build();
            }

            List<Task> tasks;
            if ("end".equals(dateType)) {
                tasks = taskService.getTasksByEndDateRange(startDate, endDate);
            } else {
                tasks = taskService.getTasksByDateRange(startDate, endDate);
            }
            return ResponseEntity.ok(tasks);

        } catch (Exception e) {
            System.err.println("Error in getTasksByDateRange: " + e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    // 🔍 통합 검색 (제목, 설명)
    @GetMapping("/search")
    public ResponseEntity<List<Task>> searchTasks(
            @RequestHeader("Authorization") String token,
            @RequestParam String keyword,
            @RequestParam(defaultValue = "all") String searchType) { // title, description, all
        try {
            if (!isValidToken(token)) {
                return ResponseEntity.status(401).build();
            }

            List<Task> tasks;
            if ("title".equals(searchType)) {
                tasks = taskService.searchTasksByTitle(keyword);
            } else {
                tasks = taskService.searchTasksByTitleOrDescription(keyword);
            }
            return ResponseEntity.ok(tasks);

        } catch (Exception e) {
            System.err.println("Error in searchTasks: " + e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    // 🔍 복합 조건 검색 및 페이지네이션 통합
    @GetMapping("/filter")
    public ResponseEntity<?> getTasksByFilter(
            @RequestHeader("Authorization") String token,
            @RequestParam(required = false) Long createdBy,
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false) TaskType taskType,
            @RequestParam(required = false, defaultValue = "false") boolean paginated,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        try {
            if (!isValidToken(token)) {
                return ResponseEntity.status(401).build();
            }

            if (paginated && createdBy != null && taskType != null) {
                // 페이지네이션이 필요한 경우
                var result = taskService.getTasksByCreatedByAndTypeWithPagination(
                        createdBy, taskType, page, size, sortBy, sortDir);
                return ResponseEntity.ok(result);
            } else {
                // 일반 검색
                List<Task> tasks = taskService.getTasksByMultipleConditions(createdBy, status, taskType);
                return ResponseEntity.ok(tasks);
            }

        } catch (Exception e) {
            System.err.println("Error in getTasksByFilter: " + e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    // ✅ 작업 생성 (JSON만 지원)
    @PostMapping
    public ResponseEntity<Task> createTask(
            @RequestHeader("Authorization") String token,
            @RequestBody Task task) {
        try {
            if (!isValidToken(token)) {
                return ResponseEntity.status(401).build();
            }

            Task createdTask = taskService.createTask(task);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);

        } catch (Exception e) {
            System.err.println("Error in createTask: " + e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    // ✅ 작업 업데이트
    @PutMapping("/{taskId}")
    public ResponseEntity<Task> updateTask(
            @RequestHeader("Authorization") String token,
            @PathVariable Integer taskId,
            @RequestBody Task updatedTask) {
        try {
            if (!isValidToken(token)) {
                return ResponseEntity.status(401).build();
            }

            Task task = taskService.updateTask(taskId, updatedTask);
            return ResponseEntity.ok(task);

        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            }
            System.err.println("Error in updateTask: " + e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    // ✅ 첨부파일 관리 (Base64 문자열로 처리)
    @PutMapping("/{taskId}/attachment")
    public ResponseEntity<Task> updateTaskAttachment(
            @RequestHeader("Authorization") String token,
            @PathVariable Integer taskId,
            @RequestBody Map<String, String> attachmentData) { // filename, contentType, content(Base64)
        try {
            if (!isValidToken(token)) {
                return ResponseEntity.status(401).build();
            }

            Optional<Task> taskOpt = taskService.getTaskById(taskId);
            if (taskOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Task task = taskOpt.get();

            if (attachmentData.containsKey("content") &&
                    attachmentData.get("content") != null &&
                    !attachmentData.get("content").isEmpty()) {

                // 첨부파일 업데이트
                task.updateAttachment(
                        attachmentData.get("filename"),
                        attachmentData.get("contentType"),
                        attachmentData.get("content")
                );

                // 파일 크기 계산 (Base64 디코딩 후)
                try {
                    byte[] decodedBytes = java.util.Base64.getDecoder().decode(attachmentData.get("content"));
                    task.setAttachmentSize((long) decodedBytes.length);
                } catch (Exception e) {
                    System.err.println("Error calculating file size: " + e.getMessage());
                }
            } else {
                // 첨부파일 제거
                task.removeAttachment();
            }

            Task savedTask = taskService.updateTask(taskId, task);
            return ResponseEntity.ok(savedTask);

        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            }
            System.err.println("Error in updateTaskAttachment: " + e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    // ✅ 첨부파일 다운로드
    @GetMapping("/{taskId}/attachment")
    public ResponseEntity<byte[]> downloadAttachment(
            @RequestHeader("Authorization") String token,
            @PathVariable Integer taskId) {
        try {
            if (!isValidToken(token)) {
                return ResponseEntity.status(401).build();
            }

            Optional<Task> taskOpt = taskService.getTaskById(taskId);
            if (taskOpt.isPresent()) {
                Task task = taskOpt.get();

                if (task.getAttachmentContent() != null && task.getAttachmentFilename() != null) {
                    // Base64 문자열을 바이트 배열로 디코딩
                    byte[] fileBytes = java.util.Base64.getDecoder().decode(task.getAttachmentContent());

                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.parseMediaType(
                            task.getAttachmentContentType() != null ?
                                    task.getAttachmentContentType() : "application/octet-stream"));
                    headers.setContentDispositionFormData("attachment", task.getAttachmentFilename());
                    headers.setContentLength(fileBytes.length);

                    return ResponseEntity.ok()
                            .headers(headers)
                            .body(fileBytes);
                } else {
                    return ResponseEntity.notFound().build();
                }
            } else {
                return ResponseEntity.notFound().build();
            }

        } catch (Exception e) {
            System.err.println("Error in downloadAttachment: " + e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    // ✅ 작업 삭제
    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(
            @RequestHeader("Authorization") String token,
            @PathVariable Integer taskId) {
        try {
            if (!isValidToken(token)) {
                return ResponseEntity.status(401).build();
            }

            boolean deleted = taskService.deleteTask(taskId);
            if (deleted) {
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.notFound().build();
            }

        } catch (Exception e) {
            System.err.println("Error in deleteTask: " + e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    // ✅ 작업 상태 업데이트
    @PatchMapping("/{taskId}/status")
    public ResponseEntity<Task> updateTaskStatus(
            @RequestHeader("Authorization") String token,
            @PathVariable Integer taskId,
            @RequestParam TaskStatus status) {
        try {
            if (!isValidToken(token)) {
                return ResponseEntity.status(401).build();
            }

            Task task = taskService.updateTaskStatus(taskId, status);
            return ResponseEntity.ok(task);

        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            }
            System.err.println("Error in updateTaskStatus: " + e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    // 🆕 ===== TaskAssignment 관련 API =====

    // 작업에 사용자 할당
    @PostMapping("/{taskId}/assign")
    public ResponseEntity<TaskAssignment> assignUserToTask(
            @RequestHeader("Authorization") String token,
            @PathVariable Integer taskId,
            @RequestParam Long userId,
            @RequestParam AssignmentRole role,
            @RequestParam Long assignedBy) {
        try {
            if (!isValidToken(token)) {
                return ResponseEntity.status(401).build();
            }

            TaskAssignment assignment = taskService.assignUserToTask(taskId, userId, role, assignedBy);
            return ResponseEntity.status(HttpStatus.CREATED).body(assignment);

        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            } else if (e.getMessage().contains("already assigned")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
            System.err.println("Error in assignUserToTask: " + e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    // 작업에서 사용자 할당 해제
    @DeleteMapping("/{taskId}/assign/{userId}")
    public ResponseEntity<Void> unassignUserFromTask(
            @RequestHeader("Authorization") String token,
            @PathVariable Integer taskId,
            @PathVariable Long userId) {
        try {
            if (!isValidToken(token)) {
                return ResponseEntity.status(401).build();
            }

            boolean unassigned = taskService.unassignUserFromTask(taskId, userId);
            if (unassigned) {
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.notFound().build();
            }

        } catch (Exception e) {
            System.err.println("Error in unassignUserFromTask: " + e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    // 내 업무 조회 (역할별 통합)
    @GetMapping("/my-tasks/{userId}")
    public ResponseEntity<List<Task>> getMyTasks(
            @RequestHeader("Authorization") String token,
            @PathVariable Long userId,
            @RequestParam(defaultValue = "assigned") String role) { // assigned, review
        try {
            if (!isValidToken(token)) {
                return ResponseEntity.status(401).build();
            }

            List<Task> tasks;
            if ("review".equals(role)) {
                tasks = taskService.getMyReviewTasks(userId);
            } else {
                tasks = taskService.getMyAssignedTasks(userId);
            }
            return ResponseEntity.ok(tasks);

        } catch (Exception e) {
            System.err.println("Error in getMyTasks: " + e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    // 특정 작업의 할당자들 조회
    @GetMapping("/{taskId}/assignments")
    public ResponseEntity<List<TaskAssignment>> getTaskAssignments(
            @RequestHeader("Authorization") String token,
            @PathVariable Integer taskId) {
        try {
            if (!isValidToken(token)) {
                return ResponseEntity.status(401).build();
            }

            List<TaskAssignment> assignments = taskService.getTaskAssignments(taskId);
            return ResponseEntity.ok(assignments);

        } catch (Exception e) {
            System.err.println("Error in getTaskAssignments: " + e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    // 🆕 ===== 실용적인 비즈니스 API =====

    // 마감일 관련 작업들 통합
    @GetMapping("/deadline")
    public ResponseEntity<List<Task>> getTasksByDeadline(
            @RequestHeader("Authorization") String token,
            @RequestParam(defaultValue = "upcoming") String type, // upcoming, today, overdue
            @RequestParam(defaultValue = "7") int days) {
        try {
            if (!isValidToken(token)) {
                return ResponseEntity.status(401).build();
            }

            List<Task> tasks;
            switch (type) {
                case "today":
                    tasks = taskService.getTasksDueToday();
                    break;
                case "overdue":
                    tasks = taskService.getOverdueTasks();
                    break;
                default: // upcoming
                    tasks = taskService.getTasksWithUpcomingDeadline(days);
                    break;
            }
            return ResponseEntity.ok(tasks);

        } catch (Exception e) {
            System.err.println("Error in getTasksByDeadline: " + e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    // 작업 통계
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getTaskStatistics(
            @RequestHeader("Authorization") String token,
            @RequestParam(required = false) Long userId) {
        try {
            if (!isValidToken(token)) {
                return ResponseEntity.status(401).build();
            }

            Map<String, Object> statistics = taskService.getTaskStatistics(userId);
            return ResponseEntity.ok(statistics);

        } catch (Exception e) {
            System.err.println("Error in getTaskStatistics: " + e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }
}
