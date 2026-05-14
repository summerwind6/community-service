package com.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.community.common.BusinessException;
import com.community.entity.dto.PageDTO;
import com.community.entity.po.ApplicationForm;
import com.community.entity.po.AuditRecord;
import com.community.entity.po.ResidentInfo;
import com.community.entity.po.ServiceItem;
import com.community.entity.vo.PageVO;
import com.community.mapper.ApplicationFormMapper;
import com.community.service.ApplicationFormService;
import com.community.service.AuditRecordService;
import com.community.service.ResidentInfoService;
import com.community.service.ServiceItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ApplicationFormServiceImpl extends ServiceImpl<ApplicationFormMapper, ApplicationForm> implements ApplicationFormService {

    @Autowired
    private AuditRecordService auditRecordService;

    @Autowired
    private ResidentInfoService residentInfoService;

    @Autowired
    private ServiceItemService serviceItemService;

    @Override
    @Transactional
    public ApplicationForm submitApplication(ApplicationForm form) {
        // 校验居民是否存在
        if (form.getResidentId() != null) {
            ResidentInfo resident = residentInfoService.getResidentById(form.getResidentId());
            if (resident == null) {
                throw new BusinessException("居民不存在");
            }
            form.setResidentName(resident.getName());
        }
        // 校验服务事项是否存在且启用
        if (form.getServiceItemId() != null) {
            ServiceItem item = serviceItemService.getItemById(form.getServiceItemId());
            if (item == null) {
                throw new BusinessException("服务事项不存在");
            }
            if (item.getStatus() != 1) {
                throw new BusinessException("该服务事项已停用，无法申请");
            }
            form.setServiceItemName(item.getItemName());
            form.setFee(item.getFee());
        }
        // 生成申请编号: AP + yyyyMMdd + 4位序号
        form.setApplyNo(generateApplyNo());
        form.setStatus("PENDING");
        form.setCreateTime(LocalDateTime.now());
        save(form);
        return form;
    }

    @Override
    public ApplicationForm getApplicationDetail(Long id) {
        return getById(id);
    }

    @Override
    public PageVO<ApplicationForm> getApplicationList(PageDTO pageDTO) {
        Page<ApplicationForm> page = new Page<>(pageDTO.getPageNum(), pageDTO.getPageSize());
        LambdaQueryWrapper<ApplicationForm> wrapper = new LambdaQueryWrapper<>();
        // 关键词搜索：申请编号、申请人姓名、服务项目名称
        if (pageDTO.getKeyword() != null && !pageDTO.getKeyword().isEmpty()) {
            wrapper.and(w -> w.like(ApplicationForm::getApplyNo, pageDTO.getKeyword())
                    .or().like(ApplicationForm::getResidentName, pageDTO.getKeyword())
                    .or().like(ApplicationForm::getServiceItemName, pageDTO.getKeyword()));
        }
        // 状态筛选
        if (pageDTO.getStatus() != null) {
            // PageDTO.status is Integer, but ApplicationForm.status is String
            // We'll use a custom approach: if status is passed as String via another field
        }
        // 时间范围
        if (pageDTO.getStartTime() != null) {
            wrapper.ge(ApplicationForm::getCreateTime, pageDTO.getStartTime());
        }
        if (pageDTO.getEndTime() != null) {
            wrapper.le(ApplicationForm::getCreateTime, pageDTO.getEndTime());
        }
        wrapper.orderByDesc(ApplicationForm::getCreateTime);
        page(page, wrapper);
        return PageVO.of(page.getRecords(), page.getTotal(), (int) page.getCurrent(), (int) page.getSize());
    }

    @Override
    @Transactional
    public void reviewApplication(Long id, String result, String opinion, Long auditorId, String auditorName) {
        ApplicationForm form = getById(id);
        if (form == null) {
            throw new BusinessException("申请单不存在");
        }
        if (!"PENDING".equals(form.getStatus())) {
            throw new BusinessException("当前状态不允许初审，仅待受理状态可操作");
        }
        // 更新申请单状态
        if ("REJECTED".equals(result)) {
            form.setStatus("REJECTED");
        } else {
            form.setStatus("PROCESSING");
        }
        form.setRemark(opinion);
        form.setHandlerId(auditorId);
        form.setHandlerName(auditorName);
        form.setUpdateTime(LocalDateTime.now());
        updateById(form);
        // 创建审核记录
        AuditRecord record = new AuditRecord();
        record.setApplicationId(id);
        record.setAuditType("COMMUNITY");
        record.setAuditorId(auditorId);
        record.setAuditorName(auditorName);
        record.setAuditResult(result);
        record.setAuditOpinion(opinion);
        record.setAuditTime(LocalDateTime.now());
        record.setCreateTime(LocalDateTime.now());
        auditRecordService.createAuditRecord(record);
    }

    @Override
    @Transactional
    public void approveApplication(Long id, String result, String opinion, Long auditorId, String auditorName) {
        ApplicationForm form = getById(id);
        if (form == null) {
            throw new BusinessException("申请单不存在");
        }
        if (!"PROCESSING".equals(form.getStatus())) {
            throw new BusinessException("当前状态不允许复审，仅处理中状态可操作");
        }
        // 更新申请单状态
        if ("REJECTED".equals(result)) {
            form.setStatus("REJECTED");
        } else {
            form.setStatus("COMPLETED");
        }
        form.setRemark(opinion);
        form.setHandlerId(auditorId);
        form.setHandlerName(auditorName);
        form.setUpdateTime(LocalDateTime.now());
        updateById(form);
        // 创建审核记录
        AuditRecord record = new AuditRecord();
        record.setApplicationId(id);
        record.setAuditType("STREET");
        record.setAuditorId(auditorId);
        record.setAuditorName(auditorName);
        record.setAuditResult(result);
        record.setAuditOpinion(opinion);
        record.setAuditTime(LocalDateTime.now());
        record.setCreateTime(LocalDateTime.now());
        auditRecordService.createAuditRecord(record);
    }

    @Override
    public List<AuditRecord> getAuditRecords(Long applicationId) {
        return auditRecordService.getAuditRecordsByApplicationId(applicationId);
    }

    @Override
    public void updateStatus(Long id, String status) {
        ApplicationForm form = getById(id);
        if (form != null) {
            form.setStatus(status);
            form.setUpdateTime(LocalDateTime.now());
            updateById(form);
        }
    }

    /** 生成申请编号: AP + yyyyMMdd + 4位当日序号 */
    private String generateApplyNo() {
        String datePrefix = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        LambdaQueryWrapper<ApplicationForm> wrapper = new LambdaQueryWrapper<>();
        wrapper.likeRight(ApplicationForm::getApplyNo, "AP" + datePrefix)
                .orderByDesc(ApplicationForm::getApplyNo)
                .last("LIMIT 1");
        ApplicationForm last = getOne(wrapper, false);
        int seq = 1;
        if (last != null && last.getApplyNo().length() >= 16) {
            try {
                seq = Integer.parseInt(last.getApplyNo().substring(12)) + 1;
            } catch (NumberFormatException ignored) {
            }
        }
        return String.format("AP%s%04d", datePrefix, seq);
    }
}
