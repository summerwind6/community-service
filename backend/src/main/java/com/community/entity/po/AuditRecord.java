package com.community.entity.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("audit_record")
public class AuditRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("application_id")
    private Long applicationId;

    @TableField("audit_type")
    private String auditType;

    @TableField("auditor_id")
    private Long auditorId;

    @TableField("auditor_name")
    private String auditorName;

    @TableField("audit_result")
    private String auditResult;

    @TableField("audit_opinion")
    private String auditOpinion;

    @TableField("audit_time")
    private LocalDateTime auditTime;

    @TableField("create_time")
    private LocalDateTime createTime;
}
