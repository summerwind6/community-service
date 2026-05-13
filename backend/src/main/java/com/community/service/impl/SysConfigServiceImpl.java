package com.community.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.community.entity.po.SysConfig;
import com.community.mapper.SysConfigMapper;
import com.community.service.SysConfigService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SysConfigServiceImpl extends ServiceImpl<SysConfigMapper, SysConfig> implements SysConfigService {

    @Override
    public List<SysConfig> getAllConfigs() {
        return list();
    }

    @Override
    public void updateConfigs(List<SysConfig> configs) {
        for (SysConfig config : configs) {
            updateById(config);
        }
    }
}
