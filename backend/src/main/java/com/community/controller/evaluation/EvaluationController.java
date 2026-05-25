package com.community.controller.evaluation;

import com.community.common.Result;
import com.community.entity.dto.PageDTO;
import com.community.entity.po.Evaluation;
import com.community.entity.vo.PageVO;
import com.community.service.EvaluationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "Evaluation", description = "Service evaluation management")
@RestController
@RequestMapping("/evaluation")
public class EvaluationController {

    @Autowired
    private EvaluationService evaluationService;

    @Operation(summary = "Evaluation list")
    @GetMapping("/list")
    public Result<PageVO<Evaluation>> list(PageDTO pageDTO,
                                           @RequestParam(required = false) Integer score) {
        return Result.success(evaluationService.getEvaluationList(pageDTO, score));
    }

    @Operation(summary = "Submit evaluation")
    @PostMapping
    public Result<Evaluation> submit(@RequestBody Evaluation evaluation) {
        return Result.success(evaluationService.submitEvaluation(evaluation));
    }

    @Operation(summary = "Evaluation stats")
    @GetMapping("/stats")
    public Result<Map<String, Object>> stats() {
        return Result.success(evaluationService.getEvaluationStats());
    }
}
