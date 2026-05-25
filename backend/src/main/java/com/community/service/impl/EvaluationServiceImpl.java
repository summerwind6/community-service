package com.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.community.common.BusinessException;
import com.community.entity.dto.PageDTO;
import com.community.entity.po.ApplicationForm;
import com.community.entity.po.Evaluation;
import com.community.entity.vo.PageVO;
import com.community.mapper.ApplicationFormMapper;
import com.community.mapper.EvaluationMapper;
import com.community.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EvaluationServiceImpl extends ServiceImpl<EvaluationMapper, Evaluation> implements EvaluationService {

    @Autowired
    private ApplicationFormMapper applicationFormMapper;

    @Override
    public Evaluation submitEvaluation(Evaluation evaluation) {
        if (evaluation.getApplicationId() == null) {
            throw new BusinessException("Application id is required");
        }
        if (evaluation.getScore() == null || evaluation.getScore() < 1 || evaluation.getScore() > 5) {
            throw new BusinessException("Score must be between 1 and 5");
        }
        ApplicationForm application = applicationFormMapper.selectById(evaluation.getApplicationId());
        if (application == null) {
            throw new BusinessException("Application not found");
        }
        if (!"COMPLETED".equals(application.getStatus())) {
            throw new BusinessException("Only completed applications can be evaluated");
        }
        LambdaQueryWrapper<Evaluation> exists = new LambdaQueryWrapper<>();
        exists.eq(Evaluation::getApplicationId, evaluation.getApplicationId());
        if (count(exists) > 0) {
            throw new BusinessException("This application has already been evaluated");
        }
        evaluation.setCreateTime(LocalDateTime.now());
        save(evaluation);
        fillApplicationInfo(evaluation);
        return evaluation;
    }

    @Override
    public PageVO<Evaluation> getEvaluationList(PageDTO pageDTO, Integer score) {
        Page<Evaluation> page = new Page<>(pageDTO.getPageNum(), pageDTO.getPageSize());
        LambdaQueryWrapper<Evaluation> wrapper = new LambdaQueryWrapper<>();
        if (score != null) {
            wrapper.eq(Evaluation::getScore, score);
        }
        if (pageDTO.getStartTime() != null) {
            wrapper.ge(Evaluation::getCreateTime, pageDTO.getStartTime());
        }
        if (pageDTO.getEndTime() != null) {
            wrapper.le(Evaluation::getCreateTime, pageDTO.getEndTime());
        }
        wrapper.orderByDesc(Evaluation::getCreateTime);
        page(page, wrapper);
        page.getRecords().forEach(this::fillApplicationInfo);
        return PageVO.of(page.getRecords(), page.getTotal(), (int) page.getCurrent(), (int) page.getSize());
    }

    @Override
    public Map<String, Object> getEvaluationStats() {
        List<Evaluation> evaluations = list();
        long total = evaluations.size();
        Map<String, Object> stats = new HashMap<>();
        int[] distribution = new int[] {0, 0, 0, 0, 0};

        int scoreSum = 0;
        long goodCount = 0;
        for (Evaluation evaluation : evaluations) {
            Integer score = evaluation.getScore();
            if (score != null && score >= 1 && score <= 5) {
                distribution[score - 1]++;
                scoreSum += score;
                if (score >= 4) {
                    goodCount++;
                }
            }
        }

        BigDecimal avgScore = total == 0 ? BigDecimal.ZERO :
                BigDecimal.valueOf(scoreSum).divide(BigDecimal.valueOf(total), 2, RoundingMode.HALF_UP);
        BigDecimal goodRate = total == 0 ? BigDecimal.ZERO :
                BigDecimal.valueOf(goodCount * 100).divide(BigDecimal.valueOf(total), 2, RoundingMode.HALF_UP);

        stats.put("total", total);
        stats.put("avgScore", avgScore);
        stats.put("goodRate", goodRate);
        stats.put("distribution", distribution);
        return stats;
    }

    private void fillApplicationInfo(Evaluation evaluation) {
        if (evaluation.getApplicationId() == null) {
            return;
        }
        ApplicationForm application = applicationFormMapper.selectById(evaluation.getApplicationId());
        if (application != null) {
            evaluation.setApplyNo(application.getApplyNo());
            evaluation.setResidentName(application.getResidentName());
            evaluation.setServiceItemName(application.getServiceItemName());
        }
    }
}
