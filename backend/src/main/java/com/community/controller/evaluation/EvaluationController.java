package com.community.controller.evaluation;

import com.community.common.Result;
import com.community.config.ClientAddress;
import com.community.config.PublicLimits;
import com.community.entity.dto.PageDTO;
import com.community.entity.po.Evaluation;
import com.community.entity.vo.PageVO;
import com.community.entity.dto.PublicEvaluationDTO;
import com.community.service.EvaluationService;
import com.community.service.PublicRateLimitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.apache.shiro.authz.annotation.RequiresPermissions;

import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

@Tag(name = "Evaluation", description = "Service evaluation management")
@RestController
@RequestMapping("/evaluation")
public class EvaluationController {

    @Autowired
    private EvaluationService evaluationService;

    @Autowired
    private PublicRateLimitService publicRateLimitService;

    @Autowired
    private ClientAddress clientAddress;

    @Autowired
    private PublicLimits publicLimits;

    @Operation(summary = "Evaluation list")
    @GetMapping("/list")
    @RequiresPermissions("evaluation:list")
    public Result<PageVO<Evaluation>> list(PageDTO pageDTO,
                                           @RequestParam(required = false) Integer score) {
        return Result.success(evaluationService.getEvaluationList(pageDTO, score));
    }

    @Operation(summary = "Submit evaluation")
    @PostMapping
    @RequiresPermissions("evaluation:list")
    public Result<Evaluation> submit(@RequestBody Evaluation evaluation) {
        return Result.success(evaluationService.submitEvaluation(evaluation));
    }

    @Operation(summary = "公开提交服务评价")
    @PostMapping("/public")
    public Result<Evaluation> submitPublic(@Valid @RequestBody PublicEvaluationDTO evaluation,
                                           HttpServletRequest request) {
        publicRateLimitService.check("public-evaluation-ip", clientAddress.resolve(request), publicLimits.evaluationIpLimit(), 60);
        return Result.success(evaluationService.submitPublicEvaluation(evaluation));
    }

    @Operation(summary = "Evaluation stats")
    @GetMapping("/stats")
    @RequiresPermissions("evaluation:list")
    public Result<Map<String, Object>> stats() {
        return Result.success(evaluationService.getEvaluationStats());
    }

}
