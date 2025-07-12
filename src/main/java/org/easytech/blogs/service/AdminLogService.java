package org.easytech.blogs.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.easytech.blogs.entity.AdminLog;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 管理员日志服务接口
 * 提供管理员操作日志相关的业务逻辑处理
 */
public interface AdminLogService {

    /**
     * 记录管理员操作日志
     * @param adminId 管理员ID
     * @param operationType 操作类型
     * @param operationContent 操作内容
     * @param targetId 操作目标ID
     * @param ipAddress IP地址
     * @param userAgent 用户代理
     * @return 记录结果
     */
    boolean recordAdminOperation(Long adminId, Integer operationType, String operationContent, 
                               Long targetId, String ipAddress, String userAgent);

    /**
     * 分页查询管理员日志
     * @param page 分页参数
     * @return 管理员日志分页列表
     */
    IPage<AdminLog> getAdminLogPage(Page<AdminLog> page);

    /**
     * 根据管理员ID分页查询日志
     * @param page 分页参数
     * @param adminId 管理员ID
     * @return 管理员日志分页列表
     */
    IPage<AdminLog> getLogsByAdminId(Page<AdminLog> page, Long adminId);

    /**
     * 根据操作类型分页查询日志
     * @param page 分页参数
     * @param operationType 操作类型
     * @return 管理员日志分页列表
     */
    IPage<AdminLog> getLogsByOperationType(Page<AdminLog> page, Integer operationType);

    /**
     * 根据时间范围分页查询日志
     * @param page 分页参数
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 管理员日志分页列表
     */
    IPage<AdminLog> getLogsByTimeRange(Page<AdminLog> page, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 根据IP地址分页查询日志
     * @param page 分页参数
     * @param ipAddress IP地址
     * @return 管理员日志分页列表
     */
    IPage<AdminLog> getLogsByIpAddress(Page<AdminLog> page, String ipAddress);

    /**
     * 统计管理员操作次数
     * @param adminId 管理员ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 操作次数
     */
    Long countOperationsByAdmin(Long adminId, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 统计各类型操作数量
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 操作类型统计列表
     */
    List<AdminLog> statisticsOperationTypes(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 清理指定时间之前的日志
     * @param beforeTime 清理时间点
     * @return 清理的日志数量
     */
    int cleanLogsBefore(LocalDateTime beforeTime);

    /**
     * 记录用户登录日志
     * @param adminId 管理员ID
     * @param ipAddress IP地址
     * @param userAgent 用户代理
     * @param success 登录是否成功
     * @return 记录结果
     */
    boolean recordLoginLog(Long adminId, String ipAddress, String userAgent, boolean success);

    /**
     * 记录用户管理操作
     * @param adminId 管理员ID
     * @param operation 操作类型（CREATE/UPDATE/DELETE）
     * @param targetUserId 目标用户ID
     * @param operationContent 操作内容描述
     * @param ipAddress IP地址
     * @return 记录结果
     */
    boolean recordUserManagement(Long adminId, String operation, Long targetUserId, 
                                String operationContent, String ipAddress);

    /**
     * 记录内容管理操作
     * @param adminId 管理员ID
     * @param operation 操作类型（PUBLISH/DELETE/AUDIT等）
     * @param contentType 内容类型（POST/COMMENT等）
     * @param contentId 内容ID
     * @param operationContent 操作内容描述
     * @param ipAddress IP地址
     * @return 记录结果
     */
    boolean recordContentManagement(Long adminId, String operation, String contentType, 
                                   Long contentId, String operationContent, String ipAddress);

    /**
     * 记录系统配置操作
     * @param adminId 管理员ID
     * @param operation 操作类型（UPDATE）
     * @param configKey 配置键
     * @param oldValue 旧值
     * @param newValue 新值
     * @param ipAddress IP地址
     * @return 记录结果
     */
    boolean recordConfigManagement(Long adminId, String operation, String configKey, 
                                  String oldValue, String newValue, String ipAddress);

    /**
     * 获取管理员今日操作统计
     * @param adminId 管理员ID
     * @return 今日操作次数
     */
    Long getTodayOperationCount(Long adminId);

    /**
     * 获取系统操作热点统计
     * @param days 统计天数
     * @param limit 返回数量限制
     * @return 操作热点统计
     */
    List<AdminLog> getOperationHotspot(int days, int limit);
}