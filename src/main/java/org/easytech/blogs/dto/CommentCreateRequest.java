package org.easytech.blogs.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 添加评论请求DTO
 */
@Data
public class CommentCreateRequest {
    
    @NotNull(message = "文章ID不能为空")
    private Long postId;
    
    @NotNull(message = "用户ID不能为空")
    private Long userId;
    
    @NotBlank(message = "评论内容不能为空")
    @Size(max = 1000, message = "评论内容不能超过1000个字符")
    private String content;
    
    private Long parentId; // 父评论ID，顶级评论为null
}