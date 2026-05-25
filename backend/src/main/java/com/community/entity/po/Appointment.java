package com.community.entity.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("appointment")
public class Appointment implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("resident_name")
    private String residentName;

    @TableField("resident_phone")
    private String residentPhone;

    @TableField("service_item_id")
    private Long serviceItemId;

    @TableField("service_item_name")
    private String serviceItemName;

    @TableField("appointment_date")
    private LocalDate appointmentDate;

    @TableField("time_slot")
    private String timeSlot;

    @TableField("status")
    private String status;

    @TableField("remark")
    private String remark;

    @TableField("create_time")
    private LocalDateTime createTime;
}
