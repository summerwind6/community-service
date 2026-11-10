package com.community.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.community.entity.dto.PageDTO;
import com.community.entity.po.ServiceItem;
import com.community.entity.vo.PageVO;

import java.util.List;

public interface ServiceItemService extends IService<ServiceItem> {

    PageVO<ServiceItem> getItemList(PageDTO pageDTO);

    ServiceItem getItemById(Long id);

    ServiceItem addItem(ServiceItem item);

    ServiceItem updateItem(ServiceItem item);

    void updateItemStatus(Long id, Integer status);

    void removeItem(Long id);

    List<ServiceItem> getEnabledItems();
}
