package com.community.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.community.entity.po.AuditRecord;

import java.util.List;

public interface AuditRecordService extends IService<AuditRecord> {

    AuditRecord createAuditRecord(AuditRecord record);

    List<AuditRecord> getAuditRecordsByApplicationId(Long applicationId);
}
