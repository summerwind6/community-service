package com.community.controller.appointment;

import com.community.common.Result;
import com.community.entity.dto.PageDTO;
import com.community.entity.po.Appointment;
import com.community.entity.vo.PageVO;
import com.community.service.AppointmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "预约管理", description = "居民预约办理时间管理")
@RestController
@RequestMapping("/appointment")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @Operation(summary = "获取预约列表")
    @GetMapping("/list")
    public Result<PageVO<Appointment>> list(PageDTO pageDTO,
                                             @RequestParam(required = false) String status) {
        return Result.success(appointmentService.getAppointmentList(pageDTO, status));
    }

    @Operation(summary = "新增预约")
    @RequiresRoles(value = {"ADMIN", "CLERK"}, logical = Logical.OR)
    @PostMapping
    public Result<Appointment> add(@RequestBody Appointment appointment) {
        return Result.success(appointmentService.addAppointment(appointment));
    }

    @Operation(summary = "更新预约状态")
    @RequiresRoles(value = {"ADMIN", "CLERK"}, logical = Logical.OR)
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestBody java.util.Map<String, String> body) {
        appointmentService.updateStatus(id, body.get("status"));
        return Result.success();
    }
}
