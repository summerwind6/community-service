package com.community.controller.auth;

import com.community.common.Result;
import com.community.entity.dto.LoginDTO;
import com.community.entity.vo.LoginVO;
import com.community.entity.vo.UserVO;
import com.community.service.SysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "认证管理", description = "登录、登出、用户信息相关接口")
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private SysUserService sysUserService;

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<LoginVO> login(@RequestBody LoginDTO loginDTO) {
        LoginVO loginVO = new LoginVO();
        UserVO user = sysUserService.login(loginDTO.getUsername(), loginDTO.getPassword());
        loginVO.setUser(user);
        return Result.success(loginVO);
    }

    @Operation(summary = "用户登出")
    @RequiresAuthentication
    @PostMapping("/logout")
    public Result<Void> logout() {
        org.apache.shiro.SecurityUtils.getSubject().logout();
        return Result.success();
    }

    @Operation(summary = "获取当前用户信息")
    @RequiresAuthentication
    @GetMapping("/info")
    public Result<UserVO> getUserInfo() {
        String username = (String) org.apache.shiro.SecurityUtils.getSubject().getPrincipal();
        UserVO userInfo =  sysUserService.getUserInfoByUsername(username);
        return Result.success(userInfo);
    }

    @Operation(summary = "未授权提示")
    @GetMapping("/unauthorized")
    public Result<String> unauthorized() {
        return Result.error(401, "未登录或登录已过期");
    }
}
