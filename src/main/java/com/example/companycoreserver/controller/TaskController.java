package com.example.companycoreserver.controller;

import com.example.companycoreserver.entity.Task;
import com.example.companycoreserver.entity.Enum.TaskStatus;
import com.example.companycoreserver.entity.Enum.TaskType;
import com.example.companycoreserver.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/tasks")
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
}
