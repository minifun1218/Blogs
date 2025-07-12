package org.easytech.blogs.service.impl;

import org.easytech.blogs.dto.auth.LoginRequest;
import org.easytech.blogs.dto.auth.LoginResponse;
import org.easytech.blogs.dto.auth.RegisterRequest;
import org.easytech.blogs.entity.Role;
import org.easytech.blogs.entity.User;
import org.easytech.blogs.exception.UnauthorizedException;
import org.easytech.blogs.exception.ValidationException;
import org.easytech.blogs.service.UserCoinService;
import org.easytech.blogs.service.UserService;
import org.easytech.blogs.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * AuthService单元测试
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserService userService;

    @Mock
    private UserCoinService userCoinService;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthServiceImpl authService;

    private User testUser;
    private List<Role> testRoles;

    @BeforeEach
    void setUp() {
        // 设置JWT过期时间
        ReflectionTestUtils.setField(authService, "jwtExpiration", 86400L);

        // 创建测试用户
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("$2a$10$encodedPassword");
        testUser.setEmail("test@example.com");
        testUser.setNickname("Test User");
        testUser.setStatus(1);
        testUser.setLastLoginTime(LocalDateTime.now());

        // 创建测试角色
        Role userRole = new Role();
        userRole.setId(1L);
        userRole.setCode("USER");
        userRole.setName("普通用户");
        testRoles = List.of(userRole);
    }

    @Test
    void testLoginSuccess() {
        // Given
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("password123");

        when(userService.findByUsername("testuser")).thenReturn(testUser);
        when(passwordEncoder.matches("password123", testUser.getPassword())).thenReturn(true);
        when(userService.getUserRoles(testUser.getId())).thenReturn(testRoles);
        when(jwtUtil.generateAccessToken(eq(1L), eq("testuser"), any())).thenReturn("access.token.here");
        when(jwtUtil.generateRefreshToken(1L, "testuser")).thenReturn("refresh.token.here");
        when(userService.updateLastLoginTime(1L)).thenReturn(true);

        // When
        LoginResponse response = authService.login(request);

        // Then
        assertNotNull(response);
        assertEquals("access.token.here", response.getAccessToken());
        assertEquals("refresh.token.here", response.getRefreshToken());
        assertEquals(86400L, response.getExpiresIn());
        assertNotNull(response.getUserInfo());
        assertEquals("testuser", response.getUserInfo().getUsername());
        assertEquals("Test User", response.getUserInfo().getNickname());

        verify(userService).updateLastLoginTime(1L);
    }

    @Test
    void testLoginUserNotFound() {
        // Given
        LoginRequest request = new LoginRequest();
        request.setUsername("nonexistent");
        request.setPassword("password123");

        when(userService.findByUsername("nonexistent")).thenReturn(null);

        // When & Then
        assertThrows(UnauthorizedException.class, () -> authService.login(request));
    }

    @Test
    void testLoginWrongPassword() {
        // Given
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("wrongpassword");

        when(userService.findByUsername("testuser")).thenReturn(testUser);
        when(passwordEncoder.matches("wrongpassword", testUser.getPassword())).thenReturn(false);

        // When & Then
        assertThrows(UnauthorizedException.class, () -> authService.login(request));
    }

    @Test
    void testLoginUserDisabled() {
        // Given
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("password123");

        testUser.setStatus(0); // 禁用状态

        when(userService.findByUsername("testuser")).thenReturn(testUser);
        when(passwordEncoder.matches("password123", testUser.getPassword())).thenReturn(true);

        // When & Then
        assertThrows(UnauthorizedException.class, () -> authService.login(request));
    }

    @Test
    void testRegisterSuccess() {
        // Given
        RegisterRequest request = new RegisterRequest();
        request.setUsername("newuser");
        request.setPassword("password123");
        request.setConfirmPassword("password123");
        request.setEmail("newuser@example.com");
        request.setNickname("New User");

        User newUser = new User();
        newUser.setId(2L);
        newUser.setUsername("newuser");
        newUser.setEmail("newuser@example.com");
        newUser.setNickname("New User");

        when(userService.existsByUsername("newuser")).thenReturn(false);
        when(userService.existsByEmail("newuser@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("$2a$10$encodedNewPassword");
        when(userService.register(any(User.class))).thenReturn(newUser);
        when(userCoinService.createUserCoinAccount(2L)).thenReturn(any());

        // Mock login after registration
        when(userService.findByUsername("newuser")).thenReturn(newUser);
        when(passwordEncoder.matches("password123", "$2a$10$encodedNewPassword")).thenReturn(true);
        when(userService.getUserRoles(2L)).thenReturn(testRoles);
        when(jwtUtil.generateAccessToken(eq(2L), eq("newuser"), any())).thenReturn("new.access.token");
        when(jwtUtil.generateRefreshToken(2L, "newuser")).thenReturn("new.refresh.token");

        // When
        LoginResponse response = authService.register(request);

        // Then
        assertNotNull(response);
        assertEquals("new.access.token", response.getAccessToken());
        verify(userService).register(any(User.class));
        verify(userCoinService).createUserCoinAccount(2L);
    }

    @Test
    void testRegisterPasswordMismatch() {
        // Given
        RegisterRequest request = new RegisterRequest();
        request.setUsername("newuser");
        request.setPassword("password123");
        request.setConfirmPassword("differentpassword");
        request.setEmail("newuser@example.com");

        // When & Then
        assertThrows(ValidationException.class, () -> authService.register(request));
    }

    @Test
    void testRegisterUsernameExists() {
        // Given
        RegisterRequest request = new RegisterRequest();
        request.setUsername("existinguser");
        request.setPassword("password123");
        request.setConfirmPassword("password123");
        request.setEmail("newuser@example.com");

        when(userService.existsByUsername("existinguser")).thenReturn(true);

        // When & Then
        assertThrows(ValidationException.class, () -> authService.register(request));
    }

    @Test
    void testRegisterEmailExists() {
        // Given
        RegisterRequest request = new RegisterRequest();
        request.setUsername("newuser");
        request.setPassword("password123");
        request.setConfirmPassword("password123");
        request.setEmail("existing@example.com");

        when(userService.existsByUsername("newuser")).thenReturn(false);
        when(userService.existsByEmail("existing@example.com")).thenReturn(true);

        // When & Then
        assertThrows(ValidationException.class, () -> authService.register(request));
    }

    @Test
    void testRefreshTokenSuccess() {
        // Given
        String refreshToken = "valid.refresh.token";

        when(jwtUtil.validateToken(refreshToken)).thenReturn(true);
        when(jwtUtil.isRefreshToken(refreshToken)).thenReturn(true);
        when(jwtUtil.getUserIdFromToken(refreshToken)).thenReturn(1L);
        when(jwtUtil.getUsernameFromToken(refreshToken)).thenReturn("testuser");
        when(userService.getUserById(1L)).thenReturn(testUser);
        when(userService.getUserRoles(1L)).thenReturn(testRoles);
        when(jwtUtil.generateAccessToken(eq(1L), eq("testuser"), any())).thenReturn("new.access.token");

        // When
        LoginResponse response = authService.refreshToken(refreshToken);

        // Then
        assertNotNull(response);
        assertEquals("new.access.token", response.getAccessToken());
        assertEquals(refreshToken, response.getRefreshToken());
        assertEquals("testuser", response.getUserInfo().getUsername());
    }

    @Test
    void testRefreshTokenInvalid() {
        // Given
        String invalidToken = "invalid.token";

        when(jwtUtil.validateToken(invalidToken)).thenReturn(false);

        // When & Then
        assertThrows(UnauthorizedException.class, () -> authService.refreshToken(invalidToken));
    }

    @Test
    void testRefreshTokenNotRefreshType() {
        // Given
        String accessToken = "access.token";

        when(jwtUtil.validateToken(accessToken)).thenReturn(true);
        when(jwtUtil.isRefreshToken(accessToken)).thenReturn(false);

        // When & Then
        assertThrows(UnauthorizedException.class, () -> authService.refreshToken(accessToken));
    }

    @Test
    void testGetCurrentUser() {
        // Given
        Long userId = 1L;

        when(userService.getUserById(userId)).thenReturn(testUser);
        when(userService.getUserRoles(userId)).thenReturn(testRoles);

        // When
        LoginResponse.UserInfo userInfo = authService.getCurrentUser(userId);

        // Then
        assertNotNull(userInfo);
        assertEquals("testuser", userInfo.getUsername());
        assertEquals("Test User", userInfo.getNickname());
        assertEquals("test@example.com", userInfo.getEmail());
        assertEquals(1, userInfo.getRoles().size());
        assertEquals("USER", userInfo.getRoles().get(0));
    }

    @Test
    void testIsUsernameAvailable() {
        // Given
        when(userService.existsByUsername("available")).thenReturn(false);
        when(userService.existsByUsername("taken")).thenReturn(true);

        // When & Then
        assertTrue(authService.isUsernameAvailable("available"));
        assertFalse(authService.isUsernameAvailable("taken"));
    }

    @Test
    void testIsEmailAvailable() {
        // Given
        when(userService.existsByEmail("available@example.com")).thenReturn(false);
        when(userService.existsByEmail("taken@example.com")).thenReturn(true);

        // When & Then
        assertTrue(authService.isEmailAvailable("available@example.com"));
        assertFalse(authService.isEmailAvailable("taken@example.com"));
    }

    @Test
    void testLogout() {
        // Given
        String token = "valid.token";
        when(jwtUtil.getUsernameFromToken(token)).thenReturn("testuser");

        // When & Then
        assertDoesNotThrow(() -> authService.logout(token));
    }
}