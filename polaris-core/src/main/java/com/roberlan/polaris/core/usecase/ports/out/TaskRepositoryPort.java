package com.roberlan.polaris.core.usecase.ports.out;

import com.roberlan.polaris.core.domain.model.Task;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TaskRepositoryPort {
    
    Task save(Task task);
    
    Optional<Task> findById(UUID id);
    
    List<Task> findAll();
    
    void delete(UUID id);
}
