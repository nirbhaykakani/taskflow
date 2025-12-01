package com.taskflow.controller;

import com.taskflow.dto.TaskCreateDTO;
import com.taskflow.dto.TaskDTO;
import com.taskflow.entity.User;
import com.taskflow.service.TaskService;
import com.taskflow.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;
    private final UserService userService;

    public TaskController(TaskService taskService, UserService userService) {
        this.taskService = taskService;
        this.userService = userService;
    }

    // TEMP: We will replace this with JWT later
    private User getUser(String email){
        return userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // Create a task
    @PostMapping("/{email}/create")
    public ResponseEntity<TaskDTO> create(
            @PathVariable String email,
            @Valid @RequestBody TaskCreateDTO dto
    ){
        TaskDTO task = taskService.createTask(dto, getUser(email));
        return ResponseEntity.ok(task);
    }

    // Get all tasks for a user
    @GetMapping("/{email}")
    public ResponseEntity<List<TaskDTO>> getUserTasks(@PathVariable String email){
        List<TaskDTO> tasks = taskService.getTasks(getUser(email));
        return ResponseEntity.ok(tasks);
    }

    // Update task status (mark completed)
    @PutMapping("/{taskId}/status/{completed}")
    public ResponseEntity<TaskDTO> updateStatus(
            @PathVariable Long taskId,
            @PathVariable boolean completed
    ){
        TaskDTO updated = taskService.updateTaskStatus(taskId, completed);
        return ResponseEntity.ok(updated);
    }

    // Delete task
    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long taskId){
        taskService.deleteTask(taskId);
        return ResponseEntity.noContent().build();
    }

}
