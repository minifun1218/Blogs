package org.easytech.blogs.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.easytech.blogs.entity.Role;
import org.easytech.blogs.entity.User;
import org.easytech.blogs.entity.UserRole;
import org.easytech.blogs.exception.BusinessException;
import org.easytech.blogs.exception.ValidationException;
import org.easytech.blogs.mapper.RoleMapper;
import org.easytech.blogs.mapper.UserMapper;
import org.easytech.blogs.mapper.UserRoleMapper;
import org.easytech.blogs.service.UserService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 用户服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final RoleMapper roleMapper;
    private final UserRoleMapper userRoleMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Cacheable(value = "users", key = "'username:' + #username")
    public User findByUsername(String username) {
        if (!StringUtils.hasText(username)) {
            return null;
        }
        return userMapper.findByUsername(username);
    }

    @Override
    @Cacheable(value = "users", key = "'email:' + #email")
    public User findByEmail(String email) {
        if (!StringUtils.hasText(email)) {
            return null;
        }
        return userMapper.findByEmail(email);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public User register(User user) {
        if (user == null || !StringUtils.hasText(user.getUsername()) || !StringUtils.hasText(user.getPassword())) {
            throw new ValidationException("用户名和密码不能为空");
        }

        if (isUsernameExists(user.getUsername(), null)) {
            throw new BusinessException("用户名已存在");
        }

        if (StringUtils.hasText(user.getEmail()) && isEmailExists(user.getEmail(), null)) {
            throw new BusinessException("邮箱已存在");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        if (user.getStatus() == null) {
            user.setStatus(1);
        }

        try {
            int result = userMapper.insert(user);
            if (result > 0) {
                assignDefaultRole(user.getId());
                log.info("用户注册成功，用户ID: {}", user.getId());
                return user;
            } else {
                throw new BusinessException("用户注册失败");
            }
        } catch (Exception e) {
            log.error("用户注册失败: {}", e.getMessage(), e);
            throw new BusinessException("用户注册失败: " + e.getMessage());
        }
    }

    @Override
    public User login(String username, String password) {
        if (!StringUtils.hasText(username) || !StringUtils.hasText(password)) {
            throw new ValidationException("用户名和密码不能为空");
        }

        User user = findByUsername(username);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BusinessException("密码错误");
        }

        if (user.getStatus() != 1) {
            throw new BusinessException("用户已被禁用");
        }

        // 更新最后登录时间
        updateLastLoginTime(user.getId());
        
        return user;
    }

    @Override
    @CacheEvict(value = "users", allEntries = true)
    public boolean updateUser(User user) {
        if (user == null || user.getId() == null) {
            throw new ValidationException("用户信息不能为空");
        }

        try {
            int result = userMapper.updateById(user);
            return result > 0;
        } catch (Exception e) {
            log.error("用户信息更新失败: {}", e.getMessage(), e);
            throw new BusinessException("用户信息更新失败");
        }
    }

    @Override
    public boolean updateById(User user) {
        return updateUser(user);
    }

    @Override
    public boolean changePassword(Long userId, String oldPassword, String newPassword) {
        if (userId == null || !StringUtils.hasText(oldPassword) || !StringUtils.hasText(newPassword)) {
            throw new ValidationException("参数不能为空");
        }

        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BusinessException("原密码错误");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        return updateUser(user);
    }

    @Override
    public boolean resetPassword(Long userId, String newPassword) {
        if (userId == null || !StringUtils.hasText(newPassword)) {
            throw new ValidationException("参数不能为空");
        }

        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        return updateUser(user);
    }

    @Override
    public Page<User> getUserPage(Page<User> page, String keyword) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            IPage<User> result = userMapper.selectUserPageWithKeyword(page, keyword.trim());
            // Convert IPage to Page by copying the results
            page.setRecords(result.getRecords());
            page.setTotal(result.getTotal());
            page.setSize(result.getSize());
            page.setCurrent(result.getCurrent());
            return page;
        }
        IPage<User> result = userMapper.selectPage(page, null);
        // Convert IPage to Page by copying the results
        page.setRecords(result.getRecords());
        page.setTotal(result.getTotal());
        page.setSize(result.getSize());
        page.setCurrent(result.getCurrent());
        return page;
    }

    @Override
    public List<User> findByRoleCode(String roleCode) {
        if (!StringUtils.hasText(roleCode)) {
            return List.of();
        }
        return userMapper.findByRoleCode(roleCode);
    }

    @Override
    public boolean updateUserStatus(Long userId, Integer status) {
        if (userId == null || status == null) {
            throw new ValidationException("参数不能为空");
        }

        try {
            int result = userMapper.updateUserStatus(userId, status);
            return result > 0;
        } catch (Exception e) {
            log.error("用户状态更新失败: {}", e.getMessage(), e);
            throw new BusinessException("状态更新失败");
        }
    }

    @Override
    @CacheEvict(value = "users", allEntries = true)
    public boolean removeById(Long userId) {
        if (userId == null) {
            throw new ValidationException("用户ID不能为空");
        }

        try {
            userRoleMapper.deleteByUserId(userId);
            int result = userMapper.deleteById(userId);
            return result > 0;
        } catch (Exception e) {
            log.error("用户删除失败: {}", e.getMessage(), e);
            throw new BusinessException("用户删除失败");
        }
    }

    @Override
    public boolean isUsernameExists(String username, Long excludeUserId) {
        if (!StringUtils.hasText(username)) {
            return false;
        }
        User user = findByUsername(username);
        return user != null && (excludeUserId == null || !user.getId().equals(excludeUserId));
    }

    @Override
    public boolean isEmailExists(String email, Long excludeUserId) {
        if (!StringUtils.hasText(email)) {
            return false;
        }
        User user = findByEmail(email);
        return user != null && (excludeUserId == null || !user.getId().equals(excludeUserId));
    }

    @Override
    public boolean assignRoles(Long userId, List<Long> roleIds) {
        if (userId == null || roleIds == null || roleIds.isEmpty()) {
            throw new ValidationException("参数不能为空");
        }

        try {
            // 先删除原有角色
            userRoleMapper.deleteByUserId(userId);
            
            // 添加新角色
            for (Long roleId : roleIds) {
                UserRole userRole = new UserRole();
                userRole.setUserId(userId);
                userRole.setRoleId(roleId);
                userRoleMapper.insert(userRole);
            }
            return true;
        } catch (Exception e) {
            log.error("分配角色失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public List<Role> getUserRoles(Long userId) {
        if (userId == null) {
            return List.of();
        }
        return userMapper.getUserRoles(userId);
    }

    @Override
    public boolean updateLastLoginTime(Long userId) {
        if (userId == null) {
            return false;
        }
        try {
            int result = userMapper.updateLastLoginTime(userId);
            return result > 0;
        } catch (Exception e) {
            log.warn("更新最后登录时间失败: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public List<User> searchByNickname(String nickname) {
        if (!StringUtils.hasText(nickname)) {
            return List.of();
        }
        return userMapper.searchByNickname(nickname);
    }

    @Override
    public Long countUsers(Integer status) {
        if (status == null) {
            return userMapper.selectCount(null);
        }
        return userMapper.countByStatus(status);
    }

    @Override
    @Cacheable(value = "users", key = "'id:' + #userId")
    public User getUserById(Long userId) {
        if (userId == null) {
            return null;
        }
        return userMapper.selectById(userId);
    }

    /**
     * 分配默认角色
     */
    private void assignDefaultRole(Long userId) {
        try {
            Role defaultRole = roleMapper.selectByCode("USER");
            if (defaultRole != null) {
                UserRole userRole = new UserRole();
                userRole.setUserId(userId);
                userRole.setRoleId(defaultRole.getId());
                userRoleMapper.insert(userRole);
            }
        } catch (Exception e) {
            log.warn("分配默认角色失败，用户ID: {}", userId, e);
        }
    }
}