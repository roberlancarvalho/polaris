package com.roberlan.polaris.infrastructure.persistence.repository;

import com.roberlan.polaris.core.domain.model.User;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public class UserRedisRepository {
    
    private static final String KEY_PREFIX = "user:";
    private final RedisTemplate<String, Object> redisTemplate;
    
    public UserRedisRepository(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
    
    public void save(User user) {
        redisTemplate.opsForValue().set(KEY_PREFIX + user.getEmail(), user);
    }
    
    public Optional<User> findByEmail(String email) {
        User user = (User) redisTemplate.opsForValue().get(KEY_PREFIX + email);
        return Optional.ofNullable(user);
    }
    
    public Set<String> findAllEmails() {
        return redisTemplate.keys("user:*");
    }
    
    public java.util.List<User> findAll() {
        Set<String> keys = redisTemplate.keys("user:*");
        if (keys == null || keys.isEmpty()) return java.util.List.of();
        
        return redisTemplate.opsForValue().multiGet(keys).stream().map(obj -> (User) obj).collect(
                java.util.stream.Collectors.toList());
    }
}