package org.easytech.blogs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.easytech.blogs.entity.SystemConfig;

/**
 * 系统配置Mapper接口
 * 负责系统配置信息的数据访问操作
 */
@Mapper
public interface SystemConfigMapper extends BaseMapper<SystemConfig> {

    /**
     * 根据配置键查询配置
     * @param configKey 配置键
     * @return 系统配置信息
     */
    @Select("SELECT * FROM tb_system_config WHERE config_key = #{configKey}")
    SystemConfig selectByConfigKey(@Param("configKey") String configKey);

    /**
     * 根据配置键更新配置值
     * @param configKey 配置键
     * @param configValue 配置值
     * @return 更新影响行数
     */
    @Update("UPDATE tb_system_config SET config_value = #{configValue}, update_time = NOW() " +
            "WHERE config_key = #{configKey}")
    int updateValueByKey(@Param("configKey") String configKey, @Param("configValue") String configValue);

    /**
     * 根据分组查询配置列表
     * @param configGroup 配置分组
     * @return 配置列表
     */
    @Select("SELECT * FROM tb_system_config WHERE config_group = #{configGroup} ORDER BY sort_order ASC")
    java.util.List<SystemConfig> selectByGroup(@Param("configGroup") String configGroup);

    /**
     * 查询所有启用的配置
     * @return 启用的配置列表
     */
    @Select("SELECT * FROM tb_system_config WHERE status = 1 ORDER BY config_group ASC, sort_order ASC")
    java.util.List<SystemConfig> selectEnabledConfigs();

    /**
     * 根据配置键批量查询
     * @param configKeys 配置键列表
     * @return 配置列表
     */
    @Select("<script>" +
            "SELECT * FROM tb_system_config WHERE config_key IN " +
            "<foreach collection='configKeys' item='key' open='(' separator=',' close=')'>" +
            "#{key}" +
            "</foreach>" +
            "</script>")
    java.util.List<SystemConfig> selectByKeys(@Param("configKeys") java.util.List<String> configKeys);

    /**
     * 检查配置键是否存在
     * @param configKey 配置键
     * @return 是否存在
     */
    @Select("SELECT COUNT(*) > 0 FROM tb_system_config WHERE config_key = #{configKey}")
    boolean existsByKey(@Param("configKey") String configKey);

    /**
     * 根据状态查询配置数量
     * @param status 配置状态
     * @return 配置数量
     */
    @Select("SELECT COUNT(*) FROM tb_system_config WHERE status = #{status}")
    Long countByStatus(@Param("status") Integer status);
}