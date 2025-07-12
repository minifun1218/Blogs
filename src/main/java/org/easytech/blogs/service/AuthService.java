package org.easytech.blogs.service;

import org.easytech.blogs.dto.auth.*;

/**
 * 认证服务接口
 */
public interface AuthService {
    
    /**
     * 用户登录
     * @param request 登录请求
     * @return 登录响应
     */
    LoginResponse login(LoginRequest request);
    
    /**
     * 用户注册
     * @param request 注册请求
     * @return 登录响应
     */
    LoginResponse register(RegisterRequest request);
    
    /**
     * 刷新访问令牌
     * @param refreshToken 刷新令牌
     * @return 新的登录响应
     */
    LoginResponse refreshToken(String refreshToken);
    
    /**
     * 用户登出
     * @param token 访问令牌
     */
    void logout(String token);
    
    /**
     * 修改密码
     * @param userId 用户ID
     * @param request 修改密码请求
     */
    void changePassword(Long userId, ChangePasswordRequest request);
    
    /**
     * 获取当前用户信息
     * @param userId 用户ID
     * @return 用户信息
     */
    LoginResponse.UserInfo getCurrentUser(Long userId);
    
    /**
     * 检查用户名是否可用
     * @param username 用户名
     * @return 是否可用
     */
    boolean isUsernameAvailable(String username);
    
    /**
     * 检查邮箱是否可用
     * @param email 邮箱
     * @return 是否可用
     */
    boolean isEmailAvailable(String email);
}