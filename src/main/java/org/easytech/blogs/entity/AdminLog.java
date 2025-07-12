package org.easytech.blogs.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 管理员操作日志实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("tb_admin_log")
public class AdminLog {

    /**
     * 日志ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 管理员ID
     */
    @TableField("admin_id")
    private Long adminId;

    /**
     * 操作类型：1-登录，2-用户管理，3-内容管理，4-配置管理，5-系统操作
     */
    @TableField("operation_type")
    private Integer operationType;

    /**
     * 操作内容描述
     */
    @TableField("operation_content")
    private String operationContent;

    /**
     * 操作目标ID（如用户ID、文章ID等）
     */
    @TableField("target_id")
    private Long targetId;

    /**
     * IP地址
     */
    @TableField("ip_address")
    private String ipAddress;

    /**
     * 用户代理
     */
    @TableField("user_agent")
    private String userAgent;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

}
