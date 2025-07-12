package org.easytech.blogs.service.impl;

import org.easytech.blogs.entity.Role;
import org.easytech.blogs.entity.User;
import org.easytech.blogs.exception.BusinessException;
import org.easytech.blogs.exception.ValidationException;
import org.easytech.blogs.mapper.RoleMapper;
import org.easytech.blogs.mapper.UserMapper;
import org.easytech.blogs.mapper.UserRoleMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * UserService单元测试
 */
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private RoleMapper roleMapper;

    @Mock
    private UserRoleMapper userRoleMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("$2a$10$encodedPassword");
        testUser.setEmail("test@example.com");
        testUser.setNickname("Test User");
        testUser.setStatus(1);
        testUser.setCreateTime(LocalDateTime.now());
        testUser.setUpdateTime(LocalDateTime.now());
    }

    @Test
    void testFindByUsername() {
        // Given
        when(userMapper.findByUsername("testuser")).thenReturn(testUser);

        // When
        User result = userService.findByUsername("testuser");

        // Then
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        verify(userMapper).findByUsername("testuser");
    }

    @Test
    void testFindByUsernameEmpty() {
        // When
        User result = userService.findByUsername("");

        // Then
        assertNull(result);
        verify(userMapper, never()).findByUsername(anyString());
    }

    @Test
    void testFindByEmail() {
        // Given
        when(userMapper.findByEmail("test@example.com")).thenReturn(testUser);

        // When
        User result = userService.findByEmail("test@example.com");

        // Then
        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
        verify(userMapper).findByEmail("test@example.com");
    }

    @Test
    void testRegisterSuccess() {
        // Given
        User newUser = new User();
        newUser.setUsername("newuser");
        newUser.setPassword("password123");
        newUser.setEmail("new@example.com");

        User savedUser = new User();
        savedUser.setId(2L);
        savedUser.setUsername("newuser");
        savedUser.setPassword("$2a$10$encodedPassword");
        savedUser.setEmail("new@example.com");

        when(userMapper.findByUsername("newuser")).thenReturn(null); // 用户名不存在
        when(passwordEncoder.encode("password123")).thenReturn("$2a$10$encodedPassword");
        when(userMapper.insert(any(User.class))).thenReturn(1);
        when(userMapper.selectById(any(Long.class))).thenReturn(savedUser);

        Role defaultRole = new Role();
        defaultRole.setId(1L);
        defaultRole.setCode("USER");
        when(roleMapper.selectByCode("USER")).thenReturn(defaultRole);
        when(userRoleMapper.insert(any())).thenReturn(1);

        // When
        User result = userService.register(newUser);

        // Then
        assertNotNull(result);
        assertEquals("newuser", result.getUsername());
        verify(passwordEncoder).encode("password123");
        verify(userMapper).insert(any(User.class));
    }

    @Test
    void testRegisterNullUser() {
        // When & Then
        assertThrows(ValidationException.class, () -> userService.register(null));
    }

    @Test
    void testRegisterExistingUsername() {
        // Given
        User newUser = new User();
        newUser.setUsername("existinguser");
        newUser.setPassword("password123");

        when(userMapper.findByUsername("existinguser")).thenReturn(testUser);

        // When & Then
        assertThrows(BusinessException.class, () -> userService.register(newUser));
    }

    @Test
    void testLoginSuccess() {
        // Given
        when(userMapper.findByUsername("testuser")).thenReturn(testUser);
        when(passwordEncoder.matches("password123", testUser.getPassword())).thenReturn(true);
        when(userMapper.updateLastLoginTime(testUser.getId())).thenReturn(1);

        // When
        User result = userService.login("testuser", "password123");

        // Then
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        verify(userMapper).updateLastLoginTime(testUser.getId());
    }

    @Test
    void testLoginUserNotFound() {
        // Given
        when(userMapper.findByUsername("nonexistent")).thenReturn(null);

        // When & Then
        assertThrows(BusinessException.class, () -> userService.login("nonexistent", "password123"));
    }

    @Test
    void testLoginWrongPassword() {
        // Given
        when(userMapper.findByUsername("testuser")).thenReturn(testUser);
        when(passwordEncoder.matches("wrongpassword", testUser.getPassword())).thenReturn(false);

        // When & Then
        assertThrows(BusinessException.class, () -> userService.login("testuser", "wrongpassword"));
    }

    @Test
    void testLoginDisabledUser() {
        // Given
        testUser.setStatus(0); // 禁用状态
        when(userMapper.findByUsername("testuser")).thenReturn(testUser);
        when(passwordEncoder.matches("password123", testUser.getPassword())).thenReturn(true);

        // When & Then
        assertThrows(BusinessException.class, () -> userService.login("testuser", "password123"));
    }

    @Test
    void testUpdateUser() {
        // Given
        when(userMapper.updateById(testUser)).thenReturn(1);

        // When
        boolean result = userService.updateUser(testUser);

        // Then
        assertTrue(result);
        verify(userMapper).updateById(testUser);
    }

    @Test
    void testUpdateUserNull() {
        // When & Then
        assertThrows(ValidationException.class, () -> userService.updateUser(null));
    }

    @Test
    void testChangePasswordSuccess() {
        // Given
        when(userMapper.selectById(1L)).thenReturn(testUser);
        when(passwordEncoder.matches("oldpassword", testUser.getPassword())).thenReturn(true);
        when(passwordEncoder.encode("newpassword")).thenReturn("$2a$10$newEncodedPassword");
        when(userMapper.updateById(any(User.class))).thenReturn(1);

        // When
        boolean result = userService.changePassword(1L, "oldpassword", "newpassword");

        // Then
        assertTrue(result);
        verify(passwordEncoder).encode("newpassword");
    }

    @Test
    void testChangePasswordWrongOldPassword() {
        // Given
        when(userMapper.selectById(1L)).thenReturn(testUser);
        when(passwordEncoder.matches("wrongoldpassword", testUser.getPassword())).thenReturn(false);

        // When & Then
        assertThrows(BusinessException.class, () -> 
            userService.changePassword(1L, "wrongoldpassword", "newpassword"));
    }

    @Test
    void testGetUserById() {
        // Given
        when(userMapper.selectById(1L)).thenReturn(testUser);

        // When
        User result = userService.getUserById(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(userMapper).selectById(1L);
    }

    @Test
    void testGetUserByIdNull() {
        // When
        User result = userService.getUserById(null);

        // Then
        assertNull(result);
        verify(userMapper, never()).selectById(any());
    }

    @Test
    void testUpdateUserStatus() {
        // Given
        when(userMapper.updateUserStatus(1L, 0)).thenReturn(1);

        // When
        boolean result = userService.updateUserStatus(1L, 0);

        // Then
        assertTrue(result);
        verify(userMapper).updateUserStatus(1L, 0);
    }

    @Test
    void testRemoveById() {
        // Given
        when(userRoleMapper.deleteByUserId(1L)).thenReturn(1);
        when(userMapper.deleteById(1L)).thenReturn(1);

        // When
        boolean result = userService.removeById(1L);

        // Then
        assertTrue(result);
        verify(userRoleMapper).deleteByUserId(1L);
        verify(userMapper).deleteById(1L);
    }

    @Test
    void testIsUsernameExists() {
        // Given
        when(userMapper.findByUsername("existing")).thenReturn(testUser);
        when(userMapper.findByUsername("nonexisting")).thenReturn(null);

        // When & Then
        assertTrue(userService.isUsernameExists("existing", null));
        assertFalse(userService.isUsernameExists("nonexisting", null));
        
        // Test excluding current user
        assertFalse(userService.isUsernameExists("existing", 1L));
    }

    @Test
    void testIsEmailExists() {
        // Given
        when(userMapper.findByEmail("existing@example.com")).thenReturn(testUser);
        when(userMapper.findByEmail("nonexisting@example.com")).thenReturn(null);

        // When & Then
        assertTrue(userService.isEmailExists("existing@example.com", null));
        assertFalse(userService.isEmailExists("nonexisting@example.com", null));
        
        // Test excluding current user
        assertFalse(userService.isEmailExists("existing@example.com", 1L));
    }

    @Test
    void testGetUserRoles() {
        // Given
        Role role1 = new Role();
        role1.setId(1L);
        role1.setCode("USER");
        role1.setName("普通用户");

        Role role2 = new Role();
        role2.setId(2L);
        role2.setCode("ADMIN");
        role2.setName("管理员");

        List<Role> roles = List.of(role1, role2);
        when(roleMapper.selectByUserId(1L)).thenReturn(roles);

        // When
        List<Role> result = userService.getUserRoles(1L);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("USER", result.get(0).getCode());
        assertEquals("ADMIN", result.get(1).getCode());
    }

    @Test
    void testUpdateLastLoginTime() {
        // Given
        when(userMapper.updateLastLoginTime(1L)).thenReturn(1);

        // When
        boolean result = userService.updateLastLoginTime(1L);

        // Then
        assertTrue(result);
        verify(userMapper).updateLastLoginTime(1L);
    }

    @Test
    void testCountUsers() {
        // Given
        when(userMapper.countByStatus(1)).thenReturn(100L);
        when(userMapper.countAll()).thenReturn(150L);

        // When & Then
        assertEquals(100L, userService.countUsers(1));
        assertEquals(150L, userService.countUsers(null));
    }
}