package org.easytech.blogs.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 评论响应DTO
 */
@Data
public class CommentResponse {
    
    private Long id;
    private Long postId;
    private Long userId;
    private String username;
    private String userAvatar;
    private String content;
    private Long parentId;
    private Integer status; // 0-待审核，1-已通过，2-已拒绝
    private Integer likeCount;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    
    // 子评论列表（用于树形结构）
    private List<CommentResponse> children;
    
    // 是否已点赞（需要用户登录状态）
    private Boolean hasLiked;
}