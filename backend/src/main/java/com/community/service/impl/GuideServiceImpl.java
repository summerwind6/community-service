package com.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.community.entity.dto.PageDTO;
import com.community.entity.po.Guide;
import com.community.entity.vo.PageVO;
import com.community.mapper.GuideMapper;
import com.community.service.GuideService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GuideServiceImpl extends ServiceImpl<GuideMapper, Guide> implements GuideService {

    @Override
    public PageVO<Guide> getGuideList(PageDTO pageDTO, String category) {
        Page<Guide> page = new Page<>(pageDTO.getPageNum(), pageDTO.getPageSize());
        LambdaQueryWrapper<Guide> wrapper = new LambdaQueryWrapper<>();
        if (pageDTO.getKeyword() != null && !pageDTO.getKeyword().isEmpty()) {
            wrapper.like(Guide::getTitle, pageDTO.getKeyword());
        }
        if (category != null && !category.isEmpty()) {
            wrapper.eq(Guide::getCategory, category);
        }
        if (pageDTO.getStatus() != null) {
            wrapper.eq(Guide::getStatus, pageDTO.getStatus());
        }
        wrapper.orderByDesc(Guide::getCreateTime);
        page(page, wrapper);
        return PageVO.of(page.getRecords(), page.getTotal(), (int) page.getCurrent(), (int) page.getSize());
    }

    @Override
    public List<Guide> getPublicGuides() {
        LambdaQueryWrapper<Guide> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Guide::getStatus, 1).orderByDesc(Guide::getCreateTime);
        return list(wrapper);
    }

    @Override
    public Guide getGuideById(Long id) {
        return getById(id);
    }

    @Override
    public Guide addGuide(Guide guide) {
        save(guide);
        return guide;
    }

    @Override
    public Guide updateGuide(Guide guide) {
        updateById(guide);
        return guide;
    }

    @Override
    public void removeGuide(Long id) {
        removeById(id);
    }
}
