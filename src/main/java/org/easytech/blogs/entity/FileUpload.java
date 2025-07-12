package org.easytech.blogs.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 文件上传记录实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("tb_file_upload")
public class FileUpload {

    /**
     * 文件ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 原始文件名
     */
    @TableField("original_name")
    private String originalName;

    /**
     * 存储文件名
     */
    @TableField("stored_name")
    private String storedName;

    /**
     * 文件名（别名，映射到stored_name）
     */
    @TableField("stored_name")
    private String fileName;

    /**
     * 文件路径
     */
    @TableField("file_path")
    private String filePath;

    /**
     * 文件URL
     */
    @TableField("file_url")
    private String fileUrl;

    /**
     * 文件类型：1-图片，2-视频，3-文档，4-其他
     */
    @TableField("file_type")
    private Integer fileType;

    /**
     * MIME类型
     */
    @TableField("mime_type")
    private String mimeType;

    /**
     * 文件大小（字节）
     */
    @TableField("file_size")
    private Long fileSize;

    /**
     * 上传用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 关联对象类型：1-文章，2-评论，3-用户头像
     */
    @TableField("related_type")
    private Integer relatedType;

    /**
     * 关联对象ID
     */
    @TableField("related_id")
    private Long relatedId;

    /**
     * 文件状态：0-临时，1-正式使用
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

    /**
     * 逻辑删除：0-未删除，1-已删除
     */
    @TableLogic
    @TableField("is_deleted")
    private Integer isDeleted;

}
