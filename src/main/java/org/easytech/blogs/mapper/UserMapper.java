package org.easytech.blogs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.easytech.blogs.entity.Role;
import org.easytech.blogs.entity.User;

import java.util.List;

/**
 * 用户Mapper接口
 */
public interface UserMapper extends BaseMapper<User> {

    /**
     * 根据用户名查询用户
     */
    @Select("SELECT * FROM tb_user WHERE username = #{username} AND is_deleted = 0")
    User findByUsername(@Param("username") String username);

    /**
     * 根据邮箱查询用户
     */
    @Select("SELECT * FROM tb_user WHERE email = #{email} AND is_deleted = 0")
    User findByEmail(@Param("email") String email);

    /**
     * 分页查询用户列表
     */
    @Select("SELECT u.*, GROUP_CONCAT(r.name) as role_names " +
            "FROM tb_user u " +
            "LEFT JOIN tb_user_role ur ON u.id = ur.user_id " +
            "LEFT JOIN tb_role r ON ur.role_id = r.id " +
            "WHERE u.is_deleted = 0 " +
            "GROUP BY u.id " +
            "ORDER BY u.create_time DESC")
    IPage<User> selectUserPage(Page<User> page);

    /**
     * 根据角色查询用户
     */
    @Select("SELECT u.* FROM tb_user u " +
            "INNER JOIN tb_user_role ur ON u.id = ur.user_id " +
            "INNER JOIN tb_role r ON ur.role_id = r.id " +
            "WHERE r.code = #{roleCode} AND u.is_deleted = 0")
    List<User> findByRoleCode(@Param("roleCode") String roleCode);

    /**
     * 统计用户数量
     */
    @Select("SELECT COUNT(*) FROM tb_user WHERE status = #{status} AND is_deleted = 0")
    Long countByStatus(@Param("status") Integer status);

    /**
     * 检查用户名是否存在
     * @param username 用户名
     * @param excludeUserId 排除的用户ID（用于更新时检查）
     * @return 是否存在
     */
    @Select("SELECT COUNT(*) > 0 FROM tb_user WHERE username = #{username} " +
            "AND (#{excludeUserId} IS NULL OR id != #{excludeUserId}) AND is_deleted = 0")
    boolean existsByUsername(@Param("username") String username, @Param("excludeUserId") Long excludeUserId);

    /**
     * 检查邮箱是否存在
     * @param email 邮箱
     * @param excludeUserId 排除的用户ID（用于更新时检查）
     * @return 是否存在
     */
    @Select("SELECT COUNT(*) > 0 FROM tb_user WHERE email = #{email} " +
            "AND (#{excludeUserId} IS NULL OR id != #{excludeUserId}) AND is_deleted = 0")
    boolean existsByEmail(@Param("email") String email, @Param("excludeUserId") Long excludeUserId);

    /**
     * 更新用户状态
     * @param userId 用户ID
     * @param status 用户状态
     * @return 更新影响行数
     */
    @Update("UPDATE tb_user SET status = #{status}, update_time = NOW() WHERE id = #{userId}")
    int updateUserStatus(@Param("userId") Long userId, @Param("status") Integer status);

    /**
     * 更新用户最后登录时间
     * @param userId 用户ID
     * @return 更新影响行数
     */
    @Update("UPDATE tb_user SET last_login_time = NOW(), update_time = NOW() WHERE id = #{userId}")
    int updateLastLoginTime(@Param("userId") Long userId);

    /**
     * 根据昵称模糊查询用户
     * @param nickname 昵称关键词
     * @return 用户列表
     */
    @Select("SELECT * FROM tb_user WHERE nickname LIKE CONCAT('%', #{nickname}, '%') " +
            "AND status = 1 AND is_deleted = 0 ORDER BY create_time DESC")
    List<User> searchByNickname(@Param("nickname") String nickname);

    /**
     * 分页查询用户列表（支持关键词搜索）
     * @param page 分页参数
     * @param keyword 搜索关键词
     * @return 用户分页列表
     */
    @Select("SELECT u.*, GROUP_CONCAT(r.name) as role_names " +
            "FROM tb_user u " +
            "LEFT JOIN tb_user_role ur ON u.id = ur.user_id " +
            "LEFT JOIN tb_role r ON ur.role_id = r.id " +
            "WHERE u.is_deleted = 0 " +
            "AND (#{keyword} IS NULL OR #{keyword} = '' OR " +
            "u.username LIKE CONCAT('%', #{keyword}, '%') OR " +
            "u.nickname LIKE CONCAT('%', #{keyword}, '%') OR " +
            "u.email LIKE CONCAT('%', #{keyword}, '%')) " +
            "GROUP BY u.id " +
            "ORDER BY u.create_time DESC")
    IPage<User> selectUserPageWithKeyword(Page<User> page, @Param("keyword") String keyword);

    /**
     * 获取用户角色列表
     * @param userId 用户ID
     * @return 角色列表
     */
    @Select("SELECT r.* FROM tb_role r " +
            "INNER JOIN tb_user_role ur ON r.id = ur.role_id " +
            "WHERE ur.user_id = #{userId} AND r.is_deleted = 0")
    List<Role> getUserRoles(@Param("userId") Long userId);
}
