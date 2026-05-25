package com.community.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.community.entity.dto.PageDTO;
import com.community.entity.po.Evaluation;
import com.community.entity.vo.PageVO;

import java.util.Map;

public interface EvaluationService extends IService<Evaluation> {

    Evaluation submitEvaluation(Evaluation evaluation);

    PageVO<Evaluation> getEvaluationList(PageDTO pageDTO, Integer score);

    Map<String, Object> getEvaluationStats();
}
