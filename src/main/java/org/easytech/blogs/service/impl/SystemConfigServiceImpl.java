package org.easytech.blogs.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.easytech.blogs.entity.SystemConfig;
import org.easytech.blogs.exception.BusinessException;
import org.easytech.blogs.exception.ResourceNotFoundException;
import org.easytech.blogs.exception.ValidationException;
import org.easytech.blogs.mapper.SystemConfigMapper;
import org.easytech.blogs.service.SystemConfigService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 系统配置服务实现类
 * 实现系统配置相关的业务逻辑处理
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SystemConfigServiceImpl implements SystemConfigService {

    private final SystemConfigMapper systemConfigMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createConfig(SystemConfig config) {
        // 参数校验
        if (config == null || !StringUtils.hasText(config.getConfigKey())) {
            throw new ValidationException("配置键不能为空");
        }

        // 检查配置键是否已存在
        if (isConfigKeyExists(config.getConfigKey())) {
            throw new BusinessException("配置键已存在");
        }

        try {
            // 设置默认值
            if (config.getStatus() == null) {
                config.setStatus(1); // 默认启用
            }
            if (config.getSortOrder() == null) {
                config.setSortOrder(0);
            }

            int result = systemConfigMapper.insert(config);
            if (result > 0) {
                log.info("系统配置创建成功，配置键: {}", config.getConfigKey());
                return true;
            }
        } catch (Exception e) {
            log.error("系统配置创建失败，配置键: {}", config.getConfigKey(), e);
            throw new BusinessException("系统配置创建失败，请稍后重试");
        }

        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateConfig(SystemConfig config) {
        if (config == null || config.getId() == null) {
            throw new ValidationException("配置ID不能为空");
        }

        // 检查配置是否存在
        SystemConfig existConfig = systemConfigMapper.selectById(config.getId());
        if (existConfig == null) {
            throw new ResourceNotFoundException("配置不存在");
        }

        // 如果更新了配置键，检查新配置键是否已存在
        if (StringUtils.hasText(config.getConfigKey()) && 
            !config.getConfigKey().equals(existConfig.getConfigKey()) &&
            isConfigKeyExists(config.getConfigKey())) {
            throw new BusinessException("配置键已存在");
        }

        try {
            int result = systemConfigMapper.updateById(config);
            if (result > 0) {
                log.info("系统配置更新成功，配置ID: {}", config.getId());
                refreshConfigCache(); // 刷新缓存
                return true;
            }
        } catch (Exception e) {
            log.error("系统配置更新失败，配置ID: {}", config.getId(), e);
            throw new BusinessException("系统配置更新失败，请稍后重试");
        }

        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteConfig(Long configId) {
        if (configId == null) {
            throw new ValidationException("配置ID不能为空");
        }

        SystemConfig config = systemConfigMapper.selectById(configId);
        if (config == null) {
            throw new ResourceNotFoundException("配置不存在");
        }

        try {
            int result = systemConfigMapper.deleteById(configId);
            if (result > 0) {
                log.info("系统配置删除成功，配置ID: {}", configId);
                refreshConfigCache(); // 刷新缓存
                return true;
            }
        } catch (Exception e) {
            log.error("系统配置删除失败，配置ID: {}", configId, e);
            throw new BusinessException("系统配置删除失败，请稍后重试");
        }

        return false;
    }

    @Override
    public SystemConfig getConfigById(Long configId) {
        if (configId == null) {
            return null;
        }
        return systemConfigMapper.selectById(configId);
    }

    @Override
    public SystemConfig getConfigByKey(String configKey) {
        if (!StringUtils.hasText(configKey)) {
            return null;
        }
        return systemConfigMapper.selectByConfigKey(configKey);
    }

    @Override
    public String getConfigValue(String configKey) {
        return getConfigValue(configKey, null);
    }

    @Override
    public String getConfigValue(String configKey, String defaultValue) {
        if (!StringUtils.hasText(configKey)) {
            return defaultValue;
        }

        SystemConfig config = systemConfigMapper.selectByConfigKey(configKey);
        if (config != null && config.getStatus() == 1) {
            return StringUtils.hasText(config.getConfigValue()) ? config.getConfigValue() : defaultValue;
        }

        return defaultValue;
    }

    @Override
    public Integer getIntConfigValue(String configKey, Integer defaultValue) {
        String value = getConfigValue(configKey);
        if (StringUtils.hasText(value)) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                log.warn("配置值格式错误，配置键: {}, 配置值: {}", configKey, value);
            }
        }
        return defaultValue;
    }

    @Override
    public Boolean getBooleanConfigValue(String configKey, Boolean defaultValue) {
        String value = getConfigValue(configKey);
        if (StringUtils.hasText(value)) {
            return "true".equalsIgnoreCase(value) || "1".equals(value) || "yes".equalsIgnoreCase(value);
        }
        return defaultValue;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateConfigValue(String configKey, String configValue) {
        if (!StringUtils.hasText(configKey)) {
            throw new ValidationException("配置键不能为空");
        }

        try {
            int result = systemConfigMapper.updateValueByKey(configKey, configValue);
            if (result > 0) {
                log.info("配置值更新成功，配置键: {}, 配置值: {}", configKey, configValue);
                refreshConfigCache(); // 刷新缓存
                return true;
            } else {
                log.warn("配置键不存在: {}", configKey);
                return false;
            }
        } catch (Exception e) {
            log.error("配置值更新失败，配置键: {}", configKey, e);
            throw new BusinessException("配置值更新失败");
        }
    }

    @Override
    public List<SystemConfig> getConfigsByGroup(String configGroup) {
        if (!StringUtils.hasText(configGroup)) {
            return List.of();
        }
        return systemConfigMapper.selectByGroup(configGroup);
    }

    @Override
    public List<SystemConfig> getAllEnabledConfigs() {
        return systemConfigMapper.selectEnabledConfigs();
    }

    @Override
    public Map<String, String> getConfigsByKeys(List<String> configKeys) {
        if (configKeys == null || configKeys.isEmpty()) {
            return Map.of();
        }

        List<SystemConfig> configs = systemConfigMapper.selectByKeys(configKeys);
        return configs.stream()
            .filter(config -> config.getStatus() == 1) // 只返回启用的配置
            .collect(Collectors.toMap(
                SystemConfig::getConfigKey,
                config -> config.getConfigValue() != null ? config.getConfigValue() : "",
                (existing, replacement) -> existing // 如果有重复键，保留现有值
            ));
    }

    @Override
    public boolean isConfigKeyExists(String configKey) {
        if (!StringUtils.hasText(configKey)) {
            return false;
        }
        return systemConfigMapper.existsByKey(configKey);
    }

    @Override
    public Long countConfigs(Integer status) {
        if (status == null) {
            return systemConfigMapper.selectCount(null);
        }
        return systemConfigMapper.countByStatus(status);
    }

    @Override
    public List<String> getAllConfigGroups() {
        List<SystemConfig> allConfigs = systemConfigMapper.selectEnabledConfigs();
        return allConfigs.stream()
            .map(SystemConfig::getConfigGroup)
            .filter(StringUtils::hasText)
            .distinct()
            .sorted()
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchUpdateConfigs(Map<String, String> configs) {
        if (configs == null || configs.isEmpty()) {
            throw new ValidationException("配置映射不能为空");
        }

        try {
            int successCount = 0;
            for (Map.Entry<String, String> entry : configs.entrySet()) {
                if (updateConfigValue(entry.getKey(), entry.getValue())) {
                    successCount++;
                }
            }

            log.info("批量更新配置完成，成功更新: {}/{}", successCount, configs.size());
            refreshConfigCache(); // 刷新缓存
            return successCount == configs.size();
        } catch (Exception e) {
            log.error("批量更新配置失败", e);
            throw new BusinessException("批量更新配置失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean initDefaultConfigs() {
        try {
            Map<String, SystemConfig> defaultConfigs = getDefaultConfigs();
            int insertCount = 0;

            for (SystemConfig config : defaultConfigs.values()) {
                if (!isConfigKeyExists(config.getConfigKey())) {
                    systemConfigMapper.insert(config);
                    insertCount++;
                    log.info("初始化默认配置: {} = {}", config.getConfigKey(), config.getConfigValue());
                }
            }

            log.info("默认配置初始化完成，新增配置: {}", insertCount);
            return true;
        } catch (Exception e) {
            log.error("默认配置初始化失败", e);
            throw new BusinessException("默认配置初始化失败");
        }
    }

    @Override
    public void refreshConfigCache() {
        // 如果使用了缓存（如Redis、本地缓存等），在这里刷新缓存
        // 当前实现直接从数据库读取，暂不需要缓存刷新操作
        log.debug("配置缓存刷新完成");
    }

    /**
     * 获取默认配置
     */
    private Map<String, SystemConfig> getDefaultConfigs() {
        Map<String, SystemConfig> configs = new HashMap<>();

        // 网站基本信息
        configs.put("site.name", createConfig("site.name", "EasyTech博客系统", "网站名称", "网站", 1));
        configs.put("site.description", createConfig("site.description", "基于Spring Boot的博客系统", "网站描述", "网站", 2));
        configs.put("site.keywords", createConfig("site.keywords", "博客,Spring Boot,技术分享", "网站关键词", "网站", 3));
        configs.put("site.author", createConfig("site.author", "EasyTech", "网站作者", "网站", 4));
        configs.put("site.icp", createConfig("site.icp", "", "ICP备案号", "网站", 5));

        // 文章相关配置
        configs.put("post.page.size", createConfig("post.page.size", "10", "文章列表每页显示数量", "文章", 1));
        configs.put("post.comment.enabled", createConfig("post.comment.enabled", "true", "是否开启评论功能", "文章", 2));
        configs.put("post.auto.summary", createConfig("post.auto.summary", "200", "文章自动摘要长度", "文章", 3));

        // 用户相关配置
        configs.put("user.register.enabled", createConfig("user.register.enabled", "true", "是否开启用户注册", "用户", 1));
        configs.put("user.email.verify", createConfig("user.email.verify", "false", "是否需要邮箱验证", "用户", 2));
        configs.put("user.default.avatar", createConfig("user.default.avatar", "/images/default-avatar.png", "默认用户头像", "用户", 3));

        // 积分系统配置
        configs.put("coin.sign.reward", createConfig("coin.sign.reward", "10", "签到奖励积分", "积分", 1));
        configs.put("coin.post.reward", createConfig("coin.post.reward", "20", "发布文章奖励积分", "积分", 2));
        configs.put("coin.comment.reward", createConfig("coin.comment.reward", "5", "发表评论奖励积分", "积分", 3));
        configs.put("coin.like.reward", createConfig("coin.like.reward", "2", "被点赞奖励积分", "积分", 4));

        // 文件上传配置
        configs.put("upload.max.size", createConfig("upload.max.size", "10485760", "文件上传最大大小（字节）", "上传", 1));
        configs.put("upload.allowed.types", createConfig("upload.allowed.types", "jpg,jpeg,png,gif,pdf,doc,docx", "允许上传的文件类型", "上传", 2));

        return configs;
    }

    /**
     * 创建配置对象
     */
    private SystemConfig createConfig(String key, String value, String description, String group, Integer sortOrder) {
        SystemConfig config = new SystemConfig();
        config.setConfigKey(key);
        config.setConfigValue(value);
        config.setDescription(description);
        config.setConfigGroup(group);
        config.setSortOrder(sortOrder);
        config.setStatus(1);
        return config;
    }
}