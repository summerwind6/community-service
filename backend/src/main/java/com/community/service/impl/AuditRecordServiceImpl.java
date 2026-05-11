package com.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.community.entity.po.AuditRecord;
import com.community.mapper.AuditRecordMapper;
import com.community.service.AuditRecordService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuditRecordServiceImpl extends ServiceImpl<AuditRecordMapper, AuditRecord> implements AuditRecordService {

    @Override
    public AuditRecord createAuditRecord(AuditRecord record) {
        save(record);
        return record;
    }

    @Override
    public List<AuditRecord> getAuditRecordsByApplicationId(Long applicationId) {
        LambdaQueryWrapper<AuditRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AuditRecord::getApplicationId, applicationId)
                .orderByDesc(AuditRecord::getAuditTime);
        return list(wrapper);
    }
}
