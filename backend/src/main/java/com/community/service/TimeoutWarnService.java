package com.community.service;

import com.community.entity.vo.PageVO;
import com.community.entity.vo.TimeoutWarnVO;

public interface TimeoutWarnService {

    PageVO<TimeoutWarnVO> getTimeoutWarnList(Integer pageNum, Integer pageSize);

    long getTimeoutWarnCount();

    void urgeApplication(Long id, Long operatorId, String operatorName);
}
