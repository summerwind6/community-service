package com.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.community.common.BusinessException;
import com.community.entity.po.ApplicationForm;
import com.community.entity.po.Notification;
import com.community.entity.po.ServiceItem;
import com.community.entity.vo.PageVO;
import com.community.entity.vo.TimeoutWarnVO;
import com.community.mapper.ApplicationFormMapper;
import com.community.service.NotificationService;
import com.community.service.ServiceItemService;
import com.community.service.TimeoutWarnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class TimeoutWarnServiceImpl extends ServiceImpl<ApplicationFormMapper, ApplicationForm> implements TimeoutWarnService {

    @Autowired
    private ServiceItemService serviceItemService;

    @Autowired
    private NotificationService notificationService;

    @Override
    public PageVO<TimeoutWarnVO> getTimeoutWarnList(Integer pageNum, Integer pageSize) {
        List<TimeoutWarnVO> all = buildTimeoutList();
        all.sort(Comparator.comparing(TimeoutWarnVO::getTimeoutDays).reversed().thenComparing(TimeoutWarnVO::getCreateTime));
        int current = pageNum == null || pageNum < 1 ? 1 : pageNum;
        int size = pageSize == null || pageSize < 1 ? 10 : pageSize;
        int fromIndex = Math.min((current - 1) * size, all.size());
        int toIndex = Math.min(fromIndex + size, all.size());
        List<TimeoutWarnVO> records = fromIndex >= toIndex ? new ArrayList<>() : all.subList(fromIndex, toIndex);
        return PageVO.of(records, all.size(), current, size);
    }

    @Override
    public long getTimeoutWarnCount() {
        return buildTimeoutList().size();
    }

    @Override
    @Transactional
    public void urgeApplication(Long id, Long operatorId, String operatorName) {
        ApplicationForm form = getById(id);
        if (form == null) {
            throw new BusinessException("申请单不存在");
        }
        TimeoutWarnVO warnVO = buildTimeoutVo(form);
        if (warnVO == null || warnVO.getTimeoutDays() == null || warnVO.getTimeoutDays() <= 0) {
            throw new BusinessException("该申请未超时，无需催办");
        }
        form.setRemark((form.getRemark() == null ? "" : form.getRemark() + "\n")
            + "[催办] " + LocalDateTime.now() + " 由 " + operatorName + " 催办，超时 " + warnVO.getTimeoutDays() + " 天");
        form.setUpdateTime(LocalDateTime.now());
        updateById(form);

        Notification notification = new Notification();
        notification.setTitle("申请" + form.getApplyNo() + "已超时催办");
        notification.setContent("申请单【" + form.getApplyNo() + "】已超时 " + warnVO.getTimeoutDays() + " 天，请尽快办理。");
        notification.setType("URGENT");
        notification.setTargetId(form.getId());
        notification.setTargetName(form.getResidentName());
        notification.setIsRead(0);
        notification.setCreateTime(LocalDateTime.now());
        notificationService.sendNotification(notification);
    }

    private List<TimeoutWarnVO> buildTimeoutList() {
        LambdaQueryWrapper<ApplicationForm> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(ApplicationForm::getCreateTime);
        List<ApplicationForm> forms = list(wrapper);
        List<TimeoutWarnVO> result = new ArrayList<>();
        for (ApplicationForm form : forms) {
            TimeoutWarnVO vo = buildTimeoutVo(form);
            if (vo != null && vo.getTimeoutDays() != null && vo.getTimeoutDays() > 0) {
                result.add(vo);
            }
        }
        return result;
    }

    private TimeoutWarnVO buildTimeoutVo(ApplicationForm form) {
        if (form == null || form.getCreateTime() == null || form.getServiceItemId() == null) {
            return null;
        }
        if ("COMPLETED".equals(form.getStatus()) || "REJECTED".equals(form.getStatus())) {
            return null;
        }
        ServiceItem item = serviceItemService.getItemById(form.getServiceItemId());
        if (item == null || item.getProcessingDays() == null) {
            return null;
        }
        long usedDays = ChronoUnit.DAYS.between(form.getCreateTime().toLocalDate(), LocalDate.now());
        int timeoutDays = (int) (usedDays - item.getProcessingDays());
        if (timeoutDays <= 0) {
            return null;
        }
        TimeoutWarnVO vo = new TimeoutWarnVO();
        vo.setApplicationId(form.getId());
        vo.setApplyNo(form.getApplyNo());
        vo.setResidentName(form.getResidentName());
        vo.setServiceItemId(form.getServiceItemId());
        vo.setServiceItemName(form.getServiceItemName());
        vo.setProcessingDays(item.getProcessingDays());
        vo.setStatus(form.getStatus());
        vo.setCreateTime(form.getCreateTime());
        vo.setTimeoutDays(timeoutDays);
        return vo;
    }
}
