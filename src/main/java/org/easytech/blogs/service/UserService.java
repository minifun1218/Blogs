package org.easytech.blogs.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.easytech.blogs.entity.Role;
import org.easytech.blogs.entity.User;

import java.util.List;

/**
 * 用户服务接口
 * 提供用户相关的业务逻辑处理
 */
public interface UserService {

    /**
     * 根据用户名查询用户
     * @param username 用户名
     * @return 用户信息
     */
    User findByUsername(String username);

    /**
     * 根据邮箱查询用户
     * @param email 邮箱
     * @return 用户信息
     */
    User findByEmail(String email);

    /**
     * 用户注册
     * @param user 用户信息
     * @return 注册后的用户信息
     */
    User register(User user);

    /**
     * 用户登录验证
     * @param username 用户名
     * @param password 密码
     * @return 用户信息，登录失败返回null
     */
    User login(String username, String password);

    /**
     * 更新用户信息
     * @param user 用户信息
     * @return 更新结果
     */
    boolean updateUser(User user);

    /**
     * 根据ID更新用户
     * @param user 用户信息
     * @return 更新结果
     */
    boolean updateById(User user);

    /**
     * 修改密码
     * @param userId 用户ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 修改结果
     */
    boolean changePassword(Long userId, String oldPassword, String newPassword);

    /**
     * 重置密码
     * @param userId 用户ID
     * @param newPassword 新密码
     * @return 重置结果
     */
    boolean resetPassword(Long userId, String newPassword);

    /**
     * 分页查询用户列表
     * @param page 分页参数
     * @param keyword 搜索关键词
     * @return 用户分页列表
     */
    Page<User> getUserPage(Page<User> page, String keyword);

    /**
     * 分页查询用户列表（重载方法）
     * @param page 分页参数
     * @return 用户分页列表
     */
    default IPage<User> getUserPage(Page<User> page) {
        return getUserPage(page, null);
    }

    /**
     * 根据角色查询用户
     * @param roleCode 角色编码
     * @return 用户列表
     */
    List<User> findByRoleCode(String roleCode);

    /**
     * 更新用户状态
     * @param userId 用户ID
     * @param status 状态
     * @return 更新结果
     */
    boolean updateUserStatus(Long userId, Integer status);

    /**
     * 删除用户（逻辑删除）
     * @param userId 用户ID
     * @return 删除结果
     */
    boolean removeById(Long userId);

    /**
     * 检查用户名是否存在
     * @param username 用户名
     * @param excludeUserId 排除的用户ID
     * @return 是否存在
     */
    boolean isUsernameExists(String username, Long excludeUserId);

    /**
     * 检查邮箱是否存在
     * @param email 邮箱
     * @param excludeUserId 排除的用户ID
     * @return 是否存在
     */
    boolean isEmailExists(String email, Long excludeUserId);

    /**
     * 为用户分配角色
     * @param userId 用户ID
     * @param roleIds 角色ID列表
     * @return 分配结果
     */
    boolean assignRoles(Long userId, List<Long> roleIds);

    /**
     * 获取用户角色列表
     * @param userId 用户ID
     * @return 角色列表
     */
    List<Role> getUserRoles(Long userId);

    /**
     * 更新用户最后登录时间
     * @param userId 用户ID
     * @return 更新结果
     */
    boolean updateLastLoginTime(Long userId);

    /**
     * 根据昵称搜索用户
     * @param nickname 昵称关键词
     * @return 用户列表
     */
    List<User> searchByNickname(String nickname);

    /**
     * 统计用户数量
     * @param status 用户状态，null表示所有状态
     * @return 用户数量
     */
    Long countUsers(Integer status);

    /**
     * 根据ID获取用户
     * @param userId 用户ID
     * @return 用户信息
     */
    User getUserById(Long userId);

    /**
     * 根据ID获取用户（别名方法）
     * @param userId 用户ID
     * @return 用户信息
     */
    default User getById(Long userId) {
        return getUserById(userId);
    }

    /**
     * 检查用户名是否存在（别名方法）
     * @param username 用户名
     * @return 是否存在
     */
    default boolean existsByUsername(String username) {
        return isUsernameExists(username, null);
    }

    /**
     * 检查邮箱是否存在（别名方法）
     * @param email 邮箱
     * @return 是否存在
     */
    default boolean existsByEmail(String email) {
        return isEmailExists(email, null);
    }

    /**
     * 更新用户信息（别名方法）
     * @param userId 用户ID
     * @param nickname 昵称
     * @param email 邮箱
     * @param bio 个人简介
     * @return 更新结果
     */
    default boolean updateUserInfo(Long userId, String nickname, String email, String bio) {
        User user = getUserById(userId);
        if (user == null) {
            return false;
        }
        if (nickname != null) user.setNickname(nickname);
        if (email != null) user.setEmail(email);
        if (bio != null) user.setBio(bio);
        return updateUser(user);
    }
}