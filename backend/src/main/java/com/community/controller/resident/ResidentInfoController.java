package com.community.controller.resident;

import com.community.common.Result;
import com.community.entity.dto.PageDTO;
import com.community.entity.po.ResidentInfo;
import com.community.entity.vo.PageVO;
import com.community.service.ResidentInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "居民信息管理", description = "社区居民基础信息CRUD操作")
@RestController
@RequestMapping("/resident")
public class ResidentInfoController {

    @Autowired
    private ResidentInfoService residentInfoService;

    @Operation(summary = "获取居民列表（分页）")
    @RequiresPermissions("resident:list")
    @GetMapping("/list")
    public Result<PageVO<ResidentInfo>> list(PageDTO pageDTO) {
        return Result.success(residentInfoService.getResidentList(pageDTO));
    }

    @Operation(summary = "获取居民详情")
    @RequiresPermissions("resident:detail")
    @GetMapping("/{id}")
    public Result<ResidentInfo> detail(@PathVariable Long id) {
        return Result.success(residentInfoService.getResidentById(id));
    }

    @Operation(summary = "新增居民")
    @RequiresPermissions("resident:add")
    @PostMapping
    public Result<ResidentInfo> add(@RequestBody ResidentInfo resident) {
        return Result.success(residentInfoService.addResident(resident));
    }

    @Operation(summary = "更新居民信息")
    @RequiresPermissions("resident:update")
    @PutMapping
    public Result<ResidentInfo> update(@RequestBody ResidentInfo resident) {
        return Result.success(residentInfoService.updateResident(resident));
    }

    @Operation(summary = "删除居民")
    @RequiresPermissions("resident:delete")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        residentInfoService.removeById(id);
        return Result.success();
    }

    @Operation(summary = "根据手机号查询居民（免登录）")
    @GetMapping("/by-phone")
    public Result<ResidentInfo> getByPhone(@RequestParam String phone) {
        return Result.success(residentInfoService.getResidentByPhone(phone));
    }
}
