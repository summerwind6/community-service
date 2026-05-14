package com.community.controller.stat;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.community.common.Result;
import com.community.entity.po.ApplicationForm;
import com.community.mapper.ApplicationFormMapper;
import com.community.mapper.ResidentInfoMapper;
import com.community.mapper.SysUserMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Tag(name = "统计分析", description = "数据统计与分析接口")
@RestController
@RequestMapping("/stat")
public class StatController {

    @Autowired
    private ApplicationFormMapper applicationFormMapper;

    @Autowired
    private ResidentInfoMapper residentInfoMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Operation(summary = "获取仪表盘统计数据")
    @RequiresPermissions("stat:dashboard")
    @GetMapping("/dashboard")
    public Result<Map<String, Object>> dashboard() {
        long totalUsers = sysUserMapper.selectCount(null);
        long totalApplies = applicationFormMapper.selectCount(null);
        long pendingAudit = applicationFormMapper.selectCount(
                new LambdaQueryWrapper<ApplicationForm>().eq(ApplicationForm::getStatus, "PENDING"));
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        long completedToday = applicationFormMapper.selectCount(
                new LambdaQueryWrapper<ApplicationForm>()
                        .eq(ApplicationForm::getStatus, "COMPLETED")
                        .ge(ApplicationForm::getUpdateTime, todayStart));

        Map<String, Object> data = new HashMap<>();
        data.put("totalUsers", totalUsers);
        data.put("totalApplies", totalApplies);
        data.put("pendingAudit", pendingAudit);
        data.put("completedToday", completedToday);
        return Result.success(data);
    }

    @Operation(summary = "申请趋势统计（近7天）")
    @RequiresPermissions("stat:trend")
    @GetMapping("/trend")
    public Result<Map<String, Object>> trend() {
        LocalDate today = LocalDate.now();
        List<String> dates = new ArrayList<>();
        List<Long> counts = new ArrayList<>();

        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            dates.add(date.format(DateTimeFormatter.ofPattern("MM-dd")));
            LocalDateTime dayStart = date.atStartOfDay();
            LocalDateTime dayEnd = date.plusDays(1).atStartOfDay();
            long count = applicationFormMapper.selectCount(
                    new LambdaQueryWrapper<ApplicationForm>()
                            .ge(ApplicationForm::getCreateTime, dayStart)
                            .lt(ApplicationForm::getCreateTime, dayEnd));
            counts.add(count);
        }

        Map<String, Object> data = new HashMap<>();
        data.put("dates", dates);
        data.put("counts", counts);
        return Result.success(data);
    }

    @Operation(summary = "服务类型分布")
    @RequiresPermissions("stat:distribution")
    @GetMapping("/distribution")
    public Result<Map<String, Object>> distribution() {
        // 查询所有申请，按服务事项分组统计
        List<ApplicationForm> all = applicationFormMapper.selectList(null);
        Map<String, Long> groupMap = new LinkedHashMap<>();
        for (ApplicationForm app : all) {
            String name = app.getServiceItemName();
            if (name == null) name = "未分类";
            groupMap.merge(name, 1L, Long::sum);
        }

        List<String> categories = new ArrayList<>(groupMap.keySet());
        List<Long> values = new ArrayList<>(groupMap.values());

        Map<String, Object> data = new HashMap<>();
        data.put("categories", categories);
        data.put("values", values);
        return Result.success(data);
    }
}
