package com.community.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.community.entity.dto.PageDTO;
import com.community.entity.po.Appointment;
import com.community.entity.vo.PageVO;

import java.time.LocalDate;

public interface AppointmentService extends IService<Appointment> {

    PageVO<Appointment> getAppointmentList(PageDTO pageDTO, String status, LocalDate startDate, LocalDate endDate);

    Appointment addAppointment(Appointment appointment);

    Appointment updateStatus(Long id, String status);
}
