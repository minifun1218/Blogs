package org.easytech.blogs.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.easytech.blogs.common.Result;
import org.easytech.blogs.dto.auth.*;
import org.easytech.blogs.service.AuthService;
import org.easytech.blogs.util.JwtUtil;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 * 处理用户登录、注册、令牌刷新等认证相关操作
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            LoginResponse response = authService.login(request);
            return Result.success(response);
        } catch (Exception e) {
            log.error("用户登录失败: {}", e.getMessage());
            return Result.error("登录失败: " + e.getMessage());
        }
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<LoginResponse> register(@Valid @RequestBody RegisterRequest request) {
        try {
            LoginResponse response = authService.register(request);
            return Result.success(response);
        } catch (Exception e) {
            log.error("用户注册失败: {}", e.getMessage());
            return Result.error("注册失败: " + e.getMessage());
        }
    }

    /**
     * 刷新访问令牌
     */
    @PostMapping("/refresh")
    public Result<LoginResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        try {
            LoginResponse response = authService.refreshToken(request.getRefreshToken());
            return Result.success(response);
        } catch (Exception e) {
            log.error("刷新令牌失败: {}", e.getMessage());
            return Result.error("刷新令牌失败: " + e.getMessage());
        }
    }

    /**
     * 用户登出
     */
    @PostMapping("/logout")
    public Result<Void> logout(HttpServletRequest request) {
        try {
            String token = extractToken(request);
            if (StringUtils.hasText(token)) {
                authService.logout(token);
            }
            return Result.success();
        } catch (Exception e) {
            log.error("用户登出失败: {}", e.getMessage());
            return Result.error("登出失败: " + e.getMessage());
        }
    }

    /**
     * 修改密码
     */
    @PostMapping("/change-password")
    public Result<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request,
                                     HttpServletRequest httpRequest) {
        try {
            String token = extractToken(httpRequest);
            if (!StringUtils.hasText(token)) {
                return Result.unauthorized();
            }
            
            Long userId = jwtUtil.getUserIdFromToken(token);
            authService.changePassword(userId, request);
            return Result.success();
        } catch (Exception e) {
            log.error("修改密码失败: {}", e.getMessage());
            return Result.error("修改密码失败: " + e.getMessage());
        }
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/me")
    public Result<LoginResponse.UserInfo> getCurrentUser(HttpServletRequest request) {
        try {
            String token = extractToken(request);
            if (!StringUtils.hasText(token)) {
                return Result.unauthorized();
            }
            
            Long userId = jwtUtil.getUserIdFromToken(token);
            LoginResponse.UserInfo userInfo = authService.getCurrentUser(userId);
            return Result.success(userInfo);
        } catch (Exception e) {
            log.error("获取当前用户信息失败: {}", e.getMessage());
            return Result.error("获取用户信息失败: " + e.getMessage());
        }
    }

    /**
     * 验证令牌
     */
    @PostMapping("/validate")
    public Result<Boolean> validateToken(@RequestParam String token) {
        try {
            boolean isValid = jwtUtil.validateToken(token) && jwtUtil.isAccessToken(token);
            return Result.success(isValid);
        } catch (Exception e) {
            log.debug("令牌验证失败: {}", e.getMessage());
            return Result.success(false);
        }
    }

    /**
     * 检查用户名是否可用
     */
    @GetMapping("/check-username")
    public Result<Boolean> checkUsername(@RequestParam String username) {
        try {
            boolean available = authService.isUsernameAvailable(username);
            return Result.success(available);
        } catch (Exception e) {
            log.error("检查用户名可用性失败: {}", e.getMessage());
            return Result.error("检查失败: " + e.getMessage());
        }
    }

    /**
     * 检查邮箱是否可用
     */
    @GetMapping("/check-email")
    public Result<Boolean> checkEmail(@RequestParam String email) {
        try {
            boolean available = authService.isEmailAvailable(email);
            return Result.success(available);
        } catch (Exception e) {
            log.error("检查邮箱可用性失败: {}", e.getMessage());
            return Result.error("检查失败: " + e.getMessage());
        }
    }

    /**
     * 获取令牌信息
     */
    @PostMapping("/token-info")
    public Result<Object> getTokenInfo(@RequestParam String token) {
        try {
            if (!jwtUtil.validateToken(token)) {
                return Result.error("无效的令牌");
            }
            
            Object tokenInfo = jwtUtil.getAllClaimsFromToken(token);
            return Result.success(tokenInfo);
        } catch (Exception e) {
            log.error("获取令牌信息失败: {}", e.getMessage());
            return Result.error("获取令牌信息失败: " + e.getMessage());
        }
    }

    /**
     * 从请求中提取JWT令牌
     */
    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return jwtUtil.extractToken(bearerToken);
        }
        return null;
    }
}