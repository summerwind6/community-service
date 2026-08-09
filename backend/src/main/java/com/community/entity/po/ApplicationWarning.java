package com.community.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("application_warning")
public class ApplicationWarning {
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("application_id")
    private Long applicationId;

    @TableField("warn_type")
    private String warnType;

    @TableField("timeout_days")
    private Integer timeoutDays;

    @TableField("warn_time")
    private LocalDateTime warnTime;
}
