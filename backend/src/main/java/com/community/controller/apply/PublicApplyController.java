package com.community.controller.apply;

import com.community.common.BusinessException;
import com.community.common.Result;
import com.community.dto.PublicApplyDTO;
import com.community.entity.po.ApplicationForm;
import com.community.entity.po.ResidentInfo;
import com.community.entity.po.ServiceItem;
import com.community.service.ApplicationFormService;
import com.community.service.ResidentInfoService;
import com.community.service.ServiceItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

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

    @Operation(summary = "居民公开提交申请")
    @PostMapping("/public")
    public Result<ApplicationForm> publicSubmit(@RequestBody PublicApplyDTO dto) {
        if (dto.getPhone() == null || dto.getPhone().isBlank()) {
            throw new BusinessException("手机号不能为空");
        }
        if (dto.getResidentName() == null || dto.getResidentName().isBlank()) {
            throw new BusinessException("居民姓名不能为空");
        }
        if (dto.getServiceItemId() == null || dto.getServiceItemId().isBlank()) {
            throw new BusinessException("服务事项不能为空");
        }

        Long serviceItemId = Long.valueOf(dto.getServiceItemId());
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
        form.setServiceItemId(item.getId());
        form.setServiceItemName(item.getItemName());
        form.setApplyContent(dto.getApplyContent());
        form.setAttachments(dto.getAttachments());
        form.setFee(item.getFee());
        return Result.success(applicationFormService.submitApplication(form));
    }
}
