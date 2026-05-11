package com.community.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.community.entity.po.ApplicationForm;
import com.community.mapper.ApplicationFormMapper;
import com.community.service.ApplicationFormService;
import org.springframework.stereotype.Service;

@Service
public class ApplicationFormServiceImpl extends ServiceImpl<ApplicationFormMapper, ApplicationForm> implements ApplicationFormService {

    @Override
    public ApplicationForm submitApplication(ApplicationForm form) {
        save(form);
        return form;
    }

    @Override
    public ApplicationForm getApplicationDetail(Long id) {
        return getById(id);
    }

    @Override
    public com.community.entity.vo.PageVO<ApplicationForm> getApplicationList(com.community.entity.dto.PageDTO pageDTO) {
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<ApplicationForm> page =
                new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(pageDTO.getPageNum(), pageDTO.getPageSize());
        page(page);
        return com.community.entity.vo.PageVO.of(page.getRecords(), page.getTotal(), (int) page.getCurrent(), (int) page.getSize());
    }

    @Override
    public void auditApplication(Long id, String auditResult, String auditOpinion) {
        ApplicationForm form = getById(id);
        if (form != null) {
            form.setStatus(auditResult);
            form.setRemark(auditOpinion);
            updateById(form);
        }
    }

    @Override
    public void updateStatus(Long id, String status) {
        ApplicationForm form = getById(id);
        if (form != null) {
            form.setStatus(status);
            updateById(form);
        }
    }
}
