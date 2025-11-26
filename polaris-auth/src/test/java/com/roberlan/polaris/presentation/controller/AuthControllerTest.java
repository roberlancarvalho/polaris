package com.roberlan.polaris.presentation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.roberlan.polaris.core.domain.model.Role;
import com.roberlan.polaris.core.domain.model.User;
import com.roberlan.polaris.infrastructure.persistence.repository.UserRedisRepository;
import com.roberlan.polaris.infrastructure.security.JwtTokenService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {
    
    @Autowired private MockMvc mockMvc;
    
    @Autowired private ObjectMapper objectMapper;
    
    @MockitoBean private UserRedisRepository userRepository;
    
    @MockitoBean private PasswordEncoder passwordEncoder;
    
    @MockitoBean private JwtTokenService jwtTokenService;
    
    @Test
    @DisplayName("Registrar um usuário com sucesso")
    void shouldRegisterUser() throws Exception {
        User user = new User("Luke", "luke@polaris.com", "123", Role.USER);
        given(passwordEncoder.encode(any())).willReturn("senha_criptografada");
        
        mockMvc.perform(post("/api/v1/auth/register").contentType(MediaType.APPLICATION_JSON).content(
                objectMapper.writeValueAsString(user))).andExpect(status().isOk()).andExpect(
                jsonPath("$.message").value("User registered successfully!"));
    }
    
    @Test
    @DisplayName("Realizar login e retornar token")
    void shouldLoginSuccessfully() throws Exception {
        String email = "han@polaris.com";
        String password = "123";
        User mockUser = new User("Han Solo", email, "senha_hash", Role.ADMIN);
        
        given(userRepository.findByEmail(email)).willReturn(Optional.of(mockUser));
        given(passwordEncoder.matches(password, "senha_hash")).willReturn(true);
        given(jwtTokenService.generateToken(mockUser)).willReturn("token_falso_jwt");
        
        Map<String, String> loginBody = Map.of("email", email, "password", password);
        
        mockMvc.perform(post("/api/v1/auth/login").contentType(MediaType.APPLICATION_JSON).content(
                objectMapper.writeValueAsString(loginBody))).andExpect(status().isOk()).andExpect(
                jsonPath("$.token").value("token_falso_jwt"));
    }
    
    @Test
    @DisplayName("Retornar 401 se a senha estiver errada")
    void shouldFailLoginWithWrongPassword() throws Exception {
        String email = "vader@polaris.com";
        User mockUser = new User("Vader", email, "senha_certa", Role.ADMIN);
        
        given(userRepository.findByEmail(email)).willReturn(Optional.of(mockUser));
        given(passwordEncoder.matches("errada", "senha_certa")).willReturn(false);
        
        Map<String, String> loginBody = Map.of("email", email, "password", "errada");
        
        mockMvc.perform(post("/api/v1/auth/login").contentType(MediaType.APPLICATION_JSON).content(
                objectMapper.writeValueAsString(loginBody))).andExpect(status().isUnauthorized());
    }
}