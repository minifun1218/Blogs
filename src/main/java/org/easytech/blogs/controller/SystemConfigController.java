package org.easytech.blogs.controller;

import org.easytech.blogs.common.Result;
import org.easytech.blogs.entity.SystemConfig;
import org.easytech.blogs.service.SystemConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 系统配置管理控制器 - RESTful API
 */
@RestController
@RequestMapping("/api/system-config")
@CrossOrigin
@Validated
public class SystemConfigController {

    private final SystemConfigService systemConfigService;

    @Autowired
    public SystemConfigController(SystemConfigService systemConfigService) {
        this.systemConfigService = systemConfigService;
    }

    /**
     * 获取所有系统配置
     * GET /api/system-config
     */
    @GetMapping
    public Result<List<SystemConfig>> getAllConfigs(
            @RequestParam(required = false) String configGroup,
            @RequestParam(required = false) Integer status) {
        
        List<SystemConfig> configs;
        if (configGroup != null) {
            configs = systemConfigService.getConfigsByGroup(configGroup);
        } else if (status != null && status == 1) {
            configs = systemConfigService.getAllEnabledConfigs();
        } else {
            // 这里需要在Service中添加获取所有配置的方法
            configs = systemConfigService.getAllEnabledConfigs();
        }
        return Result.success(configs);
    }

    /**
     * 根据配置分组获取配置列表
     * GET /api/system-config/group/{group}
     */
    @GetMapping("/group/{group}")
    public Result<List<SystemConfig>> getConfigsByGroup(@PathVariable String group) {
        List<SystemConfig> configs = systemConfigService.getConfigsByGroup(group);
        return Result.success(configs);
    }

    /**
     * 根据配置键获取配置值
     * GET /api/system-config/key/{key}
     */
    @GetMapping("/key/{key}")
    public Result<String> getConfigValue(@PathVariable String key) {
        String value = systemConfigService.getConfigValue(key);
        if (value == null) {
            return Result.notFound("配置不存在");
        }
        return Result.success(value);
    }

    /**
     * 根据配置键获取配置值（带默认值）
     * GET /api/system-config/value
     */
    @GetMapping("/value")
    public Result<String> getConfigValueWithDefault(
            @RequestParam String key,
            @RequestParam(required = false) String defaultValue) {
        
        String value = systemConfigService.getConfigValue(key, defaultValue);
        return Result.success(value);
    }

    /**
     * 根据ID查询配置详情
     * GET /api/system-config/{id}
     */
    @GetMapping("/{id}")
    public Result<SystemConfig> getSystemConfigById(@PathVariable Long id) {
        SystemConfig config = systemConfigService.getConfigById(id);
        if (config == null) {
            return Result.notFound("配置不存在");
        }
        return Result.success(config);
    }

    /**
     * 获取所有配置分组
     * GET /api/system-config/groups
     */
    @GetMapping("/groups")
    public Result<List<String>> getConfigGroups() {
        List<String> groups = systemConfigService.getAllConfigGroups();
        return Result.success(groups);
    }

    /**
     * 批量获取配置值
     * POST /api/system-config/batch-get
     */
    @PostMapping("/batch-get")
    public Result<Map<String, String>> getConfigsByKeys(@RequestBody List<String> configKeys) {
        Map<String, String> configs = systemConfigService.getConfigsByKeys(configKeys);
        return Result.success(configs);
    }

    /**
     * 创建系统配置
     * POST /api/system-config
     */
    @PostMapping
    public Result<SystemConfig> createSystemConfig(@Validated @RequestBody SystemConfig config) {
        // 检查配置键是否已存在
        if (systemConfigService.isConfigKeyExists(config.getConfigKey())) {
            return Result.badRequest("配置键已存在");
        }
        
        boolean success = systemConfigService.createConfig(config);
        if (success) {
            return Result.success("配置创建成功", config);
        }
        return Result.error("配置创建失败");
    }

    /**
     * 更新系统配置
     * PUT /api/system-config/{id}
     */
    @PutMapping("/{id}")
    public Result<SystemConfig> updateSystemConfig(@PathVariable Long id, @Validated @RequestBody SystemConfig config) {
        SystemConfig existingConfig = systemConfigService.getConfigById(id);
        if (existingConfig == null) {
            return Result.notFound("配置不存在");
        }
        
        config.setId(id);
        boolean success = systemConfigService.updateConfig(config);
        if (success) {
            return Result.success("配置更新成功", config);
        }
        return Result.error("配置更新失败");
    }

    /**
     * 更新配置值
     * PUT /api/system-config/value
     */
    @PutMapping("/value")
    public Result<String> updateConfigValue(
            @RequestParam String configKey,
            @RequestParam String configValue) {
        
        boolean success = systemConfigService.updateConfigValue(configKey, configValue);
        if (success) {
            return Result.success("配置值更新成功");
        }
        return Result.error("配置值更新失败");
    }

    /**
     * 批量更新配置值
     * PUT /api/system-config/batch
     */
    @PutMapping("/batch")
    public Result<String> batchUpdateConfigs(@RequestBody Map<String, String> configMap) {
        boolean success = systemConfigService.batchUpdateConfigs(configMap);
        if (success) {
            return Result.success("批量更新成功");
        }
        return Result.error("批量更新失败");
    }

    /**
     * 删除系统配置
     * DELETE /api/system-config/{id}
     */
    @DeleteMapping("/{id}")
    public Result<String> deleteSystemConfig(@PathVariable Long id) {
        SystemConfig config = systemConfigService.getConfigById(id);
        if (config == null) {
            return Result.notFound("配置不存在");
        }
        
        boolean success = systemConfigService.deleteConfig(id);
        if (success) {
            return Result.success("配置删除成功");
        }
        return Result.error("配置删除失败");
    }

    /**
     * 检查配置键是否可用
     * GET /api/system-config/check-key
     */
    @GetMapping("/check-key")
    public Result<Boolean> checkKeyAvailable(@RequestParam String configKey) {
        boolean exists = systemConfigService.isConfigKeyExists(configKey);
        return Result.success(!exists);
    }

    /**
     * 获取整数配置值
     * GET /api/system-config/int-value
     */
    @GetMapping("/int-value")
    public Result<Integer> getIntConfigValue(
            @RequestParam String configKey,
            @RequestParam(required = false, defaultValue = "0") Integer defaultValue) {
        
        Integer value = systemConfigService.getIntConfigValue(configKey, defaultValue);
        return Result.success(value);
    }

    /**
     * 获取布尔配置值
     * GET /api/system-config/boolean-value
     */
    @GetMapping("/boolean-value")
    public Result<Boolean> getBooleanConfigValue(
            @RequestParam String configKey,
            @RequestParam(required = false, defaultValue = "false") Boolean defaultValue) {
        
        Boolean value = systemConfigService.getBooleanConfigValue(configKey, defaultValue);
        return Result.success(value);
    }

    /**
     * 统计配置数量
     * GET /api/system-config/count
     */
    @GetMapping("/count")
    public Result<Long> countConfigs(@RequestParam(required = false) Integer status) {
        Long count = systemConfigService.countConfigs(status);
        return Result.success(count);
    }

    /**
     * 初始化默认配置
     * POST /api/system-config/init-default
     */
    @PostMapping("/init-default")
    public Result<String> initDefaultConfigs() {
        boolean success = systemConfigService.initDefaultConfigs();
        if (success) {
            return Result.success("默认配置初始化成功");
        }
        return Result.error("默认配置初始化失败");
    }

    /**
     * 刷新配置缓存
     * POST /api/system-config/refresh-cache
     */
    @PostMapping("/refresh-cache")
    public Result<String> refreshConfigCache() {
        systemConfigService.refreshConfigCache();
        return Result.success("配置缓存刷新成功");
    }
}