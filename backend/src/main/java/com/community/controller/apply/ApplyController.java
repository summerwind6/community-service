package com.community.controller.apply;

import com.community.common.Result;
import com.community.entity.dto.PageDTO;
import com.community.entity.dto.AuditRequestDTO;
import com.community.entity.dto.PublicProgressQueryDTO;
import com.community.entity.po.ApplicationForm;
import com.community.entity.po.AuditRecord;
import com.community.entity.vo.PageVO;
import com.community.entity.vo.UserVO;
import com.community.service.ApplicationFormService;
import com.community.service.SysUserService;
import com.community.service.ApplicationAccessService;
import com.community.util.ShiroUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import jakarta.servlet.http.HttpServletRequest;
import com.community.service.PublicRateLimitService;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;

@Tag(name = "申请管理", description = "便民服务申请提交、审核流转")
@RestController
@RequestMapping("/apply")
public class ApplyController {

    @Autowired
    private ApplicationFormService applicationFormService;

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private PublicRateLimitService publicRateLimitService;

    @Autowired
    private ApplicationAccessService applicationAccessService;

    @Value("${app.public.progress-ip-limit-per-minute}")
    private int progressIpLimit;

    /** 禁止 status 绑定到 PageDTO（PageDTO.status 是 Integer，申请状态是 String） */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.setDisallowedFields("status");
    }

    @Operation(summary = "提交申请")
    @PostMapping
    public Result<ApplicationForm> submit(@RequestBody ApplicationForm form) {
        return Result.success(applicationFormService.submitApplication(form));
    }

    @Operation(summary = "手机号查询申请进度")
    @GetMapping("/progress")
    public Result<Object> progress(@Valid PublicProgressQueryDTO query, HttpServletRequest request) {
        publicRateLimitService.check("public-progress-ip", clientIp(request), progressIpLimit, 60);
        return Result.success(applicationFormService.getPublicProgress(query.getApplyNo(), query.getPhoneLast4()));
    }

    @Operation(summary = "获取申请列表（分页+筛选）")
    @RequiresPermissions("apply:list")
    @GetMapping("/list")
    public Result<PageVO<ApplicationForm>> list(PageDTO pageDTO,
                                                @RequestParam(required = false) String status) {
        PageVO<ApplicationForm> page = applicationFormService.getApplicationList(pageDTO, status);
        UserVO user = getCurrentUser();
        if (!user.getRoles().contains("ADMIN")) {
            List<ApplicationForm> visible = new ArrayList<>();
            for (ApplicationForm form : page.getRecords()) {
                try {
                    applicationAccessService.assertCanView(form, user);
                    visible.add(form);
                } catch (com.community.common.BusinessException ignored) {
                    // Deliberately omit out-of-scope applications from the response.
                }
            }
            page.setRecords(visible);
            page.setTotal((long) visible.size());
            page.setPages(visible.isEmpty() ? 0 : 1);
        }
        return Result.success(page);
    }

    @Operation(summary = "获取申请详情")
    @GetMapping("/{id}")
    public Result<ApplicationForm> detail(@PathVariable Long id) {
        ApplicationForm form = applicationFormService.getApplicationDetail(id);
        applicationAccessService.assertCanView(form, getCurrentUser());
        return Result.success(form);
    }

    @Operation(summary = "获取申请的审核记录")
    @GetMapping("/{id}/audit-records")
    public Result<List<AuditRecord>> auditRecords(@PathVariable Long id) {
        applicationAccessService.assertCanView(applicationFormService.getApplicationDetail(id), getCurrentUser());
        return Result.success(applicationFormService.getAuditRecords(id));
    }

    @Operation(summary = "初审（社区受理员）PENDING→PROCESSING/REJECTED")
    @RequiresPermissions("apply:review")
    @PostMapping("/{id}/review")
    public Result<Void> review(@PathVariable Long id, @Valid @RequestBody AuditRequestDTO body) {
        String result = body.getResult();
        String opinion = body.getOpinion() == null ? "" : body.getOpinion();
        UserVO user = getCurrentUser();
        applicationAccessService.assertCanReview(applicationFormService.getApplicationDetail(id), user);
        applicationFormService.reviewApplication(id, result, opinion, user.getId(), user.getRealName());
        return Result.success();
    }

    @Operation(summary = "复审（街道审核员）PROCESSING→COMPLETED/REJECTED")
    @RequiresPermissions("apply:approve")
    @PostMapping("/{id}/approve")
    public Result<Void> approve(@PathVariable Long id, @Valid @RequestBody AuditRequestDTO body) {
        String result = body.getResult();
        String opinion = body.getOpinion() == null ? "" : body.getOpinion();
        UserVO user = getCurrentUser();
        applicationAccessService.assertCanApprove(applicationFormService.getApplicationDetail(id), user);
        applicationFormService.approveApplication(id, result, opinion, user.getId(), user.getRealName());
        return Result.success();
    }

    private UserVO getCurrentUser() {
        String username = ShiroUtils.getCurrentUsername();
        return sysUserService.getUserInfoByUsername(username);
    }

    private String clientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        return forwarded == null || forwarded.isBlank() ? request.getRemoteAddr() : forwarded.split(",")[0].trim();
    }
}
