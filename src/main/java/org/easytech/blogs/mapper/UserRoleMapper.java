package org.easytech.blogs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.easytech.blogs.entity.UserRole;

import java.util.List;

/**
 * 用户角色关联Mapper接口
 * 负责用户角色关联关系的数据访问操作
 */
@Mapper
public interface UserRoleMapper extends BaseMapper<UserRole> {

    /**
     * 根据用户ID删除用户角色关联
     * @param userId 用户ID
     * @return 删除影响行数
     */
    @Delete("DELETE FROM tb_user_role WHERE user_id = #{userId}")
    int deleteByUserId(@Param("userId") Long userId);

    /**
     * 根据角色ID删除用户角色关联
     * @param roleId 角色ID
     * @return 删除影响行数
     */
    @Delete("DELETE FROM tb_user_role WHERE role_id = #{roleId}")
    int deleteByRoleId(@Param("roleId") Long roleId);

    /**
     * 根据用户ID查询角色ID列表
     * @param userId 用户ID
     * @return 角色ID列表
     */
    @Select("SELECT role_id FROM tb_user_role WHERE user_id = #{userId}")
    List<Long> selectRoleIdsByUserId(@Param("userId") Long userId);

    /**
     * 根据角色ID查询用户ID列表
     * @param roleId 角色ID
     * @return 用户ID列表
     */
    @Select("SELECT user_id FROM tb_user_role WHERE role_id = #{roleId}")
    List<Long> selectUserIdsByRoleId(@Param("roleId") Long roleId);

    /**
     * 检查用户是否拥有指定角色
     * @param userId 用户ID
     * @param roleId 角色ID
     * @return 是否拥有角色
     */
    @Select("SELECT COUNT(*) > 0 FROM tb_user_role WHERE user_id = #{userId} AND role_id = #{roleId}")
    boolean hasRole(@Param("userId") Long userId, @Param("roleId") Long roleId);

    /**
     * 检查用户是否拥有指定角色编码
     * @param userId 用户ID
     * @param roleCode 角色编码
     * @return 是否拥有角色
     */
    @Select("SELECT COUNT(*) > 0 FROM tb_user_role ur " +
            "INNER JOIN tb_role r ON ur.role_id = r.id " +
            "WHERE ur.user_id = #{userId} AND r.code = #{roleCode} AND r.is_deleted = 0")
    boolean hasRoleByCode(@Param("userId") Long userId, @Param("roleCode") String roleCode);

    /**
     * 统计角色下的用户数量
     * @param roleId 角色ID
     * @return 用户数量
     */
    @Select("SELECT COUNT(*) FROM tb_user_role ur " +
            "INNER JOIN tb_user u ON ur.user_id = u.id " +
            "WHERE ur.role_id = #{roleId} AND u.is_deleted = 0")
    Long countUsersByRoleId(@Param("roleId") Long roleId);
}