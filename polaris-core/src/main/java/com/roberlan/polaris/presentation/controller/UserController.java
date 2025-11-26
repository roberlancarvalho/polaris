package com.roberlan.polaris.presentation.controller;

import com.roberlan.polaris.core.domain.model.User;
import com.roberlan.polaris.infrastructure.persistence.repository.UserRedisRepository;
import com.roberlan.polaris.presentation.dto.response.UserSummary;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/auth")
public class UserController {
    
    private final UserRedisRepository userRepository;
    
    public UserController(UserRedisRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    @GetMapping("/users")
    public ResponseEntity<List<UserSummary>> listUsers() {
        List<User> users = userRepository.findAll();
        
        List<UserSummary> summaryList = users.stream().map(u -> new UserSummary(u.getName(), u.getEmail())).toList();
        
        return ResponseEntity.ok(summaryList);
    }
}