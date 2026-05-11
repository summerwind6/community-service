package com.community.controller.stat;

import com.community.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "统计分析", description = "数据统计与分析接口")
@RestController
@RequestMapping("/stat")
public class StatController {

    @Operation(summary = "获取仪表盘统计数据")
    @RequiresPermissions("stat:dashboard")
    @GetMapping("/dashboard")
    public Result<Map<String, Object>> dashboard() {
        Map<String, Object> data = new HashMap<>();
        data.put("totalUsers", 0);
        data.put("totalApplies", 0);
        data.put("pendingAudit", 0);
        data.put("completedToday", 0);
        return Result.success(data);
    }

    @Operation(summary = "申请趋势统计")
    @RequiresPermissions("stat:trend")
    @GetMapping("/trend")
    public Result<Map<String, Object>> trend() {
        Map<String, Object> data = new HashMap<>();
        data.put("dates", new String[7]);
        data.put("counts", new Integer[7]);
        return Result.success(data);
    }

    @Operation(summary = "服务类型分布")
    @RequiresPermissions("stat:distribution")
    @GetMapping("/distribution")
    public Result<Map<String, Object>> distribution() {
        Map<String, Object> data = new HashMap<>();
        data.put("categories", new String[5]);
        data.put("values", new Integer[5]);
        return Result.success(data);
    }
}
