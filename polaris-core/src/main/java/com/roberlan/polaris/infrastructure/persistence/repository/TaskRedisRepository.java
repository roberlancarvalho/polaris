package com.roberlan.polaris.infrastructure.persistence.repository;

import com.roberlan.polaris.core.domain.model.Task;
import com.roberlan.polaris.presentation.controller.ports.out.TaskRepositoryPort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class TaskRedisRepository implements TaskRepositoryPort {
    
    private final RedisTemplate<String, Object> redisTemplate;
    private static final String KEY_PREFIX = "task:";
    
    public TaskRedisRepository(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
    
    @Override
    public Task save(Task task) {
        String key = KEY_PREFIX + task.getId();
        redisTemplate.opsForValue().set(key, task);
        return task;
    }
    
    @Override
    public Optional<Task> findById(UUID id) {
        String key = KEY_PREFIX + id;
        Task task = (Task) redisTemplate.opsForValue().get(key);
        return Optional.ofNullable(task);
    }
    
    @Override
    public List<Task> findAll() {
        var keys = redisTemplate.keys(KEY_PREFIX + "*");
        if (keys == null || keys.isEmpty()) return List.of();
        
        return redisTemplate.opsForValue().multiGet(keys)
                .stream()
                .map(obj -> (Task) obj)
                .collect(Collectors.toList());
    }
    
    @Override
    public void delete(UUID id) {
        redisTemplate.delete(KEY_PREFIX + id);
    }
}