package com.community.controller.guide;

import com.community.common.Result;
import com.community.entity.dto.PageDTO;
import com.community.entity.po.Guide;
import com.community.entity.vo.PageVO;
import com.community.service.GuideService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "办事指南管理", description = "办事指南的增删改查与公开浏览")
@RestController
@RequestMapping("/guide")
public class GuideController {

    @Autowired
    private GuideService guideService;

    @Operation(summary = "获取办事指南列表（分页+筛选）")
    @GetMapping("/list")
    public Result<PageVO<Guide>> list(PageDTO pageDTO, @RequestParam(required = false) String category) {
        return Result.success(guideService.getGuideList(pageDTO, category));
    }

    @Operation(summary = "获取公开办事指南列表")
    @GetMapping("/public")
    public Result<List<Guide>> publicList() {
        return Result.success(guideService.getPublicGuides());
    }

    @Operation(summary = "获取办事指南详情")
    @GetMapping("/{id}")
    public Result<Guide> detail(@PathVariable Long id) {
        return Result.success(guideService.getGuideById(id));
    }

    @Operation(summary = "新增办事指南")
    @RequiresPermissions("guide:manage")
    @PostMapping
    public Result<Guide> add(@RequestBody Guide guide) {
        return Result.success(guideService.addGuide(guide));
    }

    @Operation(summary = "更新办事指南")
    @RequiresPermissions("guide:manage")
    @PutMapping
    public Result<Guide> update(@RequestBody Guide guide) {
        return Result.success(guideService.updateGuide(guide));
    }

    @Operation(summary = "删除办事指南")
    @RequiresPermissions("guide:manage")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        guideService.removeGuide(id);
        return Result.success();
    }
}
