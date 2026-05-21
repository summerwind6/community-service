package com.community.controller.auth;

import com.community.common.Result;
import com.community.entity.po.SysPermission;
import com.community.entity.po.SysRole;
import com.community.service.SysRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "角色管理", description = "角色CRUD与权限分配")
@RestController
@RequestMapping("/role")
public class RoleController {

    @Autowired
    private SysRoleService sysRoleService;

    @Operation(summary = "获取所有角色")
    @RequiresPermissions("role:list")
    @GetMapping("/list")
    public Result<List<SysRole>> list() {
        return Result.success(sysRoleService.getAllRoles());
    }

    @Operation(summary = "获取角色详情")
    @RequiresPermissions("role:list")
    @GetMapping("/{id}")
    public Result<SysRole> detail(@PathVariable Long id) {
        return Result.success(sysRoleService.getRoleById(id));
    }

    @Operation(summary = "新增角色")
    @RequiresPermissions("user:add")
    @PostMapping
    public Result<SysRole> add(@RequestBody SysRole role) {
        return Result.success(sysRoleService.addRole(role));
    }

    @Operation(summary = "更新角色")
    @RequiresPermissions("user:update")
    @PutMapping
    public Result<SysRole> update(@RequestBody SysRole role) {
        return Result.success(sysRoleService.updateRole(role));
    }

    @Operation(summary = "删除角色")
    @RequiresPermissions("user:update")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        sysRoleService.removeById(id);
        return Result.success();
    }

    @Operation(summary = "获取角色的权限列表")
    @RequiresPermissions("role:list")
    @GetMapping("/{roleId}/permissions")
    public Result<List<SysPermission>> getPermissions(@PathVariable Long roleId) {
        return Result.success(sysRoleService.getPermissionsByRoleId(roleId));
    }

    @Operation(summary = "分配角色权限")
    @RequiresPermissions("user:update")
    @PutMapping("/{roleId}/permissions")
    public Result<Void> assignPermissions(@PathVariable Long roleId, @RequestBody Map<String, List<Long>> params) {
        sysRoleService.assignPermissions(roleId, params.get("permIds"));
        return Result.success();
    }
}
