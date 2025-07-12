package org.easytech.blogs.util;

import lombok.extern.slf4j.Slf4j;
import org.easytech.blogs.security.JwtAuthenticationFilter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 安全工具类
 * 提供当前用户信息获取和权限检查功能
 */
@Slf4j
@Component("securityUtil")
public class SecurityUtil {

    /**
     * 获取当前认证的用户信息
     * @return Authentication对象，未认证时返回null
     */
    public static Authentication getCurrentAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    /**
     * 获取当前用户ID
     * @return 用户ID，未认证时返回null
     */
    public static Long getCurrentUserId() {
        Authentication authentication = getCurrentAuthentication();
        if (authentication instanceof JwtAuthenticationFilter.JwtAuthenticationToken jwtToken) {
            return jwtToken.getUserId();
        }
        return null;
    }

    /**
     * 获取当前用户名
     * @return 用户名，未认证时返回null
     */
    public static String getCurrentUsername() {
        Authentication authentication = getCurrentAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        return null;
    }

    /**
     * 获取当前用户的JWT令牌
     * @return JWT令牌，未认证时返回null
     */
    public static String getCurrentToken() {
        Authentication authentication = getCurrentAuthentication();
        if (authentication instanceof JwtAuthenticationFilter.JwtAuthenticationToken jwtToken) {
            return jwtToken.getToken();
        }
        return null;
    }

    /**
     * 获取当前用户的角色列表
     * @return 角色列表
     */
    public static List<String> getCurrentUserRoles() {
        Authentication authentication = getCurrentAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            return authorities.stream()
                    .map(GrantedAuthority::getAuthority)
                    .map(role -> role.startsWith("ROLE_") ? role.substring(5) : role)
                    .collect(Collectors.toList());
        }
        return List.of();
    }

    /**
     * 检查当前用户是否已认证
     * @return 是否已认证
     */
    public static boolean isAuthenticated() {
        Authentication authentication = getCurrentAuthentication();
        return authentication != null && authentication.isAuthenticated() && 
               !"anonymousUser".equals(authentication.getPrincipal());
    }

    /**
     * 检查当前用户是否具有指定角色
     * @param role 角色名
     * @return 是否具有角色
     */
    public static boolean hasRole(String role) {
        Authentication authentication = getCurrentAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            String roleWithPrefix = role.startsWith("ROLE_") ? role : "ROLE_" + role;
            return authorities.stream()
                    .anyMatch(authority -> authority.getAuthority().equals(roleWithPrefix));
        }
        return false;
    }

    /**
     * 检查当前用户是否具有任一指定角色
     * @param roles 角色列表
     * @return 是否具有任一角色
     */
    public static boolean hasAnyRole(String... roles) {
        for (String role : roles) {
            if (hasRole(role)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查当前用户是否具有所有指定角色
     * @param roles 角色列表
     * @return 是否具有所有角色
     */
    public static boolean hasAllRoles(String... roles) {
        for (String role : roles) {
            if (!hasRole(role)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 检查当前用户是否为管理员
     * @return 是否为管理员
     */
    public static boolean isAdmin() {
        return hasRole("ADMIN");
    }

    /**
     * 检查当前用户是否为编辑者
     * @return 是否为编辑者
     */
    public static boolean isEditor() {
        return hasRole("EDITOR");
    }

    /**
     * 检查当前用户是否为普通用户
     * @return 是否为普通用户
     */
    public static boolean isUser() {
        return hasRole("USER");
    }

    /**
     * 检查指定用户ID是否为当前用户
     * @param userId 用户ID
     * @return 是否为当前用户
     */
    public static boolean isCurrentUser(Long userId) {
        Long currentUserId = getCurrentUserId();
        return currentUserId != null && currentUserId.equals(userId);
    }

    /**
     * 检查当前用户是否可以访问指定用户的资源
     * 管理员可以访问所有用户资源，普通用户只能访问自己的资源
     * @param userId 用户ID
     * @return 是否可以访问
     */
    public static boolean canAccessUserResource(Long userId) {
        return isAdmin() || isCurrentUser(userId);
    }

    /**
     * 检查当前用户是否可以修改指定用户的资源
     * 管理员可以修改所有用户资源，普通用户只能修改自己的资源
     * @param userId 用户ID
     * @return 是否可以修改
     */
    public static boolean canModifyUserResource(Long userId) {
        return isAdmin() || isCurrentUser(userId);
    }
}