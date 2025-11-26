package com.roberlan.polaris.core.usecase.impl;

import com.roberlan.polaris.core.domain.exception.ResourceNotFoundException;
import com.roberlan.polaris.core.domain.model.Task;
import com.roberlan.polaris.core.domain.model.TaskStatus;
import com.roberlan.polaris.core.usecase.ports.in.TaskInputPort;
import com.roberlan.polaris.core.usecase.ports.out.TaskRepositoryPort;
import com.roberlan.polaris.infrastructure.config.RabbitMQConfig;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class TaskUseCaseImpl implements TaskInputPort {
    
    private final TaskRepositoryPort taskRepository;
    private final RabbitTemplate rabbitTemplate;
    
    public TaskUseCaseImpl(TaskRepositoryPort taskRepository, RabbitTemplate rabbitTemplate) {
        this.taskRepository = taskRepository;
        this.rabbitTemplate = rabbitTemplate;
    }
    
    @Override
    public Task createTask(Task task) {
        Task savedTask = taskRepository.save(task);
        
        try {
            rabbitTemplate.convertAndSend(RabbitMQConfig.QUEUE_TASK_EVENTS, savedTask);
            System.out.println("[RabbitMQ] Evento de criação publicado para Task ID: " + savedTask.getId());
        } catch (Exception e) {
            System.err.println("[RabbitMQ] Falha ao publicar evento: " + e.getMessage());
        }
        
        return savedTask;
    }
    
    @Override
    public List<Task> listAllTasks() {
        return taskRepository.findAll();
    }
    
    @Override
    public Task getTaskById(UUID id) {
        return taskRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Task not found: " + id));
    }
    
    @Override
    public Task updateTask(UUID id, String title, String description, String assignedTo, String statusString) {
        Task task = getTaskById(id);
        task.updateContent(title, description);
        task.setAssignedTo(assignedTo);
        
        if (statusString != null && !statusString.isBlank()) {
            try {
                TaskStatus newStatus = TaskStatus.valueOf(statusString.toUpperCase());
                if (task.getStatus() != newStatus) {
                    task.changeStatus(newStatus);
                }
            } catch (IllegalArgumentException ignored) {
            }
        }
        
        return taskRepository.save(task);
    }
    
    @Override
    public Task changeTaskStatus(UUID id, String statusString) {
        Task task = getTaskById(id);
        try {
            TaskStatus newStatus = TaskStatus.valueOf(statusString.toUpperCase());
            task.changeStatus(newStatus);
            return taskRepository.save(task);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status: " + statusString);
        }
    }
    
    @Override
    public void deleteTask(UUID id) {
        if (taskRepository.findById(id).isEmpty()) {
            throw new ResourceNotFoundException("Cannot delete. Task not found: " + id);
        }
        taskRepository.delete(id);
    }
}