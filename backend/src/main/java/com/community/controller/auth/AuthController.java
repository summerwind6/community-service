package com.community.controller.auth;

import com.community.common.Result;
import com.community.entity.dto.LoginDTO;
import com.community.entity.dto.UserDTO;
import com.community.entity.vo.LoginVO;
import com.community.entity.vo.UserVO;
import com.community.service.SysUserService;
import com.community.util.ShiroUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "认证管理", description = "登录、登出、个人信息相关接口")
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private SysUserService sysUserService;

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<LoginVO> login(@RequestBody LoginDTO loginDTO) {
        LoginVO loginVO = sysUserService.login(loginDTO.getUsername(), loginDTO.getPassword());
        return Result.success(loginVO);
    }

    @Operation(summary = "用户登出")
    @RequiresAuthentication
    @PostMapping("/logout")
    public Result<Void> logout() {
        ShiroUtils.logout();
        return Result.success();
    }

    @Operation(summary = "获取当前用户信息")
    @RequiresAuthentication
    @GetMapping("/info")
    public Result<UserVO> getUserInfo() {
        String username = ShiroUtils.getCurrentUsername();
        UserVO userInfo = sysUserService.getUserInfoByUsername(username);
        return Result.success(userInfo);
    }

    @Operation(summary = "修改密码")
    @RequiresAuthentication
    @PutMapping("/change-password")
    public Result<Void> changePassword(@RequestBody Map<String, String> params) {
        String username = ShiroUtils.getCurrentUsername();
        UserVO user = sysUserService.getUserInfoByUsername(username);
        sysUserService.changePassword(user.getId(), params.get("oldPassword"), params.get("newPassword"));
        return Result.success();
    }

    @Operation(summary = "更新个人资料")
    @RequiresAuthentication
    @PutMapping("/profile")
    public Result<UserVO> updateProfile(@RequestBody UserDTO userDTO) {
        String username = ShiroUtils.getCurrentUsername();
        UserVO currentUser = sysUserService.getUserInfoByUsername(username);
        UserVO updated = sysUserService.updateProfile(currentUser.getId(), userDTO);
        return Result.success(updated);
    }

    @Operation(summary = "未授权提示")
    @GetMapping("/unauthorized")
    public Result<String> unauthorized() {
        return Result.error(401, "未登录或登录已过期");
    }
}
