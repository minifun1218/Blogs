package org.easytech.blogs.service;

import org.easytech.blogs.entity.SystemConfig;

import java.util.List;
import java.util.Map;

/**
 * 系统配置服务接口
 * 提供系统配置相关的业务逻辑处理
 */
public interface SystemConfigService {

    /**
     * 创建系统配置
     * @param config 配置信息
     * @return 创建结果
     */
    boolean createConfig(SystemConfig config);

    /**
     * 更新系统配置
     * @param config 配置信息
     * @return 更新结果
     */
    boolean updateConfig(SystemConfig config);

    /**
     * 删除系统配置
     * @param configId 配置ID
     * @return 删除结果
     */
    boolean deleteConfig(Long configId);

    /**
     * 根据ID获取配置
     * @param configId 配置ID
     * @return 配置信息
     */
    SystemConfig getConfigById(Long configId);

    /**
     * 根据配置键获取配置
     * @param configKey 配置键
     * @return 配置信息
     */
    SystemConfig getConfigByKey(String configKey);

    /**
     * 根据配置键获取配置值
     * @param configKey 配置键
     * @return 配置值
     */
    String getConfigValue(String configKey);

    /**
     * 根据配置键获取配置值（带默认值）
     * @param configKey 配置键
     * @param defaultValue 默认值
     * @return 配置值
     */
    String getConfigValue(String configKey, String defaultValue);

    /**
     * 根据配置键获取整数配置值
     * @param configKey 配置键
     * @param defaultValue 默认值
     * @return 整数配置值
     */
    Integer getIntConfigValue(String configKey, Integer defaultValue);

    /**
     * 根据配置键获取布尔配置值
     * @param configKey 配置键
     * @param defaultValue 默认值
     * @return 布尔配置值
     */
    Boolean getBooleanConfigValue(String configKey, Boolean defaultValue);

    /**
     * 根据配置键更新配置值
     * @param configKey 配置键
     * @param configValue 配置值
     * @return 更新结果
     */
    boolean updateConfigValue(String configKey, String configValue);

    /**
     * 根据分组获取配置列表
     * @param configGroup 配置分组
     * @return 配置列表
     */
    List<SystemConfig> getConfigsByGroup(String configGroup);

    /**
     * 获取所有启用的配置
     * @return 启用的配置列表
     */
    List<SystemConfig> getAllEnabledConfigs();

    /**
     * 批量获取配置
     * @param configKeys 配置键列表
     * @return 配置映射
     */
    Map<String, String> getConfigsByKeys(List<String> configKeys);

    /**
     * 检查配置键是否存在
     * @param configKey 配置键
     * @return 是否存在
     */
    boolean isConfigKeyExists(String configKey);

    /**
     * 统计配置数量
     * @param status 配置状态，null表示所有状态
     * @return 配置数量
     */
    Long countConfigs(Integer status);

    /**
     * 获取所有配置分组
     * @return 配置分组列表
     */
    List<String> getAllConfigGroups();

    /**
     * 批量更新配置
     * @param configs 配置映射（key -> value）
     * @return 更新结果
     */
    boolean batchUpdateConfigs(Map<String, String> configs);

    /**
     * 初始化默认配置
     * @return 初始化结果
     */
    boolean initDefaultConfigs();

    /**
     * 刷新配置缓存（如果使用了缓存）
     */
    void refreshConfigCache();
}