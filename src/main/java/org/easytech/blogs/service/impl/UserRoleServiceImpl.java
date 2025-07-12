package org.easytech.blogs.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.easytech.blogs.entity.UserRole;
import org.easytech.blogs.exception.BusinessException;
import org.easytech.blogs.exception.ValidationException;
import org.easytech.blogs.mapper.RoleMapper;
import org.easytech.blogs.mapper.UserMapper;
import org.easytech.blogs.mapper.UserRoleMapper;
import org.easytech.blogs.service.UserRoleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 用户角色关联服务实现类
 * 实现用户角色关联相关的业务逻辑处理
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserRoleServiceImpl implements UserRoleService {

    private final UserRoleMapper userRoleMapper;
    private final UserMapper userMapper;
    private final RoleMapper roleMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean assignRole(Long userId, Long roleId) {
        if (userId == null || roleId == null) {
            throw new ValidationException("用户ID和角色ID不能为空");
        }

        // 检查用户是否存在
        if (userMapper.selectById(userId) == null) {
            throw new BusinessException("用户不存在");
        }

        // 检查角色是否存在
        if (roleMapper.selectById(roleId) == null) {
            throw new BusinessException("角色不存在");
        }

        // 检查是否已存在关联
        if (hasRole(userId, roleId)) {
            return true; // 已存在关联
        }

        try {
            UserRole userRole = new UserRole();
            userRole.setUserId(userId);
            userRole.setRoleId(roleId);

            int result = userRoleMapper.insert(userRole);
            if (result > 0) {
                log.info("用户角色分配成功，用户ID: {}, 角色ID: {}", userId, roleId);
                return true;
            }
        } catch (Exception e) {
            log.error("用户角色分配失败，用户ID: {}, 角色ID: {}", userId, roleId, e);
            throw new BusinessException("用户角色分配失败");
        }

        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean assignRoles(Long userId, List<Long> roleIds) {
        if (userId == null || roleIds == null || roleIds.isEmpty()) {
            throw new ValidationException("用户ID和角色ID列表不能为空");
        }

        try {
            int successCount = 0;
            for (Long roleId : roleIds) {
                if (assignRole(userId, roleId)) {
                    successCount++;
                }
            }

            log.info("批量分配用户角色完成，用户ID: {}, 成功分配: {}/{}", userId, successCount, roleIds.size());
            return successCount > 0;
        } catch (Exception e) {
            log.error("批量分配用户角色失败，用户ID: {}", userId, e);
            throw new BusinessException("批量分配用户角色失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean setUserRoles(Long userId, List<Long> roleIds) {
        if (userId == null) {
            throw new ValidationException("用户ID不能为空");
        }

        try {
            // 先删除原有角色
            removeAllUserRoles(userId);

            // 分配新角色
            if (roleIds != null && !roleIds.isEmpty()) {
                return assignRoles(userId, roleIds);
            }

            return true;
        } catch (Exception e) {
            log.error("设置用户角色失败，用户ID: {}", userId, e);
            throw new BusinessException("设置用户角色失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeUserRole(Long userId, Long roleId) {
        if (userId == null || roleId == null) {
            throw new ValidationException("用户ID和角色ID不能为空");
        }

        try {
            QueryWrapper<UserRole> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", userId).eq("role_id", roleId);

            int result = userRoleMapper.delete(queryWrapper);
            if (result > 0) {
                log.info("用户角色移除成功，用户ID: {}, 角色ID: {}", userId, roleId);
                return true;
            }
        } catch (Exception e) {
            log.error("用户角色移除失败，用户ID: {}, 角色ID: {}", userId, roleId, e);
            throw new BusinessException("用户角色移除失败");
        }

        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeAllUserRoles(Long userId) {
        if (userId == null) {
            throw new ValidationException("用户ID不能为空");
        }

        try {
            int result = userRoleMapper.deleteByUserId(userId);
            log.info("用户所有角色移除成功，用户ID: {}, 移除数量: {}", userId, result);
            return true;
        } catch (Exception e) {
            log.error("用户所有角色移除失败，用户ID: {}", userId, e);
            throw new BusinessException("用户所有角色移除失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeAllRoleUsers(Long roleId) {
        if (roleId == null) {
            throw new ValidationException("角色ID不能为空");
        }

        try {
            int result = userRoleMapper.deleteByRoleId(roleId);
            log.info("角色所有用户关联移除成功，角色ID: {}, 移除数量: {}", roleId, result);
            return true;
        } catch (Exception e) {
            log.error("角色所有用户关联移除失败，角色ID: {}", roleId, e);
            throw new BusinessException("角色所有用户关联移除失败");
        }
    }

    @Override
    public List<Long> getRoleIdsByUserId(Long userId) {
        if (userId == null) {
            return List.of();
        }
        return userRoleMapper.selectRoleIdsByUserId(userId);
    }

    @Override
    public List<Long> getUserIdsByRoleId(Long roleId) {
        if (roleId == null) {
            return List.of();
        }
        return userRoleMapper.selectUserIdsByRoleId(roleId);
    }

    @Override
    public boolean hasRole(Long userId, Long roleId) {
        if (userId == null || roleId == null) {
            return false;
        }
        return userRoleMapper.hasRole(userId, roleId);
    }

    @Override
    public boolean hasRoleByCode(Long userId, String roleCode) {
        if (userId == null || !StringUtils.hasText(roleCode)) {
            return false;
        }
        return userRoleMapper.hasRoleByCode(userId, roleCode);
    }

    @Override
    public Long countUsersByRoleId(Long roleId) {
        if (roleId == null) {
            return 0L;
        }
        return userRoleMapper.countUsersByRoleId(roleId);
    }

    @Override
    public Long countRolesByUserId(Long userId) {
        if (userId == null) {
            return 0L;
        }

        QueryWrapper<UserRole> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        return userRoleMapper.selectCount(queryWrapper);
    }

    @Override
    public List<UserRole> getAllUserRoles() {
        return userRoleMapper.selectList(null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchRemoveUserRoles(List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return true;
        }

        try {
            QueryWrapper<UserRole> queryWrapper = new QueryWrapper<>();
            queryWrapper.in("user_id", userIds);
            int result = userRoleMapper.delete(queryWrapper);

            log.info("批量删除用户角色关联成功，用户数量: {}, 删除关联数量: {}", userIds.size(), result);
            return true;
        } catch (Exception e) {
            log.error("批量删除用户角色关联失败", e);
            throw new BusinessException("批量删除用户角色关联失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchRemoveRoleUsers(List<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return true;
        }

        try {
            QueryWrapper<UserRole> queryWrapper = new QueryWrapper<>();
            queryWrapper.in("role_id", roleIds);
            int result = userRoleMapper.delete(queryWrapper);

            log.info("批量删除角色用户关联成功，角色数量: {}, 删除关联数量: {}", roleIds.size(), result);
            return true;
        } catch (Exception e) {
            log.error("批量删除角色用户关联失败", e);
            throw new BusinessException("批量删除角色用户关联失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int cleanInvalidRelations() {
        try {
            List<UserRole> allRelations = userRoleMapper.selectList(null);
            int cleanCount = 0;

            for (UserRole relation : allRelations) {
                // 检查用户是否存在
                if (userMapper.selectById(relation.getUserId()) == null) {
                    userRoleMapper.deleteById(relation.getId());
                    cleanCount++;
                    continue;
                }

                // 检查角色是否存在
                if (roleMapper.selectById(relation.getRoleId()) == null) {
                    userRoleMapper.deleteById(relation.getId());
                    cleanCount++;
                }
            }

            log.info("清理无效用户角色关联完成，清理数量: {}", cleanCount);
            return cleanCount;
        } catch (Exception e) {
            log.error("清理无效用户角色关联失败", e);
            throw new BusinessException("清理无效用户角色关联失败");
        }
    }

    @Override
    public boolean isAdmin(Long userId) {
        return hasRoleByCode(userId, "ADMIN");
    }

    @Override
    public boolean hasAdminPermission(Long userId) {
        // 检查是否为管理员或其他有管理权限的角色
        return hasRoleByCode(userId, "ADMIN") || hasRoleByCode(userId, "SUPER_ADMIN");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean assignDefaultRole(Long userId) {
        if (userId == null) {
            throw new ValidationException("用户ID不能为空");
        }

        try {
            // 查找默认用户角色
            org.easytech.blogs.entity.Role defaultRole = roleMapper.selectByCode("USER");
            if (defaultRole == null) {
                log.warn("默认用户角色不存在，用户ID: {}", userId);
                return false;
            }

            return assignRole(userId, defaultRole.getId());
        } catch (Exception e) {
            log.error("分配默认角色失败，用户ID: {}", userId, e);
            throw new BusinessException("分配默认角色失败");
        }
    }
}