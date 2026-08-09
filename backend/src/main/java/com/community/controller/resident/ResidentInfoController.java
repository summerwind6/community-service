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
import com.community.util.DataMaskingUtils;
import com.community.util.ShiroUtils;

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
        PageVO<ResidentInfo> page = residentInfoService.getResidentList(pageDTO);
        if (!ShiroUtils.hasRole("ADMIN")) {
            page.getRecords().forEach(this::mask);
        }
        return Result.success(page);
    }

    @Operation(summary = "获取居民详情")
    @RequiresPermissions("resident:detail")
    @GetMapping("/{id}")
    public Result<ResidentInfo> detail(@PathVariable Long id) {
        ResidentInfo resident = residentInfoService.getResidentById(id);
        if (!ShiroUtils.hasRole("ADMIN")) {
            mask(resident);
        }
        return Result.success(resident);
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

    @Operation(summary = "根据手机号查询居民")
    @RequiresPermissions("resident:detail")
    @GetMapping("/by-phone")
    public Result<ResidentInfo> getByPhone(@RequestParam String phone) {
        ResidentInfo resident = residentInfoService.getResidentByPhone(phone);
        if (!ShiroUtils.hasRole("ADMIN")) {
            mask(resident);
        }
        return Result.success(resident);
    }

    private void mask(ResidentInfo resident) {
        if (resident == null) {
            return;
        }
        resident.setPhone(DataMaskingUtils.phone(resident.getPhone()));
        resident.setIdCard(DataMaskingUtils.idCard(resident.getIdCard()));
    }
}
