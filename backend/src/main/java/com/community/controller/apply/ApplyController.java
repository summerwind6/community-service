package com.community.controller.apply;

import com.community.common.Result;
import com.community.entity.po.ApplicationForm;
import com.community.service.impl.ApplicationFormServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "申请管理", description = "便民服务申请单处理")
@RestController
@RequestMapping("/apply")
public class ApplyController {

    @Autowired
    private ApplicationFormServiceImpl applicationFormService;

    @Operation(summary = "提交申请")
    @PostMapping
    public Result<ApplicationForm> submit(@RequestBody ApplicationForm applicationForm) {
        applicationFormService.save(applicationForm);
        return Result.success(applicationForm);
    }

    @Operation(summary = "获取我的申请列表")
    @GetMapping("/my")
    public Result<?> myApplyList() {
        return Result.success(applicationFormService.list());
    }

    @Operation(summary = "获取所有申请列表（管理员）")
    @RequiresPermissions("apply:list")
    @GetMapping("/list")
    public Result<?> list() {
        return Result.success(applicationFormService.list());
    }

    @Operation(summary = "审核申请")
    @RequiresPermissions("apply:audit")
    @PutMapping("/audit/{id}")
    public Result<Void> audit(@PathVariable Long id, @RequestBody ApplicationForm applicationForm) {
        applicationForm.setId(id);
        applicationFormService.updateById(applicationForm);
        return Result.success();
    }
}
