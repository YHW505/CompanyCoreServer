package com.example.companycoreserver.controller;

import com.example.companycoreserver.entity.Task;
import com.example.companycoreserver.entity.Enum.TaskStatus;
import com.example.companycoreserver.entity.Enum.TaskType;
import com.example.companycoreserver.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "*") // CORS 설정
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

    // 🔍 특정 사용자가 할당받은 작업 조회
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

    // 🔍 특정 사용자가 할당한 작업 조회
    @GetMapping("/assigned-by/{userId}")
    public ResponseEntity<List<Task>> getTasksByAssignedBy(
            @RequestHeader("Authorization") String token,
            @PathVariable Long userId) {
        try {
            if (!isValidToken(token)) {
                return ResponseEntity.status(401).build();
            }

            List<Task> tasks = taskService.getTasksByAssignedBy(userId);
            return ResponseEntity.ok(tasks);

        } catch (Exception e) {
            System.err.println("Error in getTasksByAssignedBy: " + e.getMessage());
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

    // 🔍 특정 사용자의 특정 상태 작업 조회
    @GetMapping("/assigned-to/{userId}/status/{status}")
    public ResponseEntity<List<Task>> getTasksByAssignedToAndStatus(
            @RequestHeader("Authorization") String token,
            @PathVariable Long userId,
            @PathVariable TaskStatus status) {
        try {
            if (!isValidToken(token)) {
                return ResponseEntity.status(401).build();
            }

            List<Task> tasks = taskService.getTasksByAssignedToAndStatus(userId, status);
            return ResponseEntity.ok(tasks);

        } catch (Exception e) {
            System.err.println("Error in getTasksByAssignedToAndStatus: " + e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    // ✅ 특정 사용자의 특정 타입 작업 조회
    @GetMapping("/assigned-to/{userId}/type/{taskType}")
    public ResponseEntity<List<Task>> getTasksByAssignedToAndType(
            @RequestHeader("Authorization") String token,
            @PathVariable Long userId,
            @PathVariable TaskType taskType) {
        try {
            if (!isValidToken(token)) {
                return ResponseEntity.status(401).build();
            }

            List<Task> tasks = taskService.getTasksByAssignedToAndType(userId, taskType);
            return ResponseEntity.ok(tasks);

        } catch (Exception e) {
            System.err.println("Error in getTasksByAssignedToAndType: " + e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    // 🆕 특정 사용자의 특정 타입 작업 조회 (페이지네이션 포함)
    @GetMapping("/assigned-to/{userId}/type/{taskType}/page")
    public ResponseEntity<?> getTasksByAssignedToAndTypeWithPagination(
            @RequestHeader("Authorization") String token,
            @PathVariable Long userId,
            @PathVariable TaskType taskType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        try {
            if (!isValidToken(token)) {
                return ResponseEntity.status(401).build();
            }

            var result = taskService.getTasksByAssignedToAndTypeWithPagination(userId, taskType, page, size, sortBy, sortDir);
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            System.err.println("Error in getTasksByAssignedToAndTypeWithPagination: " + e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    // 🔍 날짜 범위로 작업 조회
    @GetMapping("/date-range")
    public ResponseEntity<List<Task>> getTasksByDateRange(
            @RequestHeader("Authorization") String token,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            if (!isValidToken(token)) {
                return ResponseEntity.status(401).build();
            }

            List<Task> tasks = taskService.getTasksByDateRange(startDate, endDate);
            return ResponseEntity.ok(tasks);

        } catch (Exception e) {
            System.err.println("Error in getTasksByDateRange: " + e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    // ✅ 특정 사용자의 날짜 범위 작업 조회
    @GetMapping("/assigned-to/{userId}/date-range")
    public ResponseEntity<List<Task>> getTasksByAssignedToAndDateRange(
            @RequestHeader("Authorization") String token,
            @PathVariable Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            if (!isValidToken(token)) {
                return ResponseEntity.status(401).build();
            }

            List<Task> tasks = taskService.getTasksByAssignedToAndDateRange(userId, startDate, endDate);
            return ResponseEntity.ok(tasks);

        } catch (Exception e) {
            System.err.println("Error in getTasksByAssignedToAndDateRange: " + e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    // 🔍 제목으로 검색
    @GetMapping("/search")
    public ResponseEntity<List<Task>> searchTasksByTitle(
            @RequestHeader("Authorization") String token,
            @RequestParam String keyword) {
        try {
            if (!isValidToken(token)) {
                return ResponseEntity.status(401).build();
            }

            List<Task> tasks = taskService.searchTasksByTitle(keyword);
            return ResponseEntity.ok(tasks);

        } catch (Exception e) {
            System.err.println("Error in searchTasksByTitle: " + e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    // 🔍 복합 조건 검색
    @GetMapping("/filter")
    public ResponseEntity<List<Task>> getTasksByMultipleConditions(
            @RequestHeader("Authorization") String token,
            @RequestParam(required = false) Long assignedTo,
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false) TaskType taskType) {
        try {
            if (!isValidToken(token)) {
                return ResponseEntity.status(401).build();
            }

            List<Task> tasks = taskService.getTasksByMultipleConditions(assignedTo, status, taskType);
            return ResponseEntity.ok(tasks);

        } catch (Exception e) {
            System.err.println("Error in getTasksByMultipleConditions: " + e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    // ✅ 작업 생성 (첨부파일 포함)
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Task> createTaskWithAttachment(
            @RequestHeader("Authorization") String token,
            @RequestPart("task") Task task,
            @RequestPart(value = "attachment", required = false) MultipartFile attachmentFile) {
        try {
            if (!isValidToken(token)) {
                return ResponseEntity.status(401).build();
            }

            Task createdTask = taskService.createTask(task, attachmentFile);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);

        } catch (Exception e) {
            System.err.println("Error in createTaskWithAttachment: " + e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    // ✅ 작업 생성 (첨부파일 없이)
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
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

    // ✅ 첨부파일 업데이트
    @PutMapping("/{taskId}/attachment")
    public ResponseEntity<Task> updateTaskAttachment(
            @RequestHeader("Authorization") String token,
            @PathVariable Integer taskId,
            @RequestPart(value = "attachment", required = false) MultipartFile attachmentFile) {
        try {
            if (!isValidToken(token)) {
                return ResponseEntity.status(401).build();
            }

            Task task = taskService.updateTaskAttachment(taskId, attachmentFile);
            return ResponseEntity.ok(task);

        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            }
            System.err.println("Error in updateTaskAttachment: " + e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    // ✅ 첨부파일 제거
    @DeleteMapping("/{taskId}/attachment")
    public ResponseEntity<Task> removeTaskAttachment(
            @RequestHeader("Authorization") String token,
            @PathVariable Integer taskId) {
        try {
            if (!isValidToken(token)) {
                return ResponseEntity.status(401).build();
            }

            Task task = taskService.removeTaskAttachment(taskId);
            return ResponseEntity.ok(task);

        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            }
            System.err.println("Error in removeTaskAttachment: " + e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    // ✅ 첨부파일 다운로드
    @GetMapping("/{taskId}/attachment/download")
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
}
