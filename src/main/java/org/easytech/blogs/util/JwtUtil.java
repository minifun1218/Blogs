package org.easytech.blogs.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * JWT工具类
 * 用于生成、解析和验证JWT令牌
 */
@Slf4j
@Component
public class JwtUtil {

    @Value("${blog.jwt.secret:mySecretKey123456789012345678901234567890}")
    private String secret;

    @Value("${blog.jwt.expiration:86400}")
    private Long expiration; // 默认24小时，单位：秒

    @Value("${blog.jwt.refresh-expiration:604800}")
    private Long refreshExpiration; // 默认7天，单位：秒

    @Value("${blog.jwt.issuer:blog-system}")
    private String issuer;

    /**
     * 获取签名密钥
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = secret.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 生成访问令牌
     * @param userId 用户ID
     * @param username 用户名
     * @param roles 角色列表
     * @return JWT token
     */
    public String generateAccessToken(Long userId, String username, List<String> roles) {
        Instant now = Instant.now();
        Instant expiry = now.plus(expiration, ChronoUnit.SECONDS);

        return Jwts.builder()
                .subject(username)
                .claim("userId", userId)
                .claim("username", username)
                .claim("roles", roles)
                .claim("tokenType", "access")
                .issuer(issuer)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * 生成刷新令牌
     * @param userId 用户ID
     * @param username 用户名
     * @return JWT refresh token
     */
    public String generateRefreshToken(Long userId, String username) {
        Instant now = Instant.now();
        Instant expiry = now.plus(refreshExpiration, ChronoUnit.SECONDS);

        return Jwts.builder()
                .subject(username)
                .claim("userId", userId)
                .claim("username", username)
                .claim("tokenType", "refresh")
                .issuer(issuer)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * 解析JWT令牌
     * @param token JWT令牌
     * @return Claims对象
     */
    public Claims parseToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            log.debug("JWT token已过期: {}", e.getMessage());
            throw new RuntimeException("Token已过期");
        } catch (UnsupportedJwtException e) {
            log.debug("不支持的JWT token: {}", e.getMessage());
            throw new RuntimeException("不支持的Token格式");
        } catch (MalformedJwtException e) {
            log.debug("JWT token格式错误: {}", e.getMessage());
            throw new RuntimeException("Token格式错误");
        } catch (SecurityException e) {
            log.debug("JWT token签名验证失败: {}", e.getMessage());
            throw new RuntimeException("Token签名验证失败");
        } catch (IllegalArgumentException e) {
            log.debug("JWT token参数错误: {}", e.getMessage());
            throw new RuntimeException("Token参数错误");
        }
    }

    /**
     * 验证JWT令牌
     * @param token JWT令牌
     * @return 是否有效
     */
    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (Exception e) {
            log.debug("JWT token验证失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 从令牌中获取用户名
     * @param token JWT令牌
     * @return 用户名
     */
    public String getUsernameFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.getSubject();
    }

    /**
     * 从令牌中获取用户ID
     * @param token JWT令牌
     * @return 用户ID
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.get("userId", Long.class);
    }

    /**
     * 从令牌中获取角色列表
     * @param token JWT令牌
     * @return 角色列表
     */
    @SuppressWarnings("unchecked")
    public List<String> getRolesFromToken(String token) {
        Claims claims = parseToken(token);
        return (List<String>) claims.get("roles");
    }

    /**
     * 从令牌中获取令牌类型
     * @param token JWT令牌
     * @return 令牌类型
     */
    public String getTokenTypeFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.get("tokenType", String.class);
    }

    /**
     * 检查令牌是否过期
     * @param token JWT令牌
     * @return 是否过期
     */
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = parseToken(token);
            return claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * 获取令牌过期时间
     * @param token JWT令牌
     * @return 过期时间
     */
    public Date getExpirationFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.getExpiration();
    }

    /**
     * 获取令牌签发时间
     * @param token JWT令牌
     * @return 签发时间
     */
    public Date getIssuedAtFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.getIssuedAt();
    }

    /**
     * 检查是否为刷新令牌
     * @param token JWT令牌
     * @return 是否为刷新令牌
     */
    public boolean isRefreshToken(String token) {
        try {
            String tokenType = getTokenTypeFromToken(token);
            return "refresh".equals(tokenType);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 检查是否为访问令牌
     * @param token JWT令牌
     * @return 是否为访问令牌
     */
    public boolean isAccessToken(String token) {
        try {
            String tokenType = getTokenTypeFromToken(token);
            return "access".equals(tokenType);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 从令牌中获取所有声明
     * @param token JWT令牌
     * @return 所有声明
     */
    public Map<String, Object> getAllClaimsFromToken(String token) {
        Claims claims = parseToken(token);
        return Map.of(
            "userId", claims.get("userId"),
            "username", claims.get("username"),
            "roles", claims.get("roles"),
            "tokenType", claims.get("tokenType"),
            "issuer", claims.getIssuer(),
            "subject", claims.getSubject(),
            "issuedAt", claims.getIssuedAt(),
            "expiration", claims.getExpiration()
        );
    }

    /**
     * 提取Bearer令牌
     * @param bearerToken Bearer格式的令牌
     * @return 纯JWT令牌
     */
    public String extractToken(String bearerToken) {
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return bearerToken;
    }

    /**
     * 生成Bearer格式的令牌
     * @param token JWT令牌
     * @return Bearer格式令牌
     */
    public String generateBearerToken(String token) {
        return "Bearer " + token;
    }
}