package com.community.controller.apply;

import com.community.common.Result;
import com.community.entity.dto.PageDTO;
import com.community.entity.po.ApplicationForm;
import com.community.entity.po.AuditRecord;
import com.community.entity.vo.PageVO;
import com.community.entity.vo.UserVO;
import com.community.service.ApplicationFormService;
import com.community.service.SysUserService;
import com.community.util.ShiroUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "申请管理", description = "便民服务申请提交、审核流转")
@RestController
@RequestMapping("/apply")
public class ApplyController {

    @Autowired
    private ApplicationFormService applicationFormService;

    @Autowired
    private SysUserService sysUserService;

    @Operation(summary = "提交申请")
    @PostMapping
    public Result<ApplicationForm> submit(@RequestBody ApplicationForm form) {
        return Result.success(applicationFormService.submitApplication(form));
    }

    @Operation(summary = "获取申请列表（分页+筛选）")
    @RequiresPermissions("apply:list")
    @GetMapping("/list")
    public Result<PageVO<ApplicationForm>> list(PageDTO pageDTO,
                                                 @RequestParam(required = false) String status) {
        return Result.success(applicationFormService.getApplicationList(pageDTO));
    }

    @Operation(summary = "居民进度查询（免登录，手机号查申请）")
    @GetMapping("/progress")
    public Result<List<ApplicationForm>> progress(@RequestParam String phone) {
        return Result.success(applicationFormService.getApplicationsByPhone(phone));
    }

    @Operation(summary = "获取申请详情")
    @GetMapping("/{id}")
    public Result<ApplicationForm> detail(@PathVariable Long id) {
        return Result.success(applicationFormService.getApplicationDetail(id));
    }

    @Operation(summary = "获取申请的审核记录")
    @GetMapping("/{id}/audit-records")
    public Result<List<AuditRecord>> auditRecords(@PathVariable Long id) {
        return Result.success(applicationFormService.getAuditRecords(id));
    }

    @Operation(summary = "初审（社区受理员）PENDING→PROCESSING/REJECTED")
    @RequiresPermissions("apply:audit")
    @PostMapping("/{id}/review")
    public Result<Void> review(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String result = body.get("result");
        String opinion = body.getOrDefault("opinion", "");
        UserVO user = getCurrentUser();
        applicationFormService.reviewApplication(id, result, opinion, user.getId(), user.getRealName());
        return Result.success();
    }

    @Operation(summary = "复审（街道审核员）PROCESSING→COMPLETED/REJECTED")
    @RequiresPermissions("apply:audit")
    @PostMapping("/{id}/approve")
    public Result<Void> approve(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String result = body.get("result");
        String opinion = body.getOrDefault("opinion", "");
        UserVO user = getCurrentUser();
        applicationFormService.approveApplication(id, result, opinion, user.getId(), user.getRealName());
        return Result.success();
    }

    private UserVO getCurrentUser() {
        String username = ShiroUtils.getCurrentUsername();
        return sysUserService.getUserInfoByUsername(username);
    }
}
