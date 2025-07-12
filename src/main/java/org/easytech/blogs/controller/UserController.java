package org.easytech.blogs.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.easytech.blogs.annotation.PublicAccess;
import org.easytech.blogs.annotation.RequiresAuthentication;
import org.easytech.blogs.annotation.RequiresRole;
import org.easytech.blogs.common.PageResult;
import org.easytech.blogs.common.Result;
import org.easytech.blogs.dto.*;
import org.easytech.blogs.entity.User;
import org.easytech.blogs.service.UserService;
import org.easytech.blogs.util.SecurityUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器 - RESTful API
 */
@RestController
@RequestMapping("/api/users")
@CrossOrigin
@Validated
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 用户注册
     * POST /api/users/register
     */
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @PublicAccess("用户注册")
    public Result<UserResponse> register(@Validated @RequestBody UserRegisterRequest request) {
        try {
            User user = new User();
            user.setUsername(request.getUsername());
            user.setPassword(request.getPassword());
            user.setEmail(request.getEmail());
            user.setNickname(request.getNickname());
            
            User registeredUser = userService.register(user);
            UserResponse response = convertToResponse(registeredUser);
            
            return Result.success("注册成功", response);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 用户登录
     * POST /api/users/login
     */
    @PostMapping("/login")
    @PublicAccess("用户登录")
    public Result<UserResponse> login(@Validated @RequestBody UserLoginRequest request) {
        try {
            User user = userService.login(request.getUsername(), request.getPassword());
            UserResponse response = convertToResponse(user);
            
            return Result.success("登录成功", response);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取用户信息
     * GET /api/users/{id}
     */
    @GetMapping("/{id}")
    @PublicAccess("获取用户信息")
    public Result<UserResponse> getUserById(@PathVariable Long id) {
        User user = userService.getById(id);
        if (user == null) {
            return Result.notFound();
        }
        
        UserResponse response = convertToResponse(user);
        return Result.success(response);
    }

    /**
     * 获取用户列表（分页）
     * GET /api/users?page=1&size=10&keyword=xxx
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('EDITOR')")
    public Result<PageResult<UserResponse>> getUserList(
            @RequestParam(defaultValue = "1") Long page,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) String keyword) {
        
        Page<User> userPage = new Page<>(page, size);
        Page<User> result = userService.getUserPage(userPage, keyword);
        
        PageResult<UserResponse> pageResult = new PageResult<>();
        pageResult.setCurrent(result.getCurrent());
        pageResult.setSize(result.getSize());
        pageResult.setTotal(result.getTotal());
        pageResult.setPages(result.getPages());
        
        pageResult.setRecords(result.getRecords().stream()
                .map(this::convertToResponse)
                .toList());
        
        return Result.success(pageResult);
    }

    /**
     * 更新用户信息
     * PUT /api/users/{id}
     */
    @PutMapping("/{id}")
    @RequiresAuthentication("更新用户信息")
    @PreAuthorize("hasRole('ADMIN') or @securityUtil.isCurrentUser(#id)")
    public Result<UserResponse> updateUser(@PathVariable Long id, 
                                         @Validated @RequestBody UserUpdateRequest request) {
        User existingUser = userService.getById(id);
        if (existingUser == null) {
            return Result.notFound();
        }
        
        // 更新字段
        if (request.getEmail() != null) {
            existingUser.setEmail(request.getEmail());
        }
        if (request.getNickname() != null) {
            existingUser.setNickname(request.getNickname());
        }
        if (request.getBio() != null) {
            existingUser.setBio(request.getBio());
        }
        if (request.getAvatar() != null) {
            existingUser.setAvatar(request.getAvatar());
        }
        
        boolean success = userService.updateById(existingUser);
        if (success) {
            UserResponse response = convertToResponse(existingUser);
            return Result.success("更新成功", response);
        } else {
            return Result.error("更新失败");
        }
    }

    /**
     * 修改密码
     * PUT /api/users/{id}/password
     */
    @PutMapping("/{id}/password")
    @RequiresAuthentication("修改密码")
    @PreAuthorize("hasRole('ADMIN') or @securityUtil.isCurrentUser(#id)")
    public Result<Void> changePassword(@PathVariable Long id, 
                                     @Validated @RequestBody PasswordChangeRequest request) {
        try {
            boolean success = userService.changePassword(id, request.getCurrentPassword(), request.getNewPassword());
            if (success) {
                return Result.success("密码修改成功");
            } else {
                return Result.error("密码修改失败");
            }
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 更新用户状态（管理员功能）
     * PATCH /api/users/{id}/status
     */
    @PatchMapping("/{id}/status")
    @RequiresRole("ADMIN")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> updateUserStatus(@PathVariable Long id, 
                                        @RequestParam Integer status) {
        boolean success = userService.updateUserStatus(id, status);
        if (success) {
            return Result.success("状态更新成功");
        } else {
            return Result.error("状态更新失败");
        }
    }

    /**
     * 删除用户（逻辑删除）
     * DELETE /api/users/{id}
     */
    @DeleteMapping("/{id}")
    @RequiresRole("ADMIN")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> deleteUser(@PathVariable Long id) {
        boolean success = userService.removeById(id);
        if (success) {
            return Result.success("删除成功");
        } else {
            return Result.error("删除失败");
        }
    }

    /**
     * 检查用户名是否可用
     * GET /api/users/check-username?username=xxx
     */
    @GetMapping("/check-username")
    @PublicAccess("检查用户名可用性")
    public Result<Boolean> checkUsername(@RequestParam String username) {
        boolean exists = userService.existsByUsername(username);
        return Result.success(!exists); // 返回是否可用（不存在即可用）
    }

    /**
     * 检查邮箱是否可用
     * GET /api/users/check-email?email=xxx
     */
    @GetMapping("/check-email")
    @PublicAccess("检查邮箱可用性")
    public Result<Boolean> checkEmail(@RequestParam String email) {
        boolean exists = userService.existsByEmail(email);
        return Result.success(!exists); // 返回是否可用（不存在即可用）
    }

    /**
     * 获取当前用户信息
     * GET /api/users/me
     */
    @GetMapping("/me")
    @RequiresAuthentication("获取当前用户信息")
    public Result<UserResponse> getCurrentUser() {
        Long userId = SecurityUtil.getCurrentUserId();
        if (userId == null) {
            return Result.unauthorized();
        }
        
        User user = userService.getById(userId);
        if (user == null) {
            return Result.notFound();
        }
        
        UserResponse response = convertToResponse(user);
        return Result.success(response);
    }

    /**
     * 转换User实体为UserResponse DTO
     */
    private UserResponse convertToResponse(User user) {
        UserResponse response = new UserResponse();
        BeanUtils.copyProperties(user, response);
        return response;
    }
}