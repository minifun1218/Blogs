package org.easytech.blogs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.easytech.blogs.entity.Role;

import java.util.List;

/**
 * 角色Mapper接口
 * 负责角色信息的数据访问操作
 */
public interface RoleMapper extends BaseMapper<Role> {

    /**
     * 查询所有角色
     * @return 角色列表
     */
    @Select("SELECT * FROM tb_role WHERE is_deleted = 0 ORDER BY sort_order ASC, create_time ASC")
    List<Role> selectAllRoles();

    /**
     * 根据角色编码查询角色
     * @param code 角色编码
     * @return 角色信息
     */
    @Select("SELECT * FROM tb_role WHERE code = #{code} AND is_deleted = 0")
    Role selectByCode(@Param("code") String code);

    /**
     * 根据角色名称查询角色
     * @param name 角色名称
     * @return 角色信息
     */
    @Select("SELECT * FROM tb_role WHERE name = #{name} AND is_deleted = 0")
    Role selectByName(@Param("name") String name);

    /**
     * 根据用户ID查询用户角色列表
     * @param userId 用户ID
     * @return 用户角色列表
     */
    @Select("SELECT r.* FROM tb_role r " +
            "INNER JOIN tb_user_role ur ON r.id = ur.role_id " +
            "WHERE ur.user_id = #{userId} AND r.is_deleted = 0 " +
            "ORDER BY r.sort_order ASC")
    List<Role> selectRolesByUserId(@Param("userId") Long userId);

    /**
     * 查询启用状态的角色
     * @param status 角色状态
     * @return 角色列表
     */
    @Select("SELECT * FROM tb_role WHERE status = #{status} AND is_deleted = 0 ORDER BY sort_order ASC")
    List<Role> selectRolesByStatus(@Param("status") Integer status);
}