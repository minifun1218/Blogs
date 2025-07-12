package org.easytech.blogs.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 系统配置实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("tb_system_config")
public class SystemConfig {

    /**
     * 配置ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 配置键
     */
    @TableField("config_key")
    private String configKey;

    /**
     * 配置值
     */
    @TableField("config_value")
    private String configValue;

    /**
     * 配置名称
     */
    @TableField("config_name")
    private String configName;

    /**
     * 配置描述
     */
    @TableField("description")
    private String description;

    /**
     * 配置分组
     */
    @TableField("config_group")
    private String configGroup;

    /**
     * 数据类型：1-字符串，2-数字，3-布尔值，4-JSON
     */
    @TableField("data_type")
    private Integer dataType;

    /**
     * 是否系统内置：0-否，1-是
     */
    @TableField("is_system")
    private Integer isSystem;

    /**
     * 排序权重
     */
    @TableField("sort_order")
    private Integer sortOrder;

    /**
     * 配置状态：0-禁用，1-启用
     */
    @TableField("status")
    private Integer status;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

}
