package org.easytech.blogs.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.easytech.blogs.dto.auth.*;
import org.easytech.blogs.entity.Role;
import org.easytech.blogs.entity.User;
import org.easytech.blogs.exception.BusinessException;
import org.easytech.blogs.exception.UnauthorizedException;
import org.easytech.blogs.exception.ValidationException;
import org.easytech.blogs.service.AuthService;
import org.easytech.blogs.service.UserCoinService;
import org.easytech.blogs.service.UserService;
import org.easytech.blogs.util.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 认证服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final UserCoinService userCoinService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @Value("${blog.jwt.expiration:86400}")
    private Long jwtExpiration;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LoginResponse login(LoginRequest request) {
        // 验证用户名和密码
        User user = userService.findByUsername(request.getUsername());
        if (user == null) {
            throw new UnauthorizedException("用户名或密码错误");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("用户名或密码错误");
        }

        // 检查用户状态
        if (user.getStatus() == null || user.getStatus() != 1) {
            throw new UnauthorizedException("账户已被禁用");
        }

        // 更新最后登录时间
        userService.updateLastLoginTime(user.getId());

        // 获取用户角色
        List<Role> roles = userService.getUserRoles(user.getId());
        List<String> roleNames = roles.stream()
                .map(Role::getCode)
                .collect(Collectors.toList());

        // 生成JWT令牌
        String accessToken = jwtUtil.generateAccessToken(user.getId(), user.getUsername(), roleNames);
        String refreshToken = jwtUtil.generateRefreshToken(user.getId(), user.getUsername());

        // 构建用户信息
        LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo();
        userInfo.setUserId(user.getId());
        userInfo.setUsername(user.getUsername());
        userInfo.setNickname(user.getNickname());
        userInfo.setEmail(user.getEmail());
        userInfo.setAvatar(user.getAvatar());
        userInfo.setRoles(roleNames);
        userInfo.setLastLoginTime(user.getLastLoginTime());

        // 构建响应
        LoginResponse response = new LoginResponse();
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken);
        response.setExpiresIn(jwtExpiration);
        response.setUserInfo(userInfo);

        log.info("用户登录成功: {}", user.getUsername());
        return response;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LoginResponse register(RegisterRequest request) {
        // 验证密码确认
        if (!Objects.equals(request.getPassword(), request.getConfirmPassword())) {
            throw new ValidationException("两次输入的密码不一致");
        }

        // 检查用户名是否已存在
        if (userService.existsByUsername(request.getUsername())) {
            throw new ValidationException("用户名已存在");
        }

        // 检查邮箱是否已存在
        if (userService.existsByEmail(request.getEmail())) {
            throw new ValidationException("邮箱已被注册");
        }

        // 创建用户
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setNickname(StringUtils.hasText(request.getNickname()) ? 
                request.getNickname() : request.getUsername());
        user.setStatus(1); // 默认启用
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());

        // 注册用户
        User registeredUser = userService.register(user);
        if (registeredUser == null) {
            throw new BusinessException("用户注册失败");
        }

        // 创建用户积分账户
        try {
            userCoinService.createUserCoinAccount(registeredUser.getId());
        } catch (Exception e) {
            log.warn("创建用户积分账户失败，用户ID: {}", registeredUser.getId(), e);
        }

        // 自动登录
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(request.getUsername());
        loginRequest.setPassword(request.getPassword());

        log.info("用户注册成功: {}", request.getUsername());
        return login(loginRequest);
    }

    @Override
    public LoginResponse refreshToken(String refreshToken) {
        if (!StringUtils.hasText(refreshToken)) {
            throw new ValidationException("刷新令牌不能为空");
        }

        // 验证刷新令牌
        if (!jwtUtil.validateToken(refreshToken) || !jwtUtil.isRefreshToken(refreshToken)) {
            throw new UnauthorizedException("无效的刷新令牌");
        }

        try {
            // 从刷新令牌中获取用户信息
            Long userId = jwtUtil.getUserIdFromToken(refreshToken);
            String username = jwtUtil.getUsernameFromToken(refreshToken);

            // 获取用户信息
            User user = userService.getUserById(userId);
            if (user == null || user.getStatus() != 1) {
                throw new UnauthorizedException("用户不存在或已被禁用");
            }

            // 获取用户角色
            List<Role> roles = userService.getUserRoles(user.getId());
            List<String> roleNames = roles.stream()
                    .map(Role::getCode)
                    .collect(Collectors.toList());

            // 生成新的访问令牌
            String newAccessToken = jwtUtil.generateAccessToken(userId, username, roleNames);

            // 构建用户信息
            LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo();
            userInfo.setUserId(user.getId());
            userInfo.setUsername(user.getUsername());
            userInfo.setNickname(user.getNickname());
            userInfo.setEmail(user.getEmail());
            userInfo.setAvatar(user.getAvatar());
            userInfo.setRoles(roleNames);
            userInfo.setLastLoginTime(user.getLastLoginTime());

            // 构建响应
            LoginResponse response = new LoginResponse();
            response.setAccessToken(newAccessToken);
            response.setRefreshToken(refreshToken); // 保持原刷新令牌
            response.setExpiresIn(jwtExpiration);
            response.setUserInfo(userInfo);

            log.debug("刷新令牌成功: {}", username);
            return response;

        } catch (Exception e) {
            log.error("刷新令牌失败: {}", e.getMessage());
            throw new UnauthorizedException("刷新令牌失败");
        }
    }

    @Override
    public void logout(String token) {
        // 在实际项目中，这里可以将令牌加入黑名单
        // 或者在Redis中记录已登出的令牌
        if (StringUtils.hasText(token)) {
            try {
                String username = jwtUtil.getUsernameFromToken(token);
                log.info("用户登出: {}", username);
            } catch (Exception e) {
                log.debug("解析登出令牌失败: {}", e.getMessage());
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changePassword(Long userId, ChangePasswordRequest request) {
        // 验证新密码确认
        if (!Objects.equals(request.getNewPassword(), request.getConfirmNewPassword())) {
            throw new ValidationException("两次输入的新密码不一致");
        }

        // 验证原密码并修改密码
        boolean success = userService.changePassword(userId, 
                request.getOldPassword(), request.getNewPassword());
        
        if (!success) {
            throw new BusinessException("原密码错误或修改失败");
        }

        log.info("用户修改密码成功，用户ID: {}", userId);
    }

    @Override
    public LoginResponse.UserInfo getCurrentUser(Long userId) {
        User user = userService.getUserById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 获取用户角色
        List<Role> roles = userService.getUserRoles(user.getId());
        List<String> roleNames = roles.stream()
                .map(Role::getCode)
                .collect(Collectors.toList());

        // 构建用户信息
        LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo();
        userInfo.setUserId(user.getId());
        userInfo.setUsername(user.getUsername());
        userInfo.setNickname(user.getNickname());
        userInfo.setEmail(user.getEmail());
        userInfo.setAvatar(user.getAvatar());
        userInfo.setRoles(roleNames);
        userInfo.setLastLoginTime(user.getLastLoginTime());

        return userInfo;
    }

    @Override
    public boolean isUsernameAvailable(String username) {
        return !userService.existsByUsername(username);
    }

    @Override
    public boolean isEmailAvailable(String email) {
        return !userService.existsByEmail(email);
    }
}