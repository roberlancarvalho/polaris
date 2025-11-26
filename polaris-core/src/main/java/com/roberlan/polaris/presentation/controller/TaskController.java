package com.roberlan.polaris.presentation.controller;

import com.roberlan.polaris.core.domain.model.Role;
import com.roberlan.polaris.core.domain.model.Task;
import com.roberlan.polaris.core.domain.model.User;
import com.roberlan.polaris.core.usecase.ports.in.TaskInputPort;
import com.roberlan.polaris.presentation.dto.request.TaskRequest;
import com.roberlan.polaris.presentation.dto.response.TaskResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tasks")
public class TaskController {
    
    private final TaskInputPort taskInputPort;
    
    public TaskController(TaskInputPort taskInputPort) {
        this.taskInputPort = taskInputPort;
    }
    
    @PostMapping
    public ResponseEntity<TaskResponse> create(
            @RequestBody
            @Valid
            TaskRequest request) {
        
        Task newTask = new Task(request.title(), request.description(), request.assignedTo());
        
        Task savedTask = taskInputPort.createTask(newTask);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(savedTask));
    }
    
    @GetMapping
    public ResponseEntity<List<TaskResponse>> getAll() {
        var tasks = taskInputPort.listAllTasks().stream().map(this::toResponse).toList();
        return ResponseEntity.ok(tasks);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getById(
            @PathVariable UUID id) {
        Task task = taskInputPort.getTaskById(id);
        return ResponseEntity.ok(toResponse(task));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> update(
            @PathVariable UUID id,
            @RequestBody
            @Valid
            TaskRequest request) {
        Task task = taskInputPort.updateTask(
                id, request.title(), request.description(), request.assignedTo(),
                request.status()
        );
        return ResponseEntity.ok(toResponse(task));
    }
    
    @PatchMapping("/{id}/status")
    public ResponseEntity<TaskResponse> changeStatus(
            @PathVariable UUID id,
            @RequestParam String status, Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        
        var task = taskInputPort.getTaskById(id);
        
        boolean isAdmin = currentUser.getRole() == Role.ADMIN;
        boolean isOwner = task.getAssignedTo() != null && task.getAssignedTo().equals(currentUser.getEmail());
        
        if (!isAdmin && !isOwner) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Task updatedTask = taskInputPort.changeTaskStatus(id, status);
        return ResponseEntity.ok(toResponse(updatedTask));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID id) {
        taskInputPort.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
    
    private TaskResponse toResponse(Task task) {
        return new TaskResponse(
                task.getId(), task.getTitle(), task.getDescription(), task.getAssignedTo(),
                                task.getStatus(), task.getCreatedAt(), task.getCompletedAt(), task.getUpdatedAt()
        );
    }
}