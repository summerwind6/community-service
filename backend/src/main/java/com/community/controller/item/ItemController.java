package com.community.controller.item;

import com.community.common.Result;
import com.community.entity.dto.PageDTO;
import com.community.entity.po.ServiceItem;
import com.community.entity.vo.PageVO;
import com.community.service.ServiceItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "服务项目管理", description = "便民服务项目CRUD操作")
@RestController
@RequestMapping("/item")
public class ItemController {

    @Autowired
    private ServiceItemService serviceItemService;

    @Operation(summary = "获取服务项目列表（分页+筛选）")
    @GetMapping("/list")
    public Result<PageVO<ServiceItem>> list(PageDTO pageDTO) {
        return Result.success(serviceItemService.getItemList(pageDTO));
    }

    @Operation(summary = "获取已启用的服务项目")
    @GetMapping("/enabled")
    public Result<List<ServiceItem>> enabledList() {
        return Result.success(serviceItemService.getEnabledItems());
    }

    @Operation(summary = "获取服务项目详情")
    @RequiresPermissions("item:detail")
    @GetMapping("/{id}")
    public Result<ServiceItem> detail(@PathVariable Long id) {
        return Result.success(serviceItemService.getItemById(id));
    }

    @Operation(summary = "新增服务项目")
    @RequiresPermissions("item:add")
    @PostMapping
    public Result<ServiceItem> add(@RequestBody ServiceItem serviceItem) {
        return Result.success(serviceItemService.addItem(serviceItem));
    }

    @Operation(summary = "更新服务项目")
    @RequiresPermissions("item:update")
    @PutMapping
    public Result<ServiceItem> update(@RequestBody ServiceItem serviceItem) {
        return Result.success(serviceItemService.updateItem(serviceItem));
    }

    @Operation(summary = "切换服务项目启用/停用状态")
    @RequiresPermissions("item:update")
    @PutMapping("/{id}/status")
    public Result<Void> toggleStatus(@PathVariable Long id, @RequestParam Integer status) {
        serviceItemService.updateItemStatus(id, status);
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
