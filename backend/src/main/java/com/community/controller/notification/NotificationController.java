package com.community.controller.notification;

import com.community.common.Result;
import com.community.entity.dto.PageDTO;
import com.community.entity.po.Notification;
import com.community.entity.vo.PageVO;
import com.community.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "消息通知", description = "站内信通知管理")
@RestController
@RequestMapping("/notification")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @Operation(summary = "获取通知列表")
    @GetMapping("/list")
    public Result<PageVO<Notification>> list(PageDTO pageDTO,
                                              @RequestParam(required = false) String type,
                                              @RequestParam(required = false) Integer isRead) {
        return Result.success(notificationService.getNotificationList(pageDTO, type, isRead));
    }

    @Operation(summary = "发送通知")
    @RequiresPermissions("notification:send")
    @PostMapping
    public Result<Notification> send(@RequestBody Notification notification) {
        return Result.success(notificationService.sendNotification(notification));
    }

    @Operation(summary = "获取未读数量")
    @GetMapping("/unread-count")
    public Result<Map<String, Object>> getUnreadCount() {
        long count = notificationService.getUnreadCount();
        return Result.success(Map.of("count", count));
    }

    @Operation(summary = "标记已读")
    @PutMapping("/{id}/read")
    public Result<Void> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return Result.success();
    }
}
