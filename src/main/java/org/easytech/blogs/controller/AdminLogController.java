package org.easytech.blogs.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.easytech.blogs.common.PageResult;
import org.easytech.blogs.common.Result;
import org.easytech.blogs.entity.AdminLog;
import org.easytech.blogs.service.AdminLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 管理员日志控制器 - RESTful API
 */
@RestController
@RequestMapping("/api/admin-logs")
@CrossOrigin
@Validated
public class AdminLogController {

    private final AdminLogService adminLogService;

    @Autowired
    public AdminLogController(AdminLogService adminLogService) {
        this.adminLogService = adminLogService;
    }

    /**
     * 分页查询管理员日志
     * GET /api/admin-logs?page=1&size=10&adminId=1&operationType=1
     */
    @GetMapping
    public Result<PageResult<AdminLog>> getAdminLogs(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long adminId,
            @RequestParam(required = false) Integer operationType,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        
        Page<AdminLog> pageInfo = new Page<>(page, size);
        IPage<AdminLog> result;
        
        if (adminId != null) {
            result = adminLogService.getLogsByAdminId(pageInfo, adminId);
        } else if (operationType != null) {
            result = adminLogService.getLogsByOperationType(pageInfo, operationType);
        } else {
            result = adminLogService.getAdminLogPage(pageInfo);
        }
        
        return Result.success(PageResult.of(result));
    }

    /**
     * 记录管理员操作日志
     * POST /api/admin-logs
     */
    @PostMapping
    public Result<String> recordAdminLog(
            @RequestParam Long adminId,
            @RequestParam Integer operationType,
            @RequestParam String operationContent,
            @RequestParam(required = false) Long targetId,
            @RequestParam String ipAddress,
            @RequestParam String userAgent) {
        
        boolean success = adminLogService.recordAdminOperation(
            adminId, operationType, operationContent, targetId, ipAddress, userAgent);
        
        if (success) {
            return Result.success("日志记录成功");
        }
        return Result.error("日志记录失败");
    }

    /**
     * 获取管理员今日操作统计
     * GET /api/admin-logs/today/{adminId}
     */
    @GetMapping("/today/{adminId}")
    public Result<Long> getTodayOperationCount(@PathVariable Long adminId) {
        Long count = adminLogService.getTodayOperationCount(adminId);
        return Result.success(count);
    }

    /**
     * 记录登录日志
     * POST /api/admin-logs/login
     */
    @PostMapping("/login")
    public Result<String> recordLoginLog(
            @RequestParam Long adminId,
            @RequestParam String ipAddress,
            @RequestParam String userAgent,
            @RequestParam boolean success) {
        
        boolean result = adminLogService.recordLoginLog(adminId, ipAddress, userAgent, success);
        if (result) {
            return Result.success("登录日志记录成功");
        }
        return Result.error("登录日志记录失败");
    }

    /**
     * 清理过期日志
     * DELETE /api/admin-logs/cleanup
     */
    @DeleteMapping("/cleanup")
    public Result<String> cleanupExpiredLogs(@RequestParam(defaultValue = "90") int days) {
        LocalDateTime beforeTime = LocalDateTime.now().minusDays(days);
        int deletedCount = adminLogService.cleanLogsBefore(beforeTime);
        return Result.success("清理完成，删除了 " + deletedCount + " 条过期日志");
    }

    /**
     * 获取操作热点统计
     * GET /api/admin-logs/hotspot
     */
    @GetMapping("/hotspot")
    public Result<List<AdminLog>> getOperationHotspot(
            @RequestParam(defaultValue = "7") int days,
            @RequestParam(defaultValue = "10") int limit) {
        
        List<AdminLog> hotspot = adminLogService.getOperationHotspot(days, limit);
        return Result.success(hotspot);
    }
}