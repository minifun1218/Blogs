package org.easytech.blogs.service;

import org.easytech.blogs.entity.UserRole;

import java.util.List;

/**
 * 用户角色关联服务接口
 * 提供用户角色关联相关的业务逻辑处理
 */
public interface UserRoleService {

    /**
     * 为用户分配角色
     * @param userId 用户ID
     * @param roleId 角色ID
     * @return 分配结果
     */
    boolean assignRole(Long userId, Long roleId);

    /**
     * 为用户批量分配角色
     * @param userId 用户ID
     * @param roleIds 角色ID列表
     * @return 分配结果
     */
    boolean assignRoles(Long userId, List<Long> roleIds);

    /**
     * 为用户设置角色（先删除原有角色，再分配新角色）
     * @param userId 用户ID
     * @param roleIds 角色ID列表
     * @return 设置结果
     */
    boolean setUserRoles(Long userId, List<Long> roleIds);

    /**
     * 移除用户角色
     * @param userId 用户ID
     * @param roleId 角色ID
     * @return 移除结果
     */
    boolean removeUserRole(Long userId, Long roleId);

    /**
     * 移除用户的所有角色
     * @param userId 用户ID
     * @return 移除结果
     */
    boolean removeAllUserRoles(Long userId);

    /**
     * 移除角色的所有用户关联
     * @param roleId 角色ID
     * @return 移除结果
     */
    boolean removeAllRoleUsers(Long roleId);

    /**
     * 根据用户ID获取角色ID列表
     * @param userId 用户ID
     * @return 角色ID列表
     */
    List<Long> getRoleIdsByUserId(Long userId);

    /**
     * 根据角色ID获取用户ID列表
     * @param roleId 角色ID
     * @return 用户ID列表
     */
    List<Long> getUserIdsByRoleId(Long roleId);

    /**
     * 检查用户是否拥有指定角色
     * @param userId 用户ID
     * @param roleId 角色ID
     * @return 是否拥有角色
     */
    boolean hasRole(Long userId, Long roleId);

    /**
     * 检查用户是否拥有指定角色编码
     * @param userId 用户ID
     * @param roleCode 角色编码
     * @return 是否拥有角色
     */
    boolean hasRoleByCode(Long userId, String roleCode);

    /**
     * 统计角色下的用户数量
     * @param roleId 角色ID
     * @return 用户数量
     */
    Long countUsersByRoleId(Long roleId);

    /**
     * 统计用户拥有的角色数量
     * @param userId 用户ID
     * @return 角色数量
     */
    Long countRolesByUserId(Long userId);

    /**
     * 获取所有用户角色关联记录
     * @return 关联记录列表
     */
    List<UserRole> getAllUserRoles();

    /**
     * 根据用户ID列表批量删除用户角色关联
     * @param userIds 用户ID列表
     * @return 删除结果
     */
    boolean batchRemoveUserRoles(List<Long> userIds);

    /**
     * 根据角色ID列表批量删除用户角色关联
     * @param roleIds 角色ID列表
     * @return 删除结果
     */
    boolean batchRemoveRoleUsers(List<Long> roleIds);

    /**
     * 清理无效的关联记录（用户或角色已被删除）
     * @return 清理的记录数量
     */
    int cleanInvalidRelations();

    /**
     * 检查用户是否为管理员
     * @param userId 用户ID
     * @return 是否为管理员
     */
    boolean isAdmin(Long userId);

    /**
     * 检查用户是否有管理权限
     * @param userId 用户ID
     * @return 是否有管理权限
     */
    boolean hasAdminPermission(Long userId);

    /**
     * 为新用户分配默认角色
     * @param userId 用户ID
     * @return 分配结果
     */
    boolean assignDefaultRole(Long userId);
}