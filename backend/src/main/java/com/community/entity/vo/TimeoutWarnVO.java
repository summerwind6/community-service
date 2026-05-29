package com.community.entity.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TimeoutWarnVO {

    private Long applicationId;
    private String applyNo;
    private String residentName;
    private Long serviceItemId;
    private String serviceItemName;
    private Integer processingDays;
    private String status;
    private LocalDateTime createTime;
    private Integer timeoutDays;
}
