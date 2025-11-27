package com.roberlan.polaris.presentation.controller;

import com.roberlan.polaris.core.domain.model.User;
import com.roberlan.polaris.core.usecase.ports.out.FileStoragePort;
import com.roberlan.polaris.infrastructure.persistence.repository.UserRedisRepository;
import com.roberlan.polaris.presentation.dto.response.UserSummary;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/auth")
public class UserController {
    
    private final UserRedisRepository userRepository;
    private final FileStoragePort fileStoragePort;
    
    public UserController(UserRedisRepository userRepository, FileStoragePort fileStoragePort) {
        this.userRepository = userRepository;
        this.fileStoragePort = fileStoragePort;
    }
    
    @GetMapping("/users")
    public ResponseEntity<List<UserSummary>> listUsers() {
        List<User> users = userRepository.findAll();
        
        List<UserSummary> summaryList = users.stream().map(
                u -> new UserSummary(u.getName(), u.getEmail(), u.getAvatarUrl())).toList();
        
        return ResponseEntity.ok(summaryList);
    }
    
    @PostMapping(value = "/me/avatar",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserSummary> uploadAvatar(
            @RequestParam("file") MultipartFile file, Authentication authentication) throws IOException {
        
        User currentUser = (User) authentication.getPrincipal();
        
        String filename = "avatar_" + currentUser.getEmail() + "_" + file.getOriginalFilename();
        String url = fileStoragePort.upload(filename, file.getInputStream(), file.getSize(), file.getContentType());
        
        currentUser.setAvatarUrl(url);
        userRepository.save(currentUser);
        
        return ResponseEntity.ok(new UserSummary(currentUser.getName(), currentUser.getEmail(), url));
    }
}