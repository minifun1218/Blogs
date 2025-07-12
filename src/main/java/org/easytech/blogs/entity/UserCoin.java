package org.easytech.blogs.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 用户积分账户实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("tb_user_coin")
public class UserCoin {

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
     * 积分余额
     */
    @TableField("coin_balance")
    private Integer coinBalance;

    /**
     * 总获得积分
     */
    @TableField("total_earned")
    private Integer totalEarned;

    /**
     * 总消费积分
     */
    @TableField("total_consumed")
    private Integer totalConsumed;

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
