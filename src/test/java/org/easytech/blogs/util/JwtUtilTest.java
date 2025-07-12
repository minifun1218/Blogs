package org.easytech.blogs.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringJUnitExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JWT工具类单元测试
 */
@ExtendWith(SpringJUnitExtension.class)
class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        // 使用反射设置私有字段
        ReflectionTestUtils.setField(jwtUtil, "secret", "mySecretKey123456789012345678901234567890");
        ReflectionTestUtils.setField(jwtUtil, "expiration", 86400L);
        ReflectionTestUtils.setField(jwtUtil, "refreshExpiration", 604800L);
        ReflectionTestUtils.setField(jwtUtil, "issuer", "blog-system");
    }

    @Test
    void testGenerateAccessToken() {
        // Given
        Long userId = 1L;
        String username = "testuser";
        List<String> roles = List.of("USER", "ADMIN");

        // When
        String token = jwtUtil.generateAccessToken(userId, username, roles);

        // Then
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.split("\\.").length == 3); // JWT应该有3个部分
    }

    @Test
    void testGenerateRefreshToken() {
        // Given
        Long userId = 1L;
        String username = "testuser";

        // When
        String refreshToken = jwtUtil.generateRefreshToken(userId, username);

        // Then
        assertNotNull(refreshToken);
        assertFalse(refreshToken.isEmpty());
        assertTrue(refreshToken.split("\\.").length == 3);
    }

    @Test
    void testValidateToken() {
        // Given
        Long userId = 1L;
        String username = "testuser";
        List<String> roles = List.of("USER");
        String token = jwtUtil.generateAccessToken(userId, username, roles);

        // When
        boolean isValid = jwtUtil.validateToken(token);

        // Then
        assertTrue(isValid);
    }

    @Test
    void testValidateInvalidToken() {
        // Given
        String invalidToken = "invalid.token.here";

        // When
        boolean isValid = jwtUtil.validateToken(invalidToken);

        // Then
        assertFalse(isValid);
    }

    @Test
    void testParseToken() {
        // Given
        Long userId = 1L;
        String username = "testuser";
        List<String> roles = List.of("USER", "ADMIN");
        String token = jwtUtil.generateAccessToken(userId, username, roles);

        // When
        var claims = jwtUtil.parseToken(token);

        // Then
        assertNotNull(claims);
        assertEquals(username, claims.getSubject());
        assertEquals(userId, claims.get("userId", Long.class));
        assertEquals(username, claims.get("username", String.class));
        assertEquals("access", claims.get("tokenType", String.class));
    }

    @Test
    void testGetUserIdFromToken() {
        // Given
        Long userId = 1L;
        String username = "testuser";
        List<String> roles = List.of("USER");
        String token = jwtUtil.generateAccessToken(userId, username, roles);

        // When
        Long extractedUserId = jwtUtil.getUserIdFromToken(token);

        // Then
        assertEquals(userId, extractedUserId);
    }

    @Test
    void testGetUsernameFromToken() {
        // Given
        Long userId = 1L;
        String username = "testuser";
        List<String> roles = List.of("USER");
        String token = jwtUtil.generateAccessToken(userId, username, roles);

        // When
        String extractedUsername = jwtUtil.getUsernameFromToken(token);

        // Then
        assertEquals(username, extractedUsername);
    }

    @Test
    void testGetRolesFromToken() {
        // Given
        Long userId = 1L;
        String username = "testuser";
        List<String> roles = List.of("USER", "ADMIN");
        String token = jwtUtil.generateAccessToken(userId, username, roles);

        // When
        List<String> extractedRoles = jwtUtil.getRolesFromToken(token);

        // Then
        assertNotNull(extractedRoles);
        assertEquals(2, extractedRoles.size());
        assertTrue(extractedRoles.contains("USER"));
        assertTrue(extractedRoles.contains("ADMIN"));
    }

    @Test
    void testIsAccessToken() {
        // Given
        Long userId = 1L;
        String username = "testuser";
        List<String> roles = List.of("USER");
        String accessToken = jwtUtil.generateAccessToken(userId, username, roles);

        // When
        boolean isAccessToken = jwtUtil.isAccessToken(accessToken);

        // Then
        assertTrue(isAccessToken);
    }

    @Test
    void testIsRefreshToken() {
        // Given
        Long userId = 1L;
        String username = "testuser";
        String refreshToken = jwtUtil.generateRefreshToken(userId, username);

        // When
        boolean isRefreshToken = jwtUtil.isRefreshToken(refreshToken);

        // Then
        assertTrue(isRefreshToken);
    }

    @Test
    void testExtractToken() {
        // Given
        String token = "sample.jwt.token";
        String bearerToken = "Bearer " + token;

        // When
        String extractedToken = jwtUtil.extractToken(bearerToken);

        // Then
        assertEquals(token, extractedToken);
    }

    @Test
    void testExtractTokenWithoutBearer() {
        // Given
        String token = "sample.jwt.token";

        // When
        String extractedToken = jwtUtil.extractToken(token);

        // Then
        assertEquals(token, extractedToken);
    }

    @Test
    void testGenerateBearerToken() {
        // Given
        String token = "sample.jwt.token";

        // When
        String bearerToken = jwtUtil.generateBearerToken(token);

        // Then
        assertEquals("Bearer " + token, bearerToken);
    }

    @Test
    void testIsTokenExpired() {
        // Given
        Long userId = 1L;
        String username = "testuser";
        List<String> roles = List.of("USER");
        String token = jwtUtil.generateAccessToken(userId, username, roles);

        // When
        boolean isExpired = jwtUtil.isTokenExpired(token);

        // Then
        assertFalse(isExpired); // 新生成的token不应该过期
    }

    @Test
    void testGetExpirationFromToken() {
        // Given
        Long userId = 1L;
        String username = "testuser";
        List<String> roles = List.of("USER");
        String token = jwtUtil.generateAccessToken(userId, username, roles);

        // When
        var expiration = jwtUtil.getExpirationFromToken(token);

        // Then
        assertNotNull(expiration);
        assertTrue(expiration.getTime() > System.currentTimeMillis());
    }

    @Test
    void testGetIssuedAtFromToken() {
        // Given
        Long userId = 1L;
        String username = "testuser";
        List<String> roles = List.of("USER");
        String token = jwtUtil.generateAccessToken(userId, username, roles);

        // When
        var issuedAt = jwtUtil.getIssuedAtFromToken(token);

        // Then
        assertNotNull(issuedAt);
        assertTrue(issuedAt.getTime() <= System.currentTimeMillis());
    }
}