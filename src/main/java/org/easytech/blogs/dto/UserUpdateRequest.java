package org.easytech.blogs.dto;

import lombok.Data;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

/**
 * 用户更新请求DTO
 */
@Data
public class UserUpdateRequest {
    
    @Email(message = "邮箱格式不正确")
    private String email;
    
    @Size(max = 50, message = "昵称长度不能超过50个字符")
    private String nickname;
    
    @Size(max = 500, message = "个人简介长度不能超过500个字符")
    private String bio;
    
    private String avatar;
}