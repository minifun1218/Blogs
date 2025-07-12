package org.easytech.blogs.service;

import org.easytech.blogs.entity.Role;

import java.util.List;

/**
 * 角色服务接口
 * 提供角色相关的业务逻辑处理
 */
public interface RoleService {

    /**
     * 创建角色
     * @param role 角色信息
     * @return 创建结果
     */
    boolean createRole(Role role);

    /**
     * 更新角色
     * @param role 角色信息
     * @return 更新结果
     */
    boolean updateRole(Role role);

    /**
     * 删除角色
     * @param roleId 角色ID
     * @return 删除结果
     */
    boolean deleteRole(Long roleId);

    /**
     * 根据ID获取角色
     * @param roleId 角色ID
     * @return 角色信息
     */
    Role getRoleById(Long roleId);

    /**
     * 根据角色编码获取角色
     * @param code 角色编码
     * @return 角色信息
     */
    Role getRoleByCode(String code);

    /**
     * 根据角色名称获取角色
     * @param name 角色名称
     * @return 角色信息
     */
    Role getRoleByName(String name);

    /**
     * 获取所有角色
     * @return 角色列表
     */
    List<Role> getAllRoles();

    /**
     * 根据用户ID获取用户角色列表
     * @param userId 用户ID
     * @return 用户角色列表
     */
    List<Role> getRolesByUserId(Long userId);

    /**
     * 根据状态获取角色列表
     * @param status 角色状态
     * @return 角色列表
     */
    List<Role> getRolesByStatus(Integer status);

    /**
     * 检查角色编码是否存在
     * @param code 角色编码
     * @param excludeId 排除的角色ID
     * @return 是否存在
     */
    boolean isRoleCodeExists(String code, Long excludeId);

    /**
     * 检查角色名称是否存在
     * @param name 角色名称
     * @param excludeId 排除的角色ID
     * @return 是否存在
     */
    boolean isRoleNameExists(String name, Long excludeId);

    /**
     * 更新角色状态
     * @param roleId 角色ID
     * @param status 角色状态
     * @return 更新结果
     */
    boolean updateRoleStatus(Long roleId, Integer status);

    /**
     * 统计角色下的用户数量
     * @param roleId 角色ID
     * @return 用户数量
     */
    Long countUsersByRoleId(Long roleId);

    /**
     * 获取启用的角色列表
     * @return 启用的角色列表
     */
    List<Role> getEnabledRoles();

    /**
     * 初始化默认角色
     * @return 初始化结果
     */
    boolean initDefaultRoles();
}