package com.roberlan.polaris.presentation.controller.ports.in;

import com.roberlan.polaris.core.domain.model.Task;

import java.util.List;
import java.util.UUID;

public interface TaskInputPort {
    Task createTask(Task task);
    
    List<Task> listAllTasks();
    
    Task getTaskById(UUID id);
    
    Task updateTask(UUID id, String title, String description, String assignedTo, String status);
    
    Task changeTaskStatus(UUID id, String status);
    
    void deleteTask(UUID id);
}