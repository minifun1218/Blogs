package org.easytech.blogs.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * 发布文章请求DTO
 */
@Data
public class PostCreateRequest {
    
    @NotBlank(message = "标题不能为空")
    @Size(max = 200, message = "标题长度不能超过200个字符")
    private String title;
    
    @Size(max = 500, message = "摘要长度不能超过500个字符")
    private String summary;
    
    @NotBlank(message = "内容不能为空")
    private String content;
    
    @NotNull(message = "作者ID不能为空")
    private Long authorId;
    
    private Long categoryId;
    
    private List<String> tagNames;
    
    private Integer status = 1; // 默认发布状态
}