package com.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.community.entity.dto.PageDTO;
import com.community.entity.po.Appointment;
import com.community.entity.vo.PageVO;
import com.community.mapper.AppointmentMapper;
import com.community.service.AppointmentService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AppointmentServiceImpl extends ServiceImpl<AppointmentMapper, Appointment> implements AppointmentService {

    @Override
    public PageVO<Appointment> getAppointmentList(PageDTO pageDTO, String status) {
        Page<Appointment> page = new Page<>(pageDTO.getPageNum(), pageDTO.getPageSize());
        LambdaQueryWrapper<Appointment> wrapper = new LambdaQueryWrapper<>();
        if (pageDTO.getKeyword() != null && !pageDTO.getKeyword().isEmpty()) {
            wrapper.and(w -> w.like(Appointment::getResidentName, pageDTO.getKeyword())
                    .or().like(Appointment::getResidentPhone, pageDTO.getKeyword()));
        }
        if (status != null && !status.isEmpty()) {
            wrapper.eq(Appointment::getStatus, status);
        }
        wrapper.orderByDesc(Appointment::getAppointmentDate).orderByDesc(Appointment::getCreateTime);
        page(page, wrapper);
        return PageVO.of(page.getRecords(), page.getTotal(), (int) page.getCurrent(), (int) page.getSize());
    }

    @Override
    public Appointment addAppointment(Appointment appointment) {
        appointment.setStatus("PENDING");
        appointment.setCreateTime(LocalDateTime.now());
        save(appointment);
        return appointment;
    }

    @Override
    public Appointment updateStatus(Long id, String status) {
        Appointment appointment = getById(id);
        if (appointment != null) {
            appointment.setStatus(status);
            updateById(appointment);
        }
        return appointment;
    }
}
