package com.community.entity.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("service_item")
public class ServiceItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("item_name")
    private String itemName;

    @TableField("item_code")
    private String itemCode;

    @TableField("category")
    private String category;

    @TableField("description")
    private String description;

    @TableField("fee")
    private BigDecimal fee;

    @TableField("processing_days")
    private Integer processingDays;

    @TableField("required_docs")
    private String requiredDocs;

    @TableField("status")
    private Integer status;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;

    @TableLogic
    @TableField("deleted")
    private Integer deleted;
}
