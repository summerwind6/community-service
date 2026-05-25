package com.community.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.community.entity.dto.PageDTO;
import com.community.entity.po.Notification;
import com.community.entity.vo.PageVO;

public interface NotificationService extends IService<Notification> {

    PageVO<Notification> getNotificationList(PageDTO pageDTO, String type);

    Notification sendNotification(Notification notification);

    long getUnreadCount();

    void markAsRead(Long id);
}
