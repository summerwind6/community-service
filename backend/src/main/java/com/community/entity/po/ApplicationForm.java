package com.community.entity.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("application_form")
public class ApplicationForm implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("apply_no")
    private String applyNo;

    @TableField("resident_id")
    private Long residentId;

    @TableField("resident_name")
    private String residentName;

    @TableField("gender")
    private String gender;

    @TableField("service_item_id")
    private Long serviceItemId;

    @TableField("service_item_name")
    private String serviceItemName;

    @TableField("apply_content")
    private String applyContent;

    @TableField("attachments")
    private String attachments;

    @TableField("status")
    private String status;

    @TableField("fee")
    private BigDecimal fee;

    @TableField("handler_id")
    private Long handlerId;

    @TableField("handler_name")
    private String handlerName;

    @TableField("remark")
    private String remark;

    @TableField(exist = false)
    private Boolean evaluated;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;

    @TableLogic
    @TableField("deleted")
    private Integer deleted;
}
