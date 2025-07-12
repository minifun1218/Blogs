package org.easytech.blogs.controller;

import org.easytech.blogs.common.Result;
import org.easytech.blogs.entity.Role;
import org.easytech.blogs.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色管理控制器 - RESTful API
 */
@RestController
@RequestMapping("/api/roles")
@CrossOrigin
@Validated
public class RoleController {

    private final RoleService roleService;

    @Autowired
    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    /**
     * 获取所有角色
     * GET /api/roles
     */
    @GetMapping
    public Result<List<Role>> getAllRoles(
            @RequestParam(required = false) Integer status) {
        
        List<Role> roles;
        if (status != null) {
            roles = roleService.getRolesByStatus(status);
        } else {
            roles = roleService.getAllRoles();
        }
        return Result.success(roles);
    }

    /**
     * 获取所有启用的角色（不分页）
     * GET /api/roles/enabled
     */
    @GetMapping("/enabled")
    public Result<List<Role>> getEnabledRoles() {
        List<Role> roles = roleService.getEnabledRoles();
        return Result.success(roles);
    }

    /**
     * 根据ID查询角色详情
     * GET /api/roles/{id}
     */
    @GetMapping("/{id}")
    public Result<Role> getRoleById(@PathVariable Long id) {
        Role role = roleService.getRoleById(id);
        if (role == null) {
            return Result.notFound("角色不存在");
        }
        return Result.success(role);
    }

    /**
     * 根据角色编码查询角色
     * GET /api/roles/code/{code}
     */
    @GetMapping("/code/{code}")
    public Result<Role> getRoleByCode(@PathVariable String code) {
        Role role = roleService.getRoleByCode(code);
        if (role == null) {
            return Result.notFound("角色不存在");
        }
        return Result.success(role);
    }

    /**
     * 根据角色名称查询角色
     * GET /api/roles/name/{name}
     */
    @GetMapping("/name/{name}")
    public Result<Role> getRoleByName(@PathVariable String name) {
        Role role = roleService.getRoleByName(name);
        if (role == null) {
            return Result.notFound("角色不存在");
        }
        return Result.success(role);
    }

    /**
     * 根据用户ID获取用户角色列表
     * GET /api/roles/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public Result<List<Role>> getRolesByUserId(@PathVariable Long userId) {
        List<Role> roles = roleService.getRolesByUserId(userId);
        return Result.success(roles);
    }

    /**
     * 创建新角色
     * POST /api/roles
     */
    @PostMapping
    public Result<Role> createRole(@Validated @RequestBody Role role) {
        // 检查角色编码是否已存在
        if (roleService.isRoleCodeExists(role.getCode(), null)) {
            return Result.badRequest("角色编码已存在");
        }
        
        // 检查角色名称是否已存在
        if (roleService.isRoleNameExists(role.getName(), null)) {
            return Result.badRequest("角色名称已存在");
        }
        
        boolean success = roleService.createRole(role);
        if (success) {
            return Result.success("角色创建成功", role);
        }
        return Result.error("角色创建失败");
    }

    /**
     * 更新角色信息
     * PUT /api/roles/{id}
     */
    @PutMapping("/{id}")
    public Result<Role> updateRole(@PathVariable Long id, @Validated @RequestBody Role role) {
        Role existingRole = roleService.getRoleById(id);
        if (existingRole == null) {
            return Result.notFound("角色不存在");
        }
        
        // 检查角色编码是否与其他角色冲突
        if (roleService.isRoleCodeExists(role.getCode(), id)) {
            return Result.badRequest("角色编码已存在");
        }
        
        // 检查角色名称是否与其他角色冲突
        if (roleService.isRoleNameExists(role.getName(), id)) {
            return Result.badRequest("角色名称已存在");
        }
        
        role.setId(id);
        boolean success = roleService.updateRole(role);
        if (success) {
            return Result.success("角色更新成功", role);
        }
        return Result.error("角色更新失败");
    }

    /**
     * 启用或禁用角色
     * PUT /api/roles/{id}/status
     */
    @PutMapping("/{id}/status")
    public Result<String> toggleRoleStatus(@PathVariable Long id, @RequestParam Integer status) {
        Role role = roleService.getRoleById(id);
        if (role == null) {
            return Result.notFound("角色不存在");
        }
        
        boolean success = roleService.updateRoleStatus(id, status);
        if (success) {
            String message = status == 1 ? "角色已启用" : "角色已禁用";
            return Result.success(message);
        }
        return Result.error("状态更新失败");
    }

    /**
     * 删除角色（逻辑删除）
     * DELETE /api/roles/{id}
     */
    @DeleteMapping("/{id}")
    public Result<String> deleteRole(@PathVariable Long id) {
        // 检查角色是否还有关联的用户
        Long userCount = roleService.countUsersByRoleId(id);
        if (userCount > 0) {
            return Result.badRequest("该角色下还有用户，无法删除");
        }
        
        boolean success = roleService.deleteRole(id);
        if (success) {
            return Result.success("角色删除成功");
        }
        return Result.error("角色删除失败");
    }

    /**
     * 获取角色的用户数量统计
     * GET /api/roles/user-count
     */
    @GetMapping("/user-count")
    public Result<List<Object>> getRoleUserCount() {
        // 这里需要实现一个返回所有角色及其用户数量的方法
        // 暂时返回空列表，需要在Service中实现相应方法
        return Result.success(List.of());
    }

    /**
     * 检查角色编码是否可用
     * GET /api/roles/check-code
     */
    @GetMapping("/check-code")
    public Result<Boolean> checkCodeAvailable(
            @RequestParam String code,
            @RequestParam(required = false) Long excludeId) {
        
        boolean available = !roleService.isRoleCodeExists(code, excludeId);
        return Result.success(available);
    }

    /**
     * 检查角色名称是否可用
     * GET /api/roles/check-name
     */
    @GetMapping("/check-name")
    public Result<Boolean> checkNameAvailable(
            @RequestParam String name,
            @RequestParam(required = false) Long excludeId) {
        
        boolean available = !roleService.isRoleNameExists(name, excludeId);
        return Result.success(available);
    }

    /**
     * 统计角色下的用户数量
     * GET /api/roles/{id}/user-count
     */
    @GetMapping("/{id}/user-count")
    public Result<Long> countUsersByRoleId(@PathVariable Long id) {
        Long count = roleService.countUsersByRoleId(id);
        return Result.success(count);
    }

    /**
     * 初始化默认角色
     * POST /api/roles/init-default
     */
    @PostMapping("/init-default")
    public Result<String> initDefaultRoles() {
        boolean success = roleService.initDefaultRoles();
        if (success) {
            return Result.success("默认角色初始化成功");
        }
        return Result.error("默认角色初始化失败");
    }
}