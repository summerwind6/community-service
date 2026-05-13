package com.community.controller.system;

import com.community.common.Result;
import com.community.entity.po.SysConfig;
import com.community.service.SysConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "系统配置", description = "系统基础参数配置")
@RestController
@RequestMapping("/config")
public class ConfigController {

    @Autowired
    private SysConfigService sysConfigService;

    @Operation(summary = "获取所有配置")
    @RequiresPermissions("user:list")
    @GetMapping("/list")
    public Result<List<SysConfig>> list() {
        return Result.success(sysConfigService.getAllConfigs());
    }

    @Operation(summary = "更新配置")
    @RequiresPermissions("user:update")
    @PutMapping
    public Result<Void> update(@RequestBody List<SysConfig> configs) {
        sysConfigService.updateConfigs(configs);
        return Result.success();
    }
}
