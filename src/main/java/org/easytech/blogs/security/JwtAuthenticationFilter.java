package org.easytech.blogs.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.easytech.blogs.util.JwtUtil;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * JWT认证过滤器
 * 拦截请求并验证JWT令牌
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                  HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {

        try {
            String token = extractTokenFromRequest(request);
            
            if (StringUtils.hasText(token) && jwtUtil.validateToken(token)) {
                // 确保是访问令牌，不是刷新令牌
                if (jwtUtil.isAccessToken(token)) {
                    authenticateUser(token, request);
                } else {
                    log.debug("尝试使用刷新令牌进行认证，拒绝访问");
                }
            }
        } catch (Exception e) {
            log.error("JWT认证过程中发生错误: {}", e.getMessage());
            // 不中断过滤器链，让Spring Security处理未认证的请求
        }

        filterChain.doFilter(request, response);
    }

    /**
     * 从请求中提取JWT令牌
     */
    private String extractTokenFromRequest(HttpServletRequest request) {
        // 1. 首先尝试从Authorization头获取
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return jwtUtil.extractToken(bearerToken);
        }

        // 2. 尝试从查询参数获取（用于某些特殊场景，如WebSocket连接）
        String tokenParam = request.getParameter("token");
        if (StringUtils.hasText(tokenParam)) {
            return tokenParam;
        }

        return null;
    }

    /**
     * 对用户进行认证
     */
    private void authenticateUser(String token, HttpServletRequest request) {
        try {
            Claims claims = jwtUtil.parseToken(token);
            
            String username = claims.getSubject();
            Long userId = claims.get("userId", Long.class);
            @SuppressWarnings("unchecked")
            List<String> roles = (List<String>) claims.get("roles");

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // 创建权限列表
                List<SimpleGrantedAuthority> authorities = roles.stream()
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
                        .collect(Collectors.toList());

                // 创建认证主体
                JwtAuthenticationToken authToken = new JwtAuthenticationToken(
                        username, userId, authorities, token);
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 设置认证信息
                SecurityContextHolder.getContext().setAuthentication(authToken);
                
                log.debug("用户 {} (ID: {}) 通过JWT认证，角色: {}", username, userId, roles);
            }
        } catch (Exception e) {
            log.error("JWT认证失败: {}", e.getMessage());
        }
    }

    /**
     * 判断是否需要跳过JWT验证的路径
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        
        // 跳过认证的路径
        return path.startsWith("/api/auth/") ||
               path.startsWith("/api/public/") ||
               path.equals("/api/health") ||
               path.equals("/api/test/") ||
               path.startsWith("/actuator/") ||
               path.startsWith("/swagger-ui/") ||
               path.startsWith("/v3/api-docs/") ||
               path.equals("/favicon.ico") ||
               path.startsWith("/static/") ||
               path.startsWith("/css/") ||
               path.startsWith("/js/") ||
               path.startsWith("/images/");
    }

    /**
     * 自定义认证令牌类
     */
    public static class JwtAuthenticationToken extends UsernamePasswordAuthenticationToken {
        private final Long userId;
        private final String token;

        public JwtAuthenticationToken(String username, Long userId, 
                                    List<SimpleGrantedAuthority> authorities, String token) {
            super(username, null, authorities);
            this.userId = userId;
            this.token = token;
        }

        public Long getUserId() {
            return userId;
        }

        public String getToken() {
            return token;
        }

        @Override
        public Object getCredentials() {
            return token;
        }

        @Override
        public Object getPrincipal() {
            return getName();
        }
    }
}