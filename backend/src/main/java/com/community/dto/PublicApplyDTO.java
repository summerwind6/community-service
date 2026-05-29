package com.community.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PublicApplyDTO {

    private String residentName;
    private String phone;
    private String idCard;
    private String address;
    private String community;
    private String gender;
    private String serviceItemId;
    private String applyContent;
    private String attachments;
    private BigDecimal fee;
}
