package com.example.companycoreserver.controller;

import com.example.companycoreserver.entity.Task;
import com.example.companycoreserver.entity.Enum.TaskStatus;
import com.example.companycoreserver.entity.Enum.TaskType;
import com.example.companycoreserver.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "*")
public class TaskController {

    @Autowired
    private TaskService taskService;

    // 모든 작업 조회
    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks() {
        try {
            List<Task> tasks = taskService.getAllTasks();
            return ResponseEntity.ok(tasks);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ✅ ID로 작업 조회 (Long 타입)
    @GetMapping("/{taskId}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long taskId) {
        try {
            Optional<Task> task = taskService.getTaskById(taskId);
            return task.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ✅ 특정 사용자에게 할당된 작업 조회
    @GetMapping("/assigned-to/{userId}")
    public ResponseEntity<List<Task>> getTasksByAssignedTo(@PathVariable Long userId) {
        try {
            List<Task> tasks = taskService.getTasksByAssignedTo(userId);
            return ResponseEntity.ok(tasks);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ✅ 특정 사용자가 생성한 작업 조회
    @GetMapping("/created-by/{assignedBy}")
    public ResponseEntity<List<Task>> getTasksByCreatedBy(@PathVariable Long assignedBy) {
        try {
            List<Task> tasks = taskService.getTasksByCreatedBy(assignedBy);
            return ResponseEntity.ok(tasks);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 상태별 작업 조회
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Task>> getTasksByStatus(@PathVariable TaskStatus status) {
        try {
            List<Task> tasks = taskService.getTasksByStatus(status);
            return ResponseEntity.ok(tasks);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 작업 타입별 조회
    @GetMapping("/type/{taskType}")
    public ResponseEntity<List<Task>> getTasksByType(@PathVariable TaskType taskType) {
        try {
            List<Task> tasks = taskService.getTasksByType(taskType);
            return ResponseEntity.ok(tasks);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ✅ 특정 사용자가 생성한 특정 상태 작업 조회
    @GetMapping("/created-by/{assignedBy}/status/{status}")
    public ResponseEntity<List<Task>> getTasksByCreatedByAndStatus(@PathVariable Long assignedBy, @PathVariable TaskStatus status) {
        try {
            List<Task> tasks = taskService.getTasksByCreatedByAndStatus(assignedBy, status);
            return ResponseEntity.ok(tasks);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ✅ 특정 사용자가 생성한 특정 타입 작업 조회
    @GetMapping("/created-by/{assignedBy}/type/{taskType}")
    public ResponseEntity<List<Task>> getTasksByCreatedByAndType(@PathVariable Long assignedBy, @PathVariable TaskType taskType) {
        try {
            List<Task> tasks = taskService.getTasksByCreatedByAndType(assignedBy, taskType);
            return ResponseEntity.ok(tasks);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ✅ 날짜 범위로 작업 조회 (시작일 기준)
    @GetMapping("/date-range")
    public ResponseEntity<List<Task>> getTasksByDateRange(@RequestParam LocalDate startDate, @RequestParam LocalDate endDate) {
        try {
            List<Task> tasks = taskService.getTasksByDateRange(startDate, endDate);
            return ResponseEntity.ok(tasks);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 마감일 범위로 작업 조회
    @GetMapping("/end-date-range")
    public ResponseEntity<List<Task>> getTasksByEndDateRange(@RequestParam LocalDate startDate, @RequestParam LocalDate endDate) {
        try {
            List<Task> tasks = taskService.getTasksByEndDateRange(startDate, endDate);
            return ResponseEntity.ok(tasks);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 제목으로 검색
    @GetMapping("/search/title")
    public ResponseEntity<List<Task>> searchTasksByTitle(@RequestParam String keyword) {
        try {
            List<Task> tasks = taskService.searchTasksByTitle(keyword);
            return ResponseEntity.ok(tasks);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 제목 또는 설명으로 검색
    @GetMapping("/search")
    public ResponseEntity<List<Task>> searchTasksByTitleOrDescription(@RequestParam String keyword) {
        try {
            List<Task> tasks = taskService.searchTasksByTitleOrDescription(keyword);
            return ResponseEntity.ok(tasks);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ✅ 복합 조건 검색
    @GetMapping("/search/multiple")
    public ResponseEntity<List<Task>> getTasksByMultipleConditions(
            @RequestParam(required = false) Long assignedBy,
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false) TaskType taskType) {
        try {
            List<Task> tasks = taskService.getTasksByMultipleConditions(assignedBy, status, taskType);
            return ResponseEntity.ok(tasks);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

//    // ✅ 작업 생성 (첨부파일 포함)
//    @PostMapping(consumes = {"multipart/form-data"})
//    public ResponseEntity<Task> createTask(
//            @RequestPart("task") Task task,
//            @RequestPart(value = "attachmentFile", required = false) MultipartFile attachmentFile) {
//        try {
//            Task createdTask = taskService.createTask(task, attachmentFile);
//            return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        }
//    }

    // ✅ 작업 생성 (첨부파일 없이)
    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody Task task) {
        try {
            Task createdTask = taskService.createTask(task);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 🆕 여러 Task 한번에 생성
    @PostMapping("/bulk")
    public ResponseEntity<List<Task>> createTasks(@RequestBody List<Task> tasks) {
        List<Task> createdTasks = taskService.createTasks(tasks);
        return ResponseEntity.ok(createdTasks);
    }

    // ✅ 작업 업데이트 (Long 타입)
    @PutMapping("/{taskId}")
    public ResponseEntity<Task> updateTask(@PathVariable Long taskId, @RequestBody Task updatedTask) {
        try {
            Task task = taskService.updateTask(taskId, updatedTask);
            return ResponseEntity.ok(task);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

//    // ✅ 첨부파일 업데이트 (Long 타입)
//    @PutMapping("/{taskId}/attachment")
//    public ResponseEntity<Task> updateTaskAttachment(@PathVariable Long taskId, @RequestParam("file") MultipartFile attachmentFile) {
//        try {
//            Task task = taskService.updateTaskAttachment(taskId, attachmentFile);
//            return ResponseEntity.ok(task);
//        } catch (RuntimeException e) {
//            if (e.getMessage().contains("not found")) {
//                return ResponseEntity.notFound().build();
//            }
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        }
//    }
//
//    // ✅ 첨부파일 제거 (Long 타입)
//    @DeleteMapping("/{taskId}/attachment")
//    public ResponseEntity<Task> removeTaskAttachment(@PathVariable Long taskId) {
//        try {
//            Task task = taskService.removeTaskAttachment(taskId);
//            return ResponseEntity.ok(task);
//        } catch (RuntimeException e) {
//            if (e.getMessage().contains("not found")) {
//                return ResponseEntity.notFound().build();
//            }
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        }
//    }

    // ✅ 작업 삭제 (Long 타입)
    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long taskId) {
        try {
            boolean deleted = taskService.deleteTask(taskId);
            return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ✅ 작업 상태 업데이트 (Long 타입)
    @PatchMapping("/{taskId}/status")
    public ResponseEntity<Task> updateTaskStatus(@PathVariable Long taskId, @RequestBody TaskStatus newStatus) {
        try {
            Task task = taskService.updateTaskStatus(taskId, newStatus);
            return ResponseEntity.ok(task);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ✅ 페이지네이션
    @GetMapping("/created-by/{assignedBy}/type/{taskType}/paginated")
    public ResponseEntity<Map<String, Object>> getTasksByCreatedByAndTypeWithPagination(
            @PathVariable Long assignedBy,
            @PathVariable TaskType taskType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        try {
            Map<String, Object> result = taskService.getTasksByCreatedByAndTypeWithPagination(assignedBy, taskType, page, size, sortBy, sortDir);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ✅ 내가 담당자로 할당받은 작업들
    @GetMapping("/my-assigned/{userId}")
    public ResponseEntity<List<Task>> getMyAssignedTasks(@PathVariable Long userId) {
        try {
            List<Task> tasks = taskService.getMyAssignedTasks(userId);
            return ResponseEntity.ok(tasks);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ✅ 마감일 임박 작업들
    @GetMapping("/upcoming-deadline")
    public ResponseEntity<List<Task>> getTasksWithUpcomingDeadline(@RequestParam(defaultValue = "7") int days) {
        try {
            List<Task> tasks = taskService.getTasksWithUpcomingDeadline(days);
            return ResponseEntity.ok(tasks);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ✅ 오늘 마감 작업들
    @GetMapping("/due-today")
    public ResponseEntity<List<Task>> getTasksDueToday() {
        try {
            List<Task> tasks = taskService.getTasksDueToday();
            return ResponseEntity.ok(tasks);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ✅ 연체된 작업들
    @GetMapping("/overdue")
    public ResponseEntity<List<Task>> getOverdueTasks() {
        try {
            List<Task> tasks = taskService.getOverdueTasks();
            return ResponseEntity.ok(tasks);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ✅ 작업 통계
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getTaskStatistics(@RequestParam(required = false) Long userId) {
        try {
            Map<String, Object> stats = taskService.getTaskStatistics(userId);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
