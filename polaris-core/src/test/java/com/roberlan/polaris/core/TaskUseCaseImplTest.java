package com.roberlan.polaris.core;

import com.roberlan.polaris.core.domain.exception.ResourceNotFoundException;
import com.roberlan.polaris.core.domain.model.Task;
import com.roberlan.polaris.core.domain.model.TaskStatus;
import com.roberlan.polaris.core.usecase.impl.TaskUseCaseImpl;
import com.roberlan.polaris.core.usecase.ports.out.TaskRepositoryPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskUseCaseImplTest {
    
    @Mock private TaskRepositoryPort taskRepository;
    
    @Mock private RabbitTemplate rabbitTemplate;
    
    @InjectMocks private TaskUseCaseImpl taskUseCase;
    
    @Test
    @DisplayName("Criar uma tarefa com mensagem de sucesso")
    void shouldCreateTaskSuccessfully() {
        Task inputTask = new Task("Aprender Testes", "Usar Mockito", "roberlan@polaris.com");
        
        when(taskRepository.save(any(Task.class))).thenAnswer(i -> i.getArgument(0));
        
        Task createdTask = taskUseCase.createTask(inputTask);
        
        assertNotNull(createdTask.getId());
        assertEquals("Aprender Testes", createdTask.getTitle());
        assertEquals("roberlan@polaris.com", createdTask.getAssignedTo());
        assertEquals(TaskStatus.PLANNED, createdTask.getStatus());
        
        verify(taskRepository, times(1)).save(any(Task.class));
        
        verify(rabbitTemplate, times(1)).convertAndSend(eq("polaris.task.events"), any(Task.class));
    }
    
    @Test
    @DisplayName("Buscar tarefa pelo ID")
    void shouldGetTaskById() {
        UUID id = UUID.randomUUID();
        Task mockTask = new Task("Busca", "Teste", "anakin@polaris.com");
        mockTask.setId(id);
        
        when(taskRepository.findById(id)).thenReturn(Optional.of(mockTask));
        
        Task found = taskUseCase.getTaskById(id);
        
        assertEquals(id, found.getId());
        assertEquals("anakin@polaris.com", found.getAssignedTo());
    }
    
    @Test
    @DisplayName("Lançar erro se ID não existir")
    void shouldThrowExceptionWhenNotFound() {
        UUID id = UUID.randomUUID();
        when(taskRepository.findById(id)).thenReturn(Optional.empty());
        
        assertThrows(ResourceNotFoundException.class, () -> taskUseCase.getTaskById(id));
    }
    
    @Test
    @DisplayName("Deletar uma tarefa existente")
    void shouldDeleteTask() {
        UUID id = UUID.randomUUID();
        Task task = new Task();
        when(taskRepository.findById(id)).thenReturn(Optional.of(task));
        
        taskUseCase.deleteTask(id);
        
        verify(taskRepository, times(1)).delete(id);
    }
}