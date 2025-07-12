package org.easytech.blogs.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.easytech.blogs.entity.AdminLog;
import org.easytech.blogs.exception.BusinessException;
import org.easytech.blogs.exception.ValidationException;
import org.easytech.blogs.mapper.AdminLogMapper;
import org.easytech.blogs.service.AdminLogService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * 管理员日志服务实现类
 * 实现管理员操作日志相关的业务逻辑处理
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminLogServiceImpl implements AdminLogService {

    private final AdminLogMapper adminLogMapper;

    // 操作类型常量
    private static final int OPERATION_TYPE_LOGIN = 1;           // 登录
    private static final int OPERATION_TYPE_USER_MGMT = 2;       // 用户管理
    private static final int OPERATION_TYPE_CONTENT_MGMT = 3;    // 内容管理
    private static final int OPERATION_TYPE_CONFIG_MGMT = 4;     // 配置管理
    private static final int OPERATION_TYPE_SYSTEM = 5;          // 系统操作

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean recordAdminOperation(Long adminId, Integer operationType, String operationContent, 
                                      Long targetId, String ipAddress, String userAgent) {
        if (adminId == null || operationType == null) {
            throw new ValidationException("管理员ID和操作类型不能为空");
        }

        try {
            AdminLog adminLog = new AdminLog();
            adminLog.setAdminId(adminId);
            adminLog.setOperationType(operationType);
            adminLog.setOperationContent(StringUtils.hasText(operationContent) ? operationContent : "管理员操作");
            adminLog.setTargetId(targetId);
            adminLog.setIpAddress(ipAddress);
            adminLog.setUserAgent(userAgent);

            int result = adminLogMapper.insert(adminLog);
            if (result > 0) {
                log.debug("管理员操作日志记录成功，管理员ID: {}, 操作类型: {}", adminId, operationType);
                return true;
            }
        } catch (Exception e) {
            log.error("管理员操作日志记录失败，管理员ID: {}, 操作类型: {}", adminId, operationType, e);
            // 日志记录失败不应该影响主业务，所以这里不抛出异常
        }

        return false;
    }

    @Override
    public IPage<AdminLog> getAdminLogPage(Page<AdminLog> page) {
        return adminLogMapper.selectAdminLogPage(page);
    }

    @Override
    public IPage<AdminLog> getLogsByAdminId(Page<AdminLog> page, Long adminId) {
        if (adminId == null) {
            return page;
        }
        return adminLogMapper.selectLogsByAdminId(page, adminId);
    }

    @Override
    public IPage<AdminLog> getLogsByOperationType(Page<AdminLog> page, Integer operationType) {
        if (operationType == null) {
            return getAdminLogPage(page);
        }
        return adminLogMapper.selectLogsByOperationType(page, operationType);
    }

    @Override
    public IPage<AdminLog> getLogsByTimeRange(Page<AdminLog> page, LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime == null || endTime == null) {
            return getAdminLogPage(page);
        }
        return adminLogMapper.selectLogsByTimeRange(page, startTime, endTime);
    }

    @Override
    public IPage<AdminLog> getLogsByIpAddress(Page<AdminLog> page, String ipAddress) {
        if (!StringUtils.hasText(ipAddress)) {
            return getAdminLogPage(page);
        }
        return adminLogMapper.selectLogsByIpAddress(page, ipAddress);
    }

    @Override
    public Long countOperationsByAdmin(Long adminId, LocalDateTime startTime, LocalDateTime endTime) {
        if (adminId == null || startTime == null || endTime == null) {
            return 0L;
        }
        return adminLogMapper.countOperationsByAdmin(adminId, startTime, endTime);
    }

    @Override
    public List<AdminLog> statisticsOperationTypes(LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime == null || endTime == null) {
            return List.of();
        }
        return adminLogMapper.statisticsOperationTypes(startTime, endTime);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int cleanLogsBefore(LocalDateTime beforeTime) {
        if (beforeTime == null) {
            throw new ValidationException("清理时间不能为空");
        }

        try {
            int deletedCount = adminLogMapper.cleanLogsBefore(beforeTime);
            log.info("清理管理员日志完成，清理数量: {}, 清理时间点: {}", deletedCount, beforeTime);
            return deletedCount;
        } catch (Exception e) {
            log.error("清理管理员日志失败，清理时间点: {}", beforeTime, e);
            throw new BusinessException("清理管理员日志失败");
        }
    }

    @Override
    public boolean recordLoginLog(Long adminId, String ipAddress, String userAgent, boolean success) {
        if (adminId == null) {
            return false;
        }

        String operationContent = success ? "管理员登录成功" : "管理员登录失败";
        return recordAdminOperation(adminId, OPERATION_TYPE_LOGIN, operationContent, 
                                  null, ipAddress, userAgent);
    }

    @Override
    public boolean recordUserManagement(Long adminId, String operation, Long targetUserId, 
                                      String operationContent, String ipAddress) {
        if (adminId == null || !StringUtils.hasText(operation)) {
            return false;
        }

        String content = String.format("用户管理-%s: %s", operation, 
                                      StringUtils.hasText(operationContent) ? operationContent : "用户操作");
        return recordAdminOperation(adminId, OPERATION_TYPE_USER_MGMT, content, 
                                  targetUserId, ipAddress, null);
    }

    @Override
    public boolean recordContentManagement(Long adminId, String operation, String contentType, 
                                         Long contentId, String operationContent, String ipAddress) {
        if (adminId == null || !StringUtils.hasText(operation) || !StringUtils.hasText(contentType)) {
            return false;
        }

        String content = String.format("内容管理-%s-%s: %s", contentType, operation,
                                      StringUtils.hasText(operationContent) ? operationContent : "内容操作");
        return recordAdminOperation(adminId, OPERATION_TYPE_CONTENT_MGMT, content, 
                                  contentId, ipAddress, null);
    }

    @Override
    public boolean recordConfigManagement(Long adminId, String operation, String configKey, 
                                        String oldValue, String newValue, String ipAddress) {
        if (adminId == null || !StringUtils.hasText(operation) || !StringUtils.hasText(configKey)) {
            return false;
        }

        String content = String.format("配置管理-%s: 配置键=%s, 旧值=%s, 新值=%s", 
                                      operation, configKey, 
                                      StringUtils.hasText(oldValue) ? oldValue : "空",
                                      StringUtils.hasText(newValue) ? newValue : "空");
        return recordAdminOperation(adminId, OPERATION_TYPE_CONFIG_MGMT, content, 
                                  null, ipAddress, null);
    }

    @Override
    public Long getTodayOperationCount(Long adminId) {
        if (adminId == null) {
            return 0L;
        }

        LocalDateTime startTime = LocalDateTime.of(LocalDateTime.now().toLocalDate(), LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(LocalDateTime.now().toLocalDate(), LocalTime.MAX);

        return countOperationsByAdmin(adminId, startTime, endTime);
    }

    @Override
    public List<AdminLog> getOperationHotspot(int days, int limit) {
        if (days <= 0) {
            days = 7; // 默认统计7天
        }
        if (limit <= 0) {
            limit = 10; // 默认返回10条
        }

        LocalDateTime endTime = LocalDateTime.now();
        LocalDateTime startTime = endTime.minusDays(days);

        return statisticsOperationTypes(startTime, endTime).stream()
            .limit(limit)
            .collect(java.util.stream.Collectors.toList());
    }
}