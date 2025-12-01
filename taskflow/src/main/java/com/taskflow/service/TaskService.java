package com.taskflow.service;

import com.taskflow.dto.TaskCreateDTO;
import com.taskflow.dto.TaskDTO;
import com.taskflow.entity.Task;
import com.taskflow.entity.User;
import com.taskflow.repo.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskService {

    private final TaskRepository taskRepo;

    public TaskService(TaskRepository taskRepo) {
        this.taskRepo = taskRepo;
    }

    // Create a task
    public TaskDTO createTask(TaskCreateDTO dto, User user){
        Task task = Task.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .priority(dto.getPriority())
                .dueDate(dto.getDueDate())
                .completed(false)
                .user(user)
                .build();

        Task saved = taskRepo.save(task);

        return toDTO(saved);
    }

    // Get all tasks of a user
    public List<TaskDTO> getTasks(User user){
        return taskRepo.findByUser(user).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // Update task status
    public TaskDTO updateTaskStatus(Long taskId, boolean completed){
        Task task = taskRepo.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        task.setCompleted(completed);
        return toDTO(taskRepo.save(task));
    }

    // Delete task
    public void deleteTask(Long taskId){
        taskRepo.deleteById(taskId);
    }

    private TaskDTO toDTO(Task task){
        return TaskDTO.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .priority(task.getPriority())
                .dueDate(task.getDueDate())
                .completed(task.getCompleted())
                .build();
    }
}
