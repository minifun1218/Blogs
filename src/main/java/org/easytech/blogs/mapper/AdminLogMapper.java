package org.easytech.blogs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.easytech.blogs.entity.AdminLog;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 管理员日志Mapper接口
 * 负责管理员操作日志的数据访问操作
 */
@Mapper
public interface AdminLogMapper extends BaseMapper<AdminLog> {

    /**
     * 分页查询管理员日志
     * @param page 分页对象
     * @return 管理员日志分页列表
     */
    @Select("SELECT al.*, u.username, u.nickname " +
            "FROM tb_admin_log al " +
            "LEFT JOIN tb_user u ON al.admin_id = u.id " +
            "ORDER BY al.create_time DESC")
    IPage<AdminLog> selectAdminLogPage(Page<AdminLog> page);

    /**
     * 根据管理员ID查询日志
     * @param page 分页对象
     * @param adminId 管理员ID
     * @return 管理员日志分页列表
     */
    @Select("SELECT al.*, u.username, u.nickname " +
            "FROM tb_admin_log al " +
            "LEFT JOIN tb_user u ON al.admin_id = u.id " +
            "WHERE al.admin_id = #{adminId} " +
            "ORDER BY al.create_time DESC")
    IPage<AdminLog> selectLogsByAdminId(Page<AdminLog> page, @Param("adminId") Long adminId);

    /**
     * 根据操作类型查询日志
     * @param page 分页对象
     * @param operationType 操作类型
     * @return 管理员日志分页列表
     */
    @Select("SELECT al.*, u.username, u.nickname " +
            "FROM tb_admin_log al " +
            "LEFT JOIN tb_user u ON al.admin_id = u.id " +
            "WHERE al.operation_type = #{operationType} " +
            "ORDER BY al.create_time DESC")
    IPage<AdminLog> selectLogsByOperationType(Page<AdminLog> page, @Param("operationType") Integer operationType);

    /**
     * 根据时间范围查询日志
     * @param page 分页对象
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 管理员日志分页列表
     */
    @Select("SELECT al.*, u.username, u.nickname " +
            "FROM tb_admin_log al " +
            "LEFT JOIN tb_user u ON al.admin_id = u.id " +
            "WHERE al.create_time BETWEEN #{startTime} AND #{endTime} " +
            "ORDER BY al.create_time DESC")
    IPage<AdminLog> selectLogsByTimeRange(Page<AdminLog> page, 
                                         @Param("startTime") LocalDateTime startTime, 
                                         @Param("endTime") LocalDateTime endTime);

    /**
     * 统计管理员操作次数
     * @param adminId 管理员ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 操作次数
     */
    @Select("SELECT COUNT(*) FROM tb_admin_log " +
            "WHERE admin_id = #{adminId} AND create_time BETWEEN #{startTime} AND #{endTime}")
    Long countOperationsByAdmin(@Param("adminId") Long adminId, 
                               @Param("startTime") LocalDateTime startTime, 
                               @Param("endTime") LocalDateTime endTime);

    /**
     * 统计各类型操作数量
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 操作类型统计列表
     */
    @Select("SELECT operation_type, COUNT(*) as operation_count " +
            "FROM tb_admin_log " +
            "WHERE create_time BETWEEN #{startTime} AND #{endTime} " +
            "GROUP BY operation_type " +
            "ORDER BY operation_count DESC")
    List<AdminLog> statisticsOperationTypes(@Param("startTime") LocalDateTime startTime, 
                                           @Param("endTime") LocalDateTime endTime);

    /**
     * 清理指定时间之前的日志
     * @param beforeTime 清理时间点
     * @return 清理影响行数
     */
    @Select("DELETE FROM tb_admin_log WHERE create_time < #{beforeTime}")
    int cleanLogsBefore(@Param("beforeTime") LocalDateTime beforeTime);

    /**
     * 根据IP地址查询日志
     * @param page 分页对象
     * @param ipAddress IP地址
     * @return 管理员日志分页列表
     */
    @Select("SELECT al.*, u.username, u.nickname " +
            "FROM tb_admin_log al " +
            "LEFT JOIN tb_user u ON al.admin_id = u.id " +
            "WHERE al.ip_address = #{ipAddress} " +
            "ORDER BY al.create_time DESC")
    IPage<AdminLog> selectLogsByIpAddress(Page<AdminLog> page, @Param("ipAddress") String ipAddress);
}