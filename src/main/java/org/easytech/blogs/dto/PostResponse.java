package org.easytech.blogs.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 文章响应DTO
 */
@Data
public class PostResponse {
    
    private Long id;
    private String title;
    private String summary;
    private String content;
    private Long authorId;
    private String authorName;
    private Long categoryId;
    private String categoryName;
    private Integer status;
    private Integer viewCount;
    private Integer likeCount;
    private Integer commentCount;
    private Boolean isTop;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    
    // 标签列表
    private List<String> tags;
    
    // 是否已点赞（需要用户登录状态）
    private Boolean hasLiked;
}