package com.community.controller.appointment;

import com.community.common.Result;
import com.community.entity.dto.PageDTO;
import com.community.entity.po.Appointment;
import com.community.entity.vo.PageVO;
import com.community.service.AppointmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Tag(name = "预约管理", description = "居民预约办理时间管理")
@RestController
@RequestMapping("/appointment")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @Operation(summary = "获取预约列表")
    @GetMapping("/list")
    public Result<PageVO<Appointment>> list(PageDTO pageDTO,
                                             @RequestParam(required = false) String status,
                                             @RequestParam(required = false) LocalDate startDate,
                                             @RequestParam(required = false) LocalDate endDate) {
        return Result.success(appointmentService.getAppointmentList(pageDTO, status, startDate, endDate));
    }

    @Operation(summary = "新增预约（管理员代录入或公开接口）")
    @PostMapping
    public Result<Appointment> add(@RequestBody Appointment appointment) {
        return Result.success(appointmentService.addAppointment(appointment));
    }

    @Operation(summary = "确认预约")
    @RequiresPermissions("appointment:manage")
    @PutMapping("/{id}/confirm")
    public Result<Void> confirm(@PathVariable Long id) {
        appointmentService.updateStatus(id, "CONFIRMED");
        return Result.success();
    }

    @Operation(summary = "取消预约")
    @RequiresPermissions("appointment:manage")
    @PutMapping("/{id}/cancel")
    public Result<Void> cancel(@PathVariable Long id) {
        appointmentService.updateStatus(id, "CANCELED");
        return Result.success();
    }

    @Operation(summary = "标记已完成")
    @RequiresPermissions("appointment:manage")
    @PutMapping("/{id}/complete")
    public Result<Void> complete(@PathVariable Long id) {
        appointmentService.updateStatus(id, "COMPLETED");
        return Result.success();
    }
}
