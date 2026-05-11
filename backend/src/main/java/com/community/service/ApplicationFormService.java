package com.community.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.community.entity.dto.PageDTO;
import com.community.entity.po.ApplicationForm;
import com.community.entity.vo.PageVO;

public interface ApplicationFormService extends IService<ApplicationForm> {

    ApplicationForm submitApplication(ApplicationForm form);

    ApplicationForm getApplicationDetail(Long id);

    PageVO<ApplicationForm> getApplicationList(PageDTO pageDTO);

    void auditApplication(Long id, String auditResult, String auditOpinion);

    void updateStatus(Long id, String status);
}
