package com.community.controller.warn;

import com.community.common.Result;
import com.community.entity.dto.PageDTO;
import com.community.entity.vo.PageVO;
import com.community.entity.vo.TimeoutWarnVO;
import com.community.service.TimeoutWarnService;
import com.community.util.ShiroUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "超时预警", description = "申请超时列表、统计与催办")
@RestController
@RequestMapping("/warn")
public class TimeoutWarnController {

    @Autowired
    private TimeoutWarnService timeoutWarnService;

    @Operation(summary = "超时申请列表（分页+超时天数排序）")
    @GetMapping("/timeout-list")
    public Result<PageVO<TimeoutWarnVO>> timeoutList(PageDTO pageDTO) {
        return Result.success(timeoutWarnService.getTimeoutWarnList(pageDTO.getPageNum(), pageDTO.getPageSize()));
    }

    @Operation(summary = "超时数量")
    @GetMapping("/timeout-count")
    public Result<Map<String, Object>> timeoutCount() {
        return Result.success(Map.of("count", timeoutWarnService.getTimeoutWarnCount()));
    }

    @Operation(summary = "催办超时申请")
    @RequiresPermissions("apply:review")
    @PutMapping("/{id}/urge")
    public Result<Void> urge(@PathVariable Long id) {
        if (!ShiroUtils.hasRole("ADMIN")) {
            return Result.error(403, "只有管理员可以使用催办功能");
        }
        String operatorName = ShiroUtils.getCurrentUsername();
        if (operatorName == null || operatorName.isBlank()) {
            return Result.error(401, "未登录或登录已过期");
        }
        timeoutWarnService.urgeApplication(id, null, operatorName);
        return Result.success("催办成功", null);
    }

    @Operation(summary = "手动触发系统超时预警扫描")
    @RequiresPermissions("apply:review")
    @PostMapping("/system-scan")
    public Result<Void> systemScan() {
        if (!ShiroUtils.hasRole("ADMIN")) {
            return Result.error(403, "只有管理员可触发系统预警扫描");
        }
        timeoutWarnService.createSystemWarnings();
        return Result.success();
    }
}
