package com.example.companycoreserver.service;

import com.example.companycoreserver.entity.Task;
import com.example.companycoreserver.entity.Enum.TaskStatus;
import com.example.companycoreserver.entity.Enum.TaskType;
import com.example.companycoreserver.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    // 모든 작업 조회
    public List<Task> getAllTasks() {
        try {
            List<Task> tasks = taskRepository.findAll();
            System.out.println("Found " + tasks.size() + " tasks");
            return tasks;
        } catch (Exception e) {
            System.err.println("Error fetching all tasks: " + e.getMessage());
            throw new RuntimeException("Failed to fetch tasks", e);
        }
    }

    // ID로 작업 조회
    public Optional<Task> getTaskById(Integer taskId) {
        try {
            return taskRepository.findById(taskId);
        } catch (Exception e) {
            System.err.println("Error fetching task by id: " + e.getMessage());
            throw new RuntimeException("Failed to fetch task", e);
        }
    }

    // 특정 사용자가 할당받은 작업 조회
    public List<Task> getTasksByAssignedTo(Long assignedTo) {
        try {
            List<Task> tasks = taskRepository.findByAssignedTo(assignedTo);
            System.out.println("Found " + tasks.size() + " tasks assigned to user: " + assignedTo);
            return tasks;
        } catch (Exception e) {
            System.err.println("Error fetching tasks by assignedTo: " + e.getMessage());
            throw new RuntimeException("Failed to fetch tasks", e);
        }
    }

    // 특정 사용자가 할당한 작업 조회
    public List<Task> getTasksByAssignedBy(Long assignedBy) {
        try {
            List<Task> tasks = taskRepository.findByAssignedBy(assignedBy);
            System.out.println("Found " + tasks.size() + " tasks assigned by user: " + assignedBy);
            return tasks;
        } catch (Exception e) {
            System.err.println("Error fetching tasks by assignedBy: " + e.getMessage());
            throw new RuntimeException("Failed to fetch tasks", e);
        }
    }

    // 상태별 작업 조회
    public List<Task> getTasksByStatus(TaskStatus status) {
        try {
            List<Task> tasks = taskRepository.findByStatus(status);
            System.out.println("Found " + tasks.size() + " tasks with status: " + status);
            return tasks;
        } catch (Exception e) {
            System.err.println("Error fetching tasks by status: " + e.getMessage());
            throw new RuntimeException("Failed to fetch tasks", e);
        }
    }

    // 작업 타입별 조회
    public List<Task> getTasksByType(TaskType taskType) {
        try {
            List<Task> tasks = taskRepository.findByTaskType(taskType);
            System.out.println("Found " + tasks.size() + " tasks with type: " + taskType);
            return tasks;
        } catch (Exception e) {
            System.err.println("Error fetching tasks by type: " + e.getMessage());
            throw new RuntimeException("Failed to fetch tasks", e);
        }
    }

    // 특정 사용자의 특정 상태 작업 조회
    public List<Task> getTasksByAssignedToAndStatus(Long assignedTo, TaskStatus status) {
        try {
            return taskRepository.findByAssignedToAndStatus(assignedTo, status);
        } catch (Exception e) {
            System.err.println("Error fetching tasks by assignedTo and status: " + e.getMessage());
            throw new RuntimeException("Failed to fetch tasks", e);
        }
    }

    // 날짜 범위로 작업 조회
    public List<Task> getTasksByDateRange(LocalDate startDate, LocalDate endDate) {
        try {
            return taskRepository.findByDateRange(startDate, endDate);
        } catch (Exception e) {
            System.err.println("Error fetching tasks by date range: " + e.getMessage());
            throw new RuntimeException("Failed to fetch tasks", e);
        }
    }

    // 제목으로 검색
    public List<Task> searchTasksByTitle(String keyword) {
        try {
            return taskRepository.findByTitleContaining(keyword);
        } catch (Exception e) {
            System.err.println("Error searching tasks by title: " + e.getMessage());
            throw new RuntimeException("Failed to search tasks", e);
        }
    }

    // 복합 조건 검색
    public List<Task> getTasksByMultipleConditions(Long assignedTo, TaskStatus status, TaskType taskType) {
        try {
            return taskRepository.findByMultipleConditions(assignedTo, status, taskType);
        } catch (Exception e) {
            System.err.println("Error fetching tasks by multiple conditions: " + e.getMessage());
            throw new RuntimeException("Failed to fetch tasks", e);
        }
    }
}
