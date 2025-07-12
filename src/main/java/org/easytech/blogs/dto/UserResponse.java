package org.easytech.blogs.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用户响应DTO
 */
@Data
public class UserResponse {
    
    private Long id;
    private String username;
    private String email;
    private String nickname;
    private String avatar;
    private String bio;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}