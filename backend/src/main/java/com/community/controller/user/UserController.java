package com.community.controller.user;

import com.baomidou.mybatisplus.extension.service.IService;
import com.community.common.Result;
import com.community.entity.dto.PageDTO;
import com.community.entity.dto.UserDTO;
import com.community.entity.po.SysUser;
import com.community.entity.vo.PageVO;
import com.community.entity.vo.UserVO;
import com.community.mapper.SysUserMapper;
import com.community.service.SysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "用户管理", description = "系统用户CRUD操作")
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private SysUserService sysUserService;

    @Operation(summary = "获取用户列表")
    @RequiresPermissions("user:list")
    @GetMapping("/list")
    public Result<PageVO<UserVO>> list(PageDTO pageDTO) {
        PageVO<UserVO> pageVO = sysUserService.getUserList(pageDTO);
        return Result.success(pageVO);
    }

    @Operation(summary = "获取用户详情")
    @RequiresPermissions("user:detail")
    @GetMapping("/{id}")
    public Result<UserVO> detail(@PathVariable Long id) {
        UserVO userVO = sysUserService.getUserInfo(id);
        return Result.success(userVO);
    }

    @Operation(summary = "新增用户")
    @RequiresPermissions("user:add")
    @PostMapping
    public Result<Void> add(@RequestBody UserDTO userDTO, @RequestParam(required = false) Long roleId) {
        sysUserService.register(userDTO, roleId);
        return Result.success();
    }

    @Operation(summary = "更新用户")
    @RequiresPermissions("user:update")
    @PutMapping
    public Result<UserVO> update(@RequestBody UserDTO userDTO) {
        UserVO userVO = sysUserService.updateUser(userDTO);
        return Result.success(userVO);
    }

    @Operation(summary = "删除用户")
    @RequiresPermissions("user:update")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        sysUserService.removeById(id);
        return Result.success();
    }
}
