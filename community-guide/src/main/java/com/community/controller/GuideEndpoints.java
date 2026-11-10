package com.community.controller;

import com.community.common.BusinessException;
import com.community.common.Result;
import com.community.entity.dto.PageDTO;
import com.community.entity.po.Guide;
import com.community.entity.vo.PageVO;
import com.community.service.GuideService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class GuideEndpoints {
    private static final Logger log = LoggerFactory.getLogger(GuideEndpoints.class);
    private final GuideService service;

    public GuideEndpoints(GuideService service) {
        this.service = service;
    }

    @GetMapping("/guide/public")
    public Result<List<Guide>> publicList() {
        log.debug("Public guide request traceId={}", MDC.get("traceId"));
        return Result.success(service.getPublicGuides());
    }

    @GetMapping("/internal/guide")
    public Result<PageVO<Guide>> list(PageDTO page, @RequestParam(required = false) String category) {
        return Result.success(service.getGuideList(page, category));
    }

    @GetMapping("/internal/guide/{id}")
    public Result<Guide> detail(@PathVariable Long id) {
        return Result.success(service.getGuideById(id));
    }

    @PostMapping("/internal/guide")
    public Result<Guide> add(@RequestBody Guide guide) {
        return Result.success(service.addGuide(guide));
    }

    @PutMapping("/internal/guide")
    public Result<Guide> update(@RequestBody Guide guide) {
        return Result.success(service.updateGuide(guide));
    }

    @DeleteMapping("/internal/guide/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        service.removeGuide(id);
        return Result.success();
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Result<Void>> businessFailure(BusinessException e) {
        return ResponseEntity.status(e.getCode() == 503 ? 503 : 200)
                .body(Result.error(e.getCode(), e.getMessage()));
    }

    @ExceptionHandler(DataAccessException.class)
    public Result<Void> dataFailure(Exception e) {
        log.error("Guide database operation failed", e);
        return Result.error("系统繁忙，请稍后再试");
    }
}
