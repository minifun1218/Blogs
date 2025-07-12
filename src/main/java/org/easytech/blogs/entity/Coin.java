package org.easytech.blogs.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 积分记录实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("tb_coin")
public class Coin {

    /**
     * 记录ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 积分变化量（正数为增加，负数为减少）
     */
    @TableField("amount")
    private Integer amount;

    /**
     * 操作类型：1-发布文章，2-评论，3-点赞，4-被点赞，5-签到，6-消费
     */
    @TableField("operation_type")
    private Integer operationType;

    /**
     * 操作描述
     */
    @TableField("description")
    private String description;

    /**
     * 关联对象ID（如文章ID、评论ID等）
     */
    @TableField("related_id")
    private Long relatedId;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

}
