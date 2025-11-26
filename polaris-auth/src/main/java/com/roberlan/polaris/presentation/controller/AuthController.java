package com.roberlan.polaris.presentation.controller;

import com.roberlan.polaris.core.domain.model.Role;
import com.roberlan.polaris.core.domain.model.User;
import com.roberlan.polaris.infrastructure.persistence.repository.UserRedisRepository;
import com.roberlan.polaris.infrastructure.security.JwtTokenService;
import com.roberlan.polaris.presentation.dto.response.UserSummary;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    
    private final UserRedisRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;
    
    public AuthController(UserRedisRepository userRepository, PasswordEncoder passwordEncoder,
                          JwtTokenService jwtTokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenService = jwtTokenService;
    }
    
    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestBody User user) {
        if (user.getEmail() == null || user.getName() == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email and Name are required"));
        }
        
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.getRole() == null) user.setRole(Role.USER);
        
        userRepository.save(user);
        return ResponseEntity.ok(Map.of("message", "User registered successfully!"));
    }
    
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String password = credentials.get("password");
        
        var userOpt = userRepository.findByEmail(email);
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (passwordEncoder.matches(password, user.getPassword())) {
                String token = jwtTokenService.generateToken(user);
                return ResponseEntity.ok(Map.of(
                        "token", token, "role", user.getRole(), "name", user.getName(), "email",
                        user.getEmail()
                ));
            }
        }
        
        return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
    }
    
    @GetMapping("/users")
    public ResponseEntity<List<UserSummary>> listUsers() {
        List<User> users = userRepository.findAll();
        
        List<UserSummary> summaryList = users.stream().map(u -> new UserSummary(u.getName(), u.getEmail())).toList();
        
        return ResponseEntity.ok(summaryList);
    }
}