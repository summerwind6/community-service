package com.community.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.community.entity.po.SysConfig;

import java.util.List;
import java.util.Map;

public interface SysConfigService extends IService<SysConfig> {

    List<SysConfig> getAllConfigs();

    void updateConfigs(List<SysConfig> configs);
}
