package org.easytech.blogs.dto;

import lombok.Data;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * 更新文章请求DTO
 */
@Data
public class PostUpdateRequest {
    
    @Size(max = 200, message = "标题长度不能超过200个字符")
    private String title;
    
    @Size(max = 500, message = "摘要长度不能超过500个字符")
    private String summary;
    
    private String content;
    
    private Long categoryId;
    
    private List<String> tagNames;
}