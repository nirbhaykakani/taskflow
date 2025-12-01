package com.taskflow.service;

import com.taskflow.dto.TaskCreateDTO;
import com.taskflow.dto.TaskDTO;
import com.taskflow.dto.TaskResponseDTO;
import com.taskflow.dto.TaskUpdateDTO;
import com.taskflow.entity.Task;
import com.taskflow.entity.User;
import com.taskflow.exception.ResourceNotFoundException;
import com.taskflow.repo.TaskRepository;
import com.taskflow.repo.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TaskService {

    private final TaskRepository taskRepo;
    private final UserRepository userRepo;

    public TaskService(TaskRepository taskRepo, UserRepository userRepo) {
        this.taskRepo = taskRepo;
        this.userRepo = userRepo;
    }

    // Create task for currently authenticated user
    public TaskResponseDTO createTask(TaskCreateDTO dto) {
        User current = getCurrentUserEntity();

        Task t = Task.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .priority(dto.getPriority())
                .dueDate(dto.getDueDate())
                .completed(false)
                .user(current)
                .build();

        Task saved = taskRepo.save(t);
        return toDTO(saved);
    }

    // Get tasks - if admin, return all, else only user's tasks
    @Transactional(readOnly = true)
    public List<TaskResponseDTO> getTasksForCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = hasRoleAdmin(auth);

        if (isAdmin) {
            return taskRepo.findAll().stream().map(this::toDTO).collect(Collectors.toList());
        } else {
            User current = getCurrentUserEntity();
            return taskRepo.findByUser(current).stream().map(this::toDTO).collect(Collectors.toList());
        }
    }

    // Get single task by id (checks access)
    @Transactional(readOnly = true)
    public TaskResponseDTO getTaskById(Long id) {
        Task t = taskRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
        enforceOwnershipOrAdmin(t);
        return toDTO(t);
    }

    // Update task (partial via TaskUpdateDTO)
    public TaskResponseDTO updateTask(Long id, TaskUpdateDTO dto) {
        Task t = taskRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
        enforceOwnershipOrAdmin(t);

        if (dto.getTitle() != null) t.setTitle(dto.getTitle());
        if (dto.getDescription() != null) t.setDescription(dto.getDescription());
        if (dto.getPriority() != null) t.setPriority(dto.getPriority());
        if (dto.getDueDate() != null) t.setDueDate(dto.getDueDate());
        if (dto.getCompleted() != null) t.setCompleted(dto.getCompleted());

        Task saved = taskRepo.save(t);
        return toDTO(saved);
    }

    // Delete task
    public void deleteTask(Long id) {
        Task t = taskRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
        enforceOwnershipOrAdmin(t);
        taskRepo.delete(t);
    }

    /* ---------------- helper methods ---------------- */

    private TaskResponseDTO toDTO(Task t) {
        return TaskResponseDTO.builder()
                .id(t.getId())
                .title(t.getTitle())
                .description(t.getDescription())
                .priority(t.getPriority())
                .dueDate(t.getDueDate())
                .completed(t.getCompleted())
                .build();
    }

    private User getCurrentUserEntity() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) {
            throw new AccessDeniedException("Unauthenticated");
        }
        String email = auth.getName(); // we set username = email in JWT UserDetails
        return userRepo.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Current user not found"));
    }

    private void enforceOwnershipOrAdmin(Task task) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) throw new AccessDeniedException("Unauthenticated");

        boolean isAdmin = hasRoleAdmin(auth);
        String currentEmail = auth.getName();

        if (isAdmin) return;

        // owner check
        if (task.getUser() == null || !currentEmail.equals(task.getUser().getEmail())) {
            throw new AccessDeniedException("You don't have permission to access this resource");
        }
    }

    private boolean hasRoleAdmin(Authentication auth) {
        return auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
}
