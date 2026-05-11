package com.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.community.entity.dto.PageDTO;
import com.community.entity.po.ResidentInfo;
import com.community.entity.vo.PageVO;
import com.community.mapper.ResidentInfoMapper;
import com.community.service.ResidentInfoService;
import org.springframework.stereotype.Service;

@Service
public class ResidentInfoServiceImpl extends ServiceImpl<ResidentInfoMapper, ResidentInfo> implements ResidentInfoService {

    @Override
    public PageVO<ResidentInfo> getResidentList(PageDTO pageDTO) {
        Page<ResidentInfo> page = new Page<>(pageDTO.getPageNum(), pageDTO.getPageSize());
        LambdaQueryWrapper<ResidentInfo> wrapper = new LambdaQueryWrapper<>();
        if (pageDTO.getKeyword() != null && !pageDTO.getKeyword().isEmpty()) {
            wrapper.and(w -> w.like(ResidentInfo::getName, pageDTO.getKeyword())
                    .or().like(ResidentInfo::getPhone, pageDTO.getKeyword()));
        }
        page(page, wrapper);
        return PageVO.of(page.getRecords(), page.getTotal(), (int) page.getCurrent(), (int) page.getSize());
    }

    @Override
    public ResidentInfo getResidentById(Long id) {
        return getById(id);
    }

    @Override
    public ResidentInfo getResidentByPhone(String phone) {
        LambdaQueryWrapper<ResidentInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ResidentInfo::getPhone, phone);
        return getOne(wrapper);
    }

    @Override
    public ResidentInfo addResident(ResidentInfo resident) {
        save(resident);
        return resident;
    }

    @Override
    public ResidentInfo updateResident(ResidentInfo resident) {
        updateById(resident);
        return resident;
    }
}
