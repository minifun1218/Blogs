package org.easytech.blogs.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.easytech.blogs.entity.Role;
import org.easytech.blogs.exception.BusinessException;
import org.easytech.blogs.exception.ResourceNotFoundException;
import org.easytech.blogs.exception.ValidationException;
import org.easytech.blogs.mapper.RoleMapper;
import org.easytech.blogs.mapper.UserRoleMapper;
import org.easytech.blogs.service.RoleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 角色服务实现类
 * 实现角色相关的业务逻辑处理
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleMapper roleMapper;
    private final UserRoleMapper userRoleMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createRole(Role role) {
        // 参数校验
        if (role == null || !StringUtils.hasText(role.getName()) || !StringUtils.hasText(role.getCode())) {
            throw new ValidationException("角色名称和编码不能为空");
        }

        // 检查角色编码是否已存在
        if (isRoleCodeExists(role.getCode(), null)) {
            throw new BusinessException("角色编码已存在");
        }

        // 检查角色名称是否已存在
        if (isRoleNameExists(role.getName(), null)) {
            throw new BusinessException("角色名称已存在");
        }

        try {
            // 设置默认值
            if (role.getStatus() == null) {
                role.setStatus(1); // 默认启用
            }
            if (role.getSortOrder() == null) {
                role.setSortOrder(0);
            }

            int result = roleMapper.insert(role);
            if (result > 0) {
                log.info("角色创建成功，角色名称: {}, 角色编码: {}", role.getName(), role.getCode());
                return true;
            }
        } catch (Exception e) {
            log.error("角色创建失败，角色名称: {}", role.getName(), e);
            throw new BusinessException("角色创建失败，请稍后重试");
        }

        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateRole(Role role) {
        if (role == null || role.getId() == null) {
            throw new ValidationException("角色ID不能为空");
        }

        // 检查角色是否存在
        Role existRole = roleMapper.selectById(role.getId());
        if (existRole == null) {
            throw new ResourceNotFoundException("角色不存在");
        }

        // 检查角色编码是否已存在（排除当前角色）
        if (StringUtils.hasText(role.getCode()) && 
            isRoleCodeExists(role.getCode(), role.getId())) {
            throw new BusinessException("角色编码已存在");
        }

        // 检查角色名称是否已存在（排除当前角色）
        if (StringUtils.hasText(role.getName()) && 
            isRoleNameExists(role.getName(), role.getId())) {
            throw new BusinessException("角色名称已存在");
        }

        try {
            int result = roleMapper.updateById(role);
            if (result > 0) {
                log.info("角色更新成功，角色ID: {}", role.getId());
                return true;
            }
        } catch (Exception e) {
            log.error("角色更新失败，角色ID: {}", role.getId(), e);
            throw new BusinessException("角色更新失败，请稍后重试");
        }

        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteRole(Long roleId) {
        if (roleId == null) {
            throw new ValidationException("角色ID不能为空");
        }

        Role role = roleMapper.selectById(roleId);
        if (role == null) {
            throw new ResourceNotFoundException("角色不存在");
        }

        // 检查是否有用户使用该角色
        Long userCount = userRoleMapper.countUsersByRoleId(roleId);
        if (userCount > 0) {
            throw new BusinessException("该角色下还有用户，不能删除");
        }

        try {
            int result = roleMapper.deleteById(roleId);
            if (result > 0) {
                log.info("角色删除成功，角色ID: {}", roleId);
                return true;
            }
        } catch (Exception e) {
            log.error("角色删除失败，角色ID: {}", roleId, e);
            throw new BusinessException("角色删除失败，请稍后重试");
        }

        return false;
    }

    @Override
    public Role getRoleById(Long roleId) {
        if (roleId == null) {
            return null;
        }
        return roleMapper.selectById(roleId);
    }

    @Override
    public Role getRoleByCode(String code) {
        if (!StringUtils.hasText(code)) {
            return null;
        }
        return roleMapper.selectByCode(code);
    }

    @Override
    public Role getRoleByName(String name) {
        if (!StringUtils.hasText(name)) {
            return null;
        }
        return roleMapper.selectByName(name);
    }

    @Override
    public List<Role> getAllRoles() {
        return roleMapper.selectAllRoles();
    }

    @Override
    public List<Role> getRolesByUserId(Long userId) {
        if (userId == null) {
            return List.of();
        }
        return roleMapper.selectRolesByUserId(userId);
    }

    @Override
    public List<Role> getRolesByStatus(Integer status) {
        if (status == null) {
            return getAllRoles();
        }
        return roleMapper.selectRolesByStatus(status);
    }

    @Override
    public boolean isRoleCodeExists(String code, Long excludeId) {
        if (!StringUtils.hasText(code)) {
            return false;
        }

        Role existRole = roleMapper.selectByCode(code);
        if (existRole == null) {
            return false;
        }

        // 如果是更新操作，排除当前角色
        if (excludeId != null && existRole.getId().equals(excludeId)) {
            return false;
        }

        return true;
    }

    @Override
    public boolean isRoleNameExists(String name, Long excludeId) {
        if (!StringUtils.hasText(name)) {
            return false;
        }

        Role existRole = roleMapper.selectByName(name);
        if (existRole == null) {
            return false;
        }

        // 如果是更新操作，排除当前角色
        if (excludeId != null && existRole.getId().equals(excludeId)) {
            return false;
        }

        return true;
    }

    @Override
    public boolean updateRoleStatus(Long roleId, Integer status) {
        if (roleId == null || status == null) {
            throw new ValidationException("参数不能为空");
        }

        Role role = new Role();
        role.setId(roleId);
        role.setStatus(status);

        try {
            int result = roleMapper.updateById(role);
            if (result > 0) {
                log.info("角色状态更新成功，角色ID: {}, 状态: {}", roleId, status);
                return true;
            }
        } catch (Exception e) {
            log.error("角色状态更新失败，角色ID: {}", roleId, e);
            throw new BusinessException("角色状态更新失败");
        }

        return false;
    }

    @Override
    public Long countUsersByRoleId(Long roleId) {
        if (roleId == null) {
            return 0L;
        }
        return userRoleMapper.countUsersByRoleId(roleId);
    }

    @Override
    public List<Role> getEnabledRoles() {
        return getRolesByStatus(1);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean initDefaultRoles() {
        try {
            // 检查是否已存在默认角色
            if (roleMapper.selectByCode("ADMIN") == null) {
                Role adminRole = new Role();
                adminRole.setName("系统管理员");
                adminRole.setCode("ADMIN");
                adminRole.setDescription("系统管理员，拥有所有权限");
                adminRole.setStatus(1);
                adminRole.setSortOrder(1);
                roleMapper.insert(adminRole);
                log.info("创建默认管理员角色");
            }

            if (roleMapper.selectByCode("USER") == null) {
                Role userRole = new Role();
                userRole.setName("普通用户");
                userRole.setCode("USER");
                userRole.setDescription("普通用户，基础权限");
                userRole.setStatus(1);
                userRole.setSortOrder(2);
                roleMapper.insert(userRole);
                log.info("创建默认用户角色");
            }

            if (roleMapper.selectByCode("AUTHOR") == null) {
                Role authorRole = new Role();
                authorRole.setName("作者");
                authorRole.setCode("AUTHOR");
                authorRole.setDescription("作者，可以发布和管理自己的文章");
                authorRole.setStatus(1);
                authorRole.setSortOrder(3);
                roleMapper.insert(authorRole);
                log.info("创建默认作者角色");
            }

            log.info("默认角色初始化完成");
            return true;
        } catch (Exception e) {
            log.error("默认角色初始化失败", e);
            throw new BusinessException("默认角色初始化失败");
        }
    }
}