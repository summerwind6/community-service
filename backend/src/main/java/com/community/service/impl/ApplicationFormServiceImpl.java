package com.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.community.audit.AuditNotifications;
import com.community.audit.AuditOutbox;
import com.community.common.BusinessException;
import com.community.entity.dto.PageDTO;
import com.community.entity.po.ApplicationForm;
import com.community.entity.po.AuditRecord;

import com.community.entity.po.ResidentInfo;
import com.community.entity.po.ServiceItem;
import com.community.entity.enums.ApplicationStatus;
import com.community.entity.vo.PageVO;
import com.community.entity.vo.PublicProgressVO;
import com.community.mapper.ApplicationFormMapper;
import com.community.service.ApplicationFormService;
import com.community.service.AuditRecordService;

import com.community.service.ResidentInfoService;
import com.community.service.ServiceItemService;
import com.community.service.EvaluationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

@Service
public class ApplicationFormServiceImpl extends ServiceImpl<ApplicationFormMapper, ApplicationForm> implements ApplicationFormService {

    private static final Logger log = LoggerFactory.getLogger(ApplicationFormServiceImpl.class);

    @Autowired
    private AuditRecordService auditRecordService;

    @Autowired
    private ResidentInfoService residentInfoService;

    @Autowired
    private ServiceItemService serviceItemService;

    @Autowired
    private AuditOutbox auditOutbox;

    @Autowired
    private AuditNotifications auditNotifications;

    @Value("${app.audit.outbox-enabled:true}")
    private boolean auditOutboxEnabled;

    @Autowired
    private EvaluationService evaluationService;

    @Override
    @Transactional
    public ApplicationForm submitApplication(ApplicationForm form) {
        if (form.getResidentId() != null) {
            ResidentInfo resident = residentInfoService.getResidentById(form.getResidentId());
            if (resident == null) {
                throw new BusinessException("居民不存在");
            }
            form.setResidentName(resident.getName());
        }
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
        form.setStatus(ApplicationStatus.PENDING.name());
        form.setVersion(0);
        form.setCreateTime(LocalDateTime.now());
        save(form);
        form.setApplyNo(generateApplyNo(form.getId()));
        updateById(form);
        return form;
    }

    @Override
    public ApplicationForm getApplicationDetail(Long id) {
        return getById(id);
    }

    @Override
    public PageVO<ApplicationForm> getApplicationList(PageDTO pageDTO, String status) {
        Page<ApplicationForm> page = new Page<>(pageDTO.getPageNum(), pageDTO.getPageSize());
        LambdaQueryWrapper<ApplicationForm> wrapper = new LambdaQueryWrapper<>();
        if (pageDTO.getKeyword() != null && !pageDTO.getKeyword().isEmpty()) {
            wrapper.and(w -> w.like(ApplicationForm::getApplyNo, pageDTO.getKeyword())
                .or().like(ApplicationForm::getResidentName, pageDTO.getKeyword())
                .or().like(ApplicationForm::getServiceItemName, pageDTO.getKeyword()));
        }
        if (status != null && !status.isEmpty()) {
            wrapper.eq(ApplicationForm::getStatus, status);
        }
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
        if (ApplicationStatus.from(form.getStatus()) != ApplicationStatus.PENDING) {
            throw new BusinessException("当前状态不允许初审，仅待受理状态可操作");
        }
        ApplicationStatus target = ApplicationStatus.auditResult(result,
                Set.of(ApplicationStatus.PROCESSING, ApplicationStatus.REJECTED));
        transition(form, ApplicationStatus.PENDING, target, opinion, auditorId, auditorName);
        AuditRecord record = new AuditRecord();
        record.setApplicationId(id);
        record.setAuditType("COMMUNITY");
        record.setAuditorId(auditorId);
        record.setAuditorName(auditorName);
        record.setAuditResult(target.name());
        record.setAuditOpinion(opinion);
        record.setAuditTime(LocalDateTime.now());
        record.setCreateTime(LocalDateTime.now());
        auditRecordService.createAuditRecord(record);
        recordAuditNotification(form, record);
    }

    @Override
    @Transactional
    public void approveApplication(Long id, String result, String opinion, Long auditorId, String auditorName) {
        ApplicationForm form = getById(id);
        if (form == null) {
            throw new BusinessException("申请单不存在");
        }
        if (ApplicationStatus.from(form.getStatus()) != ApplicationStatus.PROCESSING) {
            throw new BusinessException("当前状态不允许复审，仅处理中状态可操作");
        }
        ApplicationStatus target = ApplicationStatus.auditResult(result,
                Set.of(ApplicationStatus.COMPLETED, ApplicationStatus.REJECTED));
        transition(form, ApplicationStatus.PROCESSING, target, opinion, auditorId, auditorName);
        AuditRecord record = new AuditRecord();
        record.setApplicationId(id);
        record.setAuditType("STREET");
        record.setAuditorId(auditorId);
        record.setAuditorName(auditorName);
        record.setAuditResult(target.name());
        record.setAuditOpinion(opinion);
        record.setAuditTime(LocalDateTime.now());
        record.setCreateTime(LocalDateTime.now());
        auditRecordService.createAuditRecord(record);
        recordAuditNotification(form, record);
    }

    @Override
    public List<AuditRecord> getAuditRecords(Long applicationId) {
        return auditRecordService.getAuditRecordsByApplicationId(applicationId);
    }

    @Override
    public List<ApplicationForm> getApplicationProgressByPhone(String phone) {
        ResidentInfo resident = residentInfoService.getResidentByPhone(phone);
        if (resident == null) {
            return List.of();
        }
        LambdaQueryWrapper<ApplicationForm> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ApplicationForm::getResidentId, resident.getId())
            .orderByDesc(ApplicationForm::getCreateTime);
        List<ApplicationForm> forms = list(wrapper);
        forms.forEach(form -> form.setEvaluated(isApplicationEvaluated(form.getId())));
        return forms;
    }

    @Override
    public PublicProgressVO getPublicProgress(String applyNo, String phoneLast4) {
        ApplicationForm form = getOne(new LambdaQueryWrapper<ApplicationForm>()
                .eq(ApplicationForm::getApplyNo, applyNo), false);
        if (form == null || form.getResidentId() == null) {
            throw new BusinessException(404, "申请不存在或验证信息错误");
        }
        ResidentInfo resident = residentInfoService.getResidentById(form.getResidentId());
        if (resident == null || resident.getPhone() == null || !resident.getPhone().endsWith(phoneLast4)) {
            throw new BusinessException(404, "申请不存在或验证信息错误");
        }
        PublicProgressVO vo = new PublicProgressVO();
        vo.setApplyNo(form.getApplyNo());
        vo.setServiceItemName(form.getServiceItemName());
        vo.setStatus(form.getStatus());
        vo.setUpdateTime(form.getUpdateTime());
        vo.setEvaluated(isApplicationEvaluated(form.getId()));
        return vo;
    }

    @Override
    public Boolean isApplicationEvaluated(Long applicationId) {
        if (applicationId == null) {
            return false;
        }
        LambdaQueryWrapper<com.community.entity.po.Evaluation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(com.community.entity.po.Evaluation::getApplicationId, applicationId);
        return evaluationService.count(wrapper) > 0;
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

    private void recordAuditNotification(ApplicationForm form, AuditRecord record) {
        if (auditOutboxEnabled) {
            auditOutbox.append(form, record);
            return;
        }
        try {
            auditNotifications.sendSynchronously(form, record);
        } catch (Exception e) {
            log.warn("审核通知发送失败, applyId={}: {}", form.getId(), e.getClass().getSimpleName());
        }
    }

    private void transition(ApplicationForm form, ApplicationStatus expected, ApplicationStatus target,
                            String opinion, Long auditorId, String auditorName) {
        int version = form.getVersion() == null ? 0 : form.getVersion();
        LocalDateTime now = LocalDateTime.now();
        boolean updated = update(new LambdaUpdateWrapper<ApplicationForm>()
                .eq(ApplicationForm::getId, form.getId())
                .eq(ApplicationForm::getStatus, expected.name())
                .eq(ApplicationForm::getVersion, version)
                .set(ApplicationForm::getStatus, target.name())
                .set(ApplicationForm::getRemark, opinion)
                .set(ApplicationForm::getHandlerId, auditorId)
                .set(ApplicationForm::getHandlerName, auditorName)
                .set(ApplicationForm::getUpdateTime, now)
                .set(ApplicationForm::getVersion, version + 1));
        if (!updated) {
            throw new BusinessException(409, "申请状态已变化，请刷新后重试");
        }
        form.setStatus(target.name());
        form.setRemark(opinion);
        form.setHandlerId(auditorId);
        form.setHandlerName(auditorName);
        form.setUpdateTime(now);
        form.setVersion(version + 1);
    }

    private String generateApplyNo(Long id) {
        String datePrefix = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        return String.format("AP%s%08d", datePrefix, id);
    }
}
