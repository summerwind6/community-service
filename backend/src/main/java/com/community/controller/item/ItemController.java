package com.community.controller.item;

import com.community.common.Result;
import com.community.entity.po.ServiceItem;
import com.community.service.impl.ServiceItemServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "服务项目管理", description = "便民服务项目CRUD操作")
@RestController
@RequestMapping("/item")
public class ItemController {

    @Autowired
    private ServiceItemServiceImpl serviceItemService;

    @Operation(summary = "获取服务项目列表")
    @GetMapping("/list")
    public Result<?> list() {
        return Result.success(serviceItemService.list());
    }

    @Operation(summary = "获取服务项目详情")
    @RequiresPermissions("item:detail")
    @GetMapping("/{id}")
    public Result<ServiceItem> detail(@PathVariable Long id) {
        return Result.success(serviceItemService.getById(id));
    }

    @Operation(summary = "新增服务项目")
    @RequiresPermissions("item:add")
    @PostMapping
    public Result<Void> add(@RequestBody ServiceItem serviceItem) {
        serviceItemService.save(serviceItem);
        return Result.success();
    }

    @Operation(summary = "更新服务项目")
    @RequiresPermissions("item:update")
    @PutMapping
    public Result<Void> update(@RequestBody ServiceItem serviceItem) {
        serviceItemService.updateById(serviceItem);
        return Result.success();
    }

    @Operation(summary = "删除服务项目")
    @RequiresPermissions("item:delete")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        serviceItemService.removeById(id);
        return Result.success();
    }
}
