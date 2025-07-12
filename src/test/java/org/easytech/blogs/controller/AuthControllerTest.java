package org.easytech.blogs.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.easytech.blogs.dto.auth.LoginRequest;
import org.easytech.blogs.dto.auth.RegisterRequest;
import org.easytech.blogs.service.AuthService;
import org.easytech.blogs.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * AuthController集成测试
 */
@WebMvcTest(AuthController.class)
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtUtil jwtUtil;

    private LoginRequest loginRequest;
    private RegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password123");

        registerRequest = new RegisterRequest();
        registerRequest.setUsername("newuser");
        registerRequest.setPassword("password123");
        registerRequest.setConfirmPassword("password123");
        registerRequest.setEmail("newuser@example.com");
        registerRequest.setNickname("New User");
    }

    @Test
    void testLogin() throws Exception {
        // Given
        // Mock service response would be set up here

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk());
    }

    @Test
    void testLoginWithInvalidData() throws Exception {
        // Given
        loginRequest.setUsername(""); // Invalid username

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testRegister() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/auth/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk());
    }

    @Test
    void testRegisterWithInvalidEmail() throws Exception {
        // Given
        registerRequest.setEmail("invalid-email"); // Invalid email format

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpected(status().isBadRequest());
    }

    @Test
    void testCheckUsernameAvailable() throws Exception {
        // Given
        when(authService.isUsernameAvailable("available")).thenReturn(true);

        // When & Then
        mockMvc.perform(get("/api/auth/check-username")
                .param("username", "available"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(true));
    }

    @Test
    void testCheckEmailAvailable() throws Exception {
        // Given
        when(authService.isEmailAvailable("available@example.com")).thenReturn(true);

        // When & Then
        mockMvc.perform(get("/api/auth/check-email")
                .param("email", "available@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(true));
    }

    @Test
    void testValidateToken() throws Exception {
        // Given
        String token = "valid.token.here";
        when(jwtUtil.validateToken(token)).thenReturn(true);
        when(jwtUtil.isAccessToken(token)).thenReturn(true);

        // When & Then
        mockMvc.perform(post("/api/auth/validate")
                .with(csrf())
                .param("token", token))
                .andExpect(status().isOk())
                .andExpected(jsonPath("$.data").value(true));
    }

    @Test
    @WithMockUser(username = "testuser")
    void testLogout() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/auth/logout")
                .with(csrf())
                .header("Authorization", "Bearer valid.token.here"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "testuser", authorities = {"ROLE_USER"})
    void testGetCurrentUser() throws Exception {
        // Given
        when(jwtUtil.getUserIdFromToken(any())).thenReturn(1L);

        // When & Then
        mockMvc.perform(get("/api/auth/me")
                .header("Authorization", "Bearer valid.token.here"))
                .andExpect(status().isOk());
    }
}