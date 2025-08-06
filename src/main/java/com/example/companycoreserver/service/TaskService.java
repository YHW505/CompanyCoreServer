package com.example.companycoreserver.service;

import com.example.companycoreserver.entity.Task;
import com.example.companycoreserver.entity.Enum.TaskStatus;
import com.example.companycoreserver.entity.Enum.TaskType;
import com.example.companycoreserver.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

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

    // 특정 사용자의 특정 타입 작업 조회
    public List<Task> getTasksByAssignedToAndType(Long assignedTo, TaskType taskType) {
        try {
            return taskRepository.findByAssignedToAndTaskType(assignedTo, taskType);
        } catch (Exception e) {
            System.err.println("Error fetching tasks by assignedTo and taskType: " + e.getMessage());
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

    // 특정 사용자의 날짜 범위 작업 조회
    public List<Task> getTasksByAssignedToAndDateRange(Long assignedTo, LocalDate startDate, LocalDate endDate) {
        try {
            return taskRepository.findByAssignedToAndDateRange(assignedTo, startDate, endDate);
        } catch (Exception e) {
            System.err.println("Error fetching tasks by assignedTo and date range: " + e.getMessage());
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

    // ✅ 작업 생성 (첨부파일 포함)
    public Task createTask(Task task, MultipartFile attachmentFile) {
        try {
            // 첨부파일이 있는 경우 처리
            if (attachmentFile != null && !attachmentFile.isEmpty()) {
                task.updateAttachment(
                        attachmentFile.getOriginalFilename(),
                        attachmentFile.getContentType(),
                        attachmentFile.getBytes()
                );
                // 파일 크기 설정
                task.setAttachmentSize(attachmentFile.getSize());
            }

            Task savedTask = taskRepository.save(task);
            System.out.println("Task created successfully with ID: " + savedTask.getTaskId());
            return savedTask;
        } catch (IOException e) {
            System.err.println("Error processing attachment file: " + e.getMessage());
            throw new RuntimeException("Failed to process attachment file", e);
        } catch (Exception e) {
            System.err.println("Error creating task: " + e.getMessage());
            throw new RuntimeException("Failed to create task", e);
        }
    }

    // ✅ 작업 생성 (첨부파일 없이)
    public Task createTask(Task task) {
        try {
            Task savedTask = taskRepository.save(task);
            System.out.println("Task created successfully with ID: " + savedTask.getTaskId());
            return savedTask;
        } catch (Exception e) {
            System.err.println("Error creating task: " + e.getMessage());
            throw new RuntimeException("Failed to create task", e);
        }
    }

    // ✅ 작업 업데이트
    public Task updateTask(Integer taskId, Task updatedTask) {
        try {
            Optional<Task> existingTaskOpt = taskRepository.findById(taskId);
            if (existingTaskOpt.isPresent()) {
                Task existingTask = existingTaskOpt.get();

                // 기본 필드 업데이트
                if (updatedTask.getTitle() != null) {
                    existingTask.setTitle(updatedTask.getTitle());
                }
                if (updatedTask.getDescription() != null) {
                    existingTask.setDescription(updatedTask.getDescription());
                }
                if (updatedTask.getStatus() != null) {
                    existingTask.setStatus(updatedTask.getStatus());
                }
                if (updatedTask.getTaskType() != null) {
                    existingTask.setTaskType(updatedTask.getTaskType());
                }
                if (updatedTask.getStartDate() != null) {
                    existingTask.setStartDate(updatedTask.getStartDate());
                }
                if (updatedTask.getEndDate() != null) {
                    existingTask.setEndDate(updatedTask.getEndDate());
                }
                if (updatedTask.getAssignedTo() != null) {
                    existingTask.setAssignedTo(updatedTask.getAssignedTo());
                }

                Task savedTask = taskRepository.save(existingTask);
                System.out.println("Task updated successfully: " + taskId);
                return savedTask;
            } else {
                throw new RuntimeException("Task not found with id: " + taskId);
            }
        } catch (Exception e) {
            System.err.println("Error updating task: " + e.getMessage());
            throw new RuntimeException("Failed to update task", e);
        }
    }

    // ✅ 첨부파일 업데이트
    public Task updateTaskAttachment(Integer taskId, MultipartFile attachmentFile) {
        try {
            Optional<Task> taskOpt = taskRepository.findById(taskId);
            if (taskOpt.isPresent()) {
                Task task = taskOpt.get();

                if (attachmentFile != null && !attachmentFile.isEmpty()) {
                    task.updateAttachment(
                            attachmentFile.getOriginalFilename(),
                            attachmentFile.getContentType(),
                            attachmentFile.getBytes()
                    );
                    task.setAttachmentSize(attachmentFile.getSize());
                } else {
                    task.removeAttachment();
                }

                Task savedTask = taskRepository.save(task);
                System.out.println("Task attachment updated successfully: " + taskId);
                return savedTask;
            } else {
                throw new RuntimeException("Task not found with id: " + taskId);
            }
        } catch (IOException e) {
            System.err.println("Error processing attachment file: " + e.getMessage());
            throw new RuntimeException("Failed to process attachment file", e);
        } catch (Exception e) {
            System.err.println("Error updating task attachment: " + e.getMessage());
            throw new RuntimeException("Failed to update task attachment", e);
        }
    }

    // ✅ 첨부파일 제거
    public Task removeTaskAttachment(Integer taskId) {
        try {
            Optional<Task> taskOpt = taskRepository.findById(taskId);
            if (taskOpt.isPresent()) {
                Task task = taskOpt.get();
                task.removeAttachment();

                Task savedTask = taskRepository.save(task);
                System.out.println("Task attachment removed successfully: " + taskId);
                return savedTask;
            } else {
                throw new RuntimeException("Task not found with id: " + taskId);
            }
        } catch (Exception e) {
            System.err.println("Error removing task attachment: " + e.getMessage());
            throw new RuntimeException("Failed to remove task attachment", e);
        }
    }

    // ✅ 작업 삭제
    public boolean deleteTask(Integer taskId) {
        try {
            if (taskRepository.existsById(taskId)) {
                taskRepository.deleteById(taskId);
                System.out.println("Task deleted successfully: " + taskId);
                return true;
            } else {
                System.out.println("Task not found for deletion: " + taskId);
                return false;
            }
        } catch (Exception e) {
            System.err.println("Error deleting task: " + e.getMessage());
            throw new RuntimeException("Failed to delete task", e);
        }
    }

    // ✅ 작업 상태 업데이트
    public Task updateTaskStatus(Integer taskId, TaskStatus newStatus) {
        try {
            Optional<Task> taskOpt = taskRepository.findById(taskId);
            if (taskOpt.isPresent()) {
                Task task = taskOpt.get();
                task.setStatus(newStatus);

                Task savedTask = taskRepository.save(task);
                System.out.println("Task status updated successfully: " + taskId + " -> " + newStatus);
                return savedTask;
            } else {
                throw new RuntimeException("Task not found with id: " + taskId);
            }
        } catch (Exception e) {
            System.err.println("Error updating task status: " + e.getMessage());
            throw new RuntimeException("Failed to update task status", e);
        }
    }

    // 🆕 특정 사용자의 특정 타입 작업 조회 (페이지네이션 포함)
    public Map<String, Object> getTasksByAssignedToAndTypeWithPagination(Long assignedTo, TaskType taskType, int page, int size, String sortBy, String sortDir) {
        try {
            Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);
            
            Page<Task> taskPage = taskRepository.findByAssignedToAndTaskType(assignedTo, taskType, pageable);
            
            Map<String, Object> response = new HashMap<>();
            response.put("content", taskPage.getContent());
            response.put("totalElements", taskPage.getTotalElements());
            response.put("totalPages", taskPage.getTotalPages());
            response.put("currentPage", page);
            response.put("size", size);
            
            return response;
        } catch (Exception e) {
            System.err.println("Error fetching tasks by assignedTo and taskType with pagination: " + e.getMessage());
            throw new RuntimeException("Failed to fetch tasks with pagination", e);
        }
    }
}
