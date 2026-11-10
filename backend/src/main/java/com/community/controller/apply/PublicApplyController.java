package com.community.controller.apply;

import com.community.common.BusinessException;
import com.community.common.Result;
import com.community.config.ClientAddress;
import com.community.config.PublicLimits;
import com.community.dto.PublicApplyDTO;
import com.community.entity.po.ApplicationForm;
import com.community.entity.po.ResidentInfo;
import com.community.entity.po.ServiceItem;
import com.community.entity.vo.PublicApplyResultVO;
import com.community.entity.vo.AttachmentVO;
import com.community.service.ApplicationFormService;
import com.community.service.PublicRateLimitService;
import com.community.service.ResidentInfoService;
import com.community.service.ServiceItemService;
import com.community.service.TemporaryAttachmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Tag(name = "居民公开申请", description = "居民免登录提交申请与进度查询")
@RestController
@RequestMapping("/apply")
public class PublicApplyController {

    @Autowired
    private ApplicationFormService applicationFormService;

    @Autowired
    private ResidentInfoService residentInfoService;

    @Autowired
    private ServiceItemService serviceItemService;

    @Autowired
    private PublicRateLimitService publicRateLimitService;

    @Autowired
    private TemporaryAttachmentService temporaryAttachmentService;

    @Autowired
    private ClientAddress clientAddress;

    @Autowired
    private PublicLimits publicLimits;

    @Operation(summary = "居民公开提交申请")
    @PostMapping("/public")
    @Transactional
    public Result<PublicApplyResultVO> publicSubmit(@Valid @RequestBody PublicApplyDTO dto,
                                                     HttpServletRequest request) {
        publicRateLimitService.check("public-apply-phone", dto.getPhone(), publicLimits.applyPhoneLimit(), 3600);
        publicRateLimitService.check("public-apply-ip", clientAddress.resolve(request), publicLimits.applyIpLimit(), 3600);
        Long serviceItemId;
        try {
            serviceItemId = Long.valueOf(dto.getServiceItemId());
        } catch (NumberFormatException e) {
            throw new BusinessException(400, "服务事项格式不正确");
        }
        ServiceItem item = serviceItemService.getItemById(serviceItemId);
        if (item == null) {
            throw new BusinessException("服务事项不存在");
        }
        if (item.getStatus() != 1) {
            throw new BusinessException("该服务事项已停用，无法申请");
        }

        ResidentInfo resident = residentInfoService.getResidentByPhone(dto.getPhone());
        if (resident == null) {
            resident = new ResidentInfo();
            resident.setName(dto.getResidentName());
            resident.setPhone(dto.getPhone());
            resident.setIdCard(dto.getIdCard());
            resident.setAddress(dto.getAddress());
            resident.setCommunity(dto.getCommunity());
            resident.setGender(dto.getGender());
            resident.setCreateTime(LocalDateTime.now());
            resident.setUpdateTime(LocalDateTime.now());
            residentInfoService.addResident(resident);
        } else {
            if (resident.getIdCard() == null || resident.getIdCard().length() < 6 || dto.getIdCard().length() < 6
                    || !resident.getIdCard().regionMatches(true, resident.getIdCard().length() - 6,
                    dto.getIdCard(), dto.getIdCard().length() - 6, 6)) {
                throw new BusinessException(403, "已有居民资料校验失败，请核对身份证后六位");
            }
            resident.setName(dto.getResidentName());
            resident.setIdCard(dto.getIdCard());
            resident.setAddress(dto.getAddress());
            resident.setCommunity(dto.getCommunity());
            resident.setGender(dto.getGender());
            resident.setUpdateTime(LocalDateTime.now());
            residentInfoService.updateResident(resident);
        }

        ApplicationForm form = new ApplicationForm();
        form.setResidentId(resident.getId());
        form.setResidentName(resident.getName());
        form.setGender(dto.getGender());
        form.setServiceItemId(item.getId());
        form.setServiceItemName(item.getItemName());
        form.setApplyContent(dto.getApplyContent());
        form.setAttachments(toAttachmentsJson(temporaryAttachmentService.consumeAll(dto.getAttachments())));
        form.setFee(item.getFee());
        ApplicationForm submitted = applicationFormService.submitApplication(form);
        return Result.success(new PublicApplyResultVO(submitted.getApplyNo()));
    }

    private String toAttachmentsJson(List<String> attachments) {
        return attachments.stream().map(path -> "\"" + path + "\"").collect(java.util.stream.Collectors.joining(",", "[", "]"));
    }

}
