package com.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.community.entity.dto.PageDTO;
import com.community.entity.po.ServiceItem;
import com.community.entity.vo.PageVO;
import com.community.mapper.ServiceItemMapper;
import com.community.service.ServiceItemService;
import org.springframework.stereotype.Service;

import com.community.cache.PublicListCache;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ServiceItemServiceImpl extends ServiceImpl<ServiceItemMapper, ServiceItem> implements ServiceItemService {

    private static final String CACHE_KEY = "cache:item:enabled:v1";
    private final PublicListCache cache;

    public ServiceItemServiceImpl(PublicListCache cache) {
        this.cache = cache;
    }

    @Override
    public PageVO<ServiceItem> getItemList(PageDTO pageDTO) {
        Page<ServiceItem> page = new Page<>(pageDTO.getPageNum(), pageDTO.getPageSize());
        LambdaQueryWrapper<ServiceItem> wrapper = new LambdaQueryWrapper<>();
        if (pageDTO.getKeyword() != null && !pageDTO.getKeyword().isEmpty()) {
            wrapper.and(w -> w.like(ServiceItem::getItemName, pageDTO.getKeyword())
                    .or().like(ServiceItem::getCategory, pageDTO.getKeyword()));
        }
        if (pageDTO.getStatus() != null) {
            wrapper.eq(ServiceItem::getStatus, pageDTO.getStatus());
        }
        page(page, wrapper);
        return PageVO.of(page.getRecords(), page.getTotal(), (int) page.getCurrent(), (int) page.getSize());
    }

    @Override
    public ServiceItem getItemById(Long id) {
        return getById(id);
    }

    @Override
    @Transactional
    public ServiceItem addItem(ServiceItem item) {
        save(item);
        cache.invalidateAfterCommit(CACHE_KEY);
        return item;
    }

    @Override
    @Transactional
    public ServiceItem updateItem(ServiceItem item) {
        updateById(item);
        cache.invalidateAfterCommit(CACHE_KEY);
        return item;
    }

    @Override
    @Transactional
    public void updateItemStatus(Long id, Integer status) {
        ServiceItem item = getById(id);
        if (item != null) {
            item.setStatus(status);
            updateById(item);
            cache.invalidateAfterCommit(CACHE_KEY);
        }
    }

    @Override
    @Transactional
    public void removeItem(Long id) {
        removeById(id);
        cache.invalidateAfterCommit(CACHE_KEY);
    }

    @Override
    public List<ServiceItem> getEnabledItems() {
        return cache.read(CACHE_KEY, ServiceItem.class, () -> {
            LambdaQueryWrapper<ServiceItem> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ServiceItem::getStatus, 1);
            return list(wrapper);
        });
    }
}
