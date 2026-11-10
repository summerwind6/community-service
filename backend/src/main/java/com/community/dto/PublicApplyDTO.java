package com.community.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;

@Data
public class PublicApplyDTO {

    @NotBlank(message = "居民姓名不能为空")
    private String residentName;
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;
    @NotBlank(message = "身份证不能为空")
    @Pattern(regexp = "^(\\d{15}|\\d{17}[0-9Xx])$", message = "身份证格式不正确")
    private String idCard;
    private String address;
    private String community;
    private String gender;
    @NotBlank(message = "服务事项不能为空")
    private String serviceItemId;
    private String applyContent;
    private String attachments;
    private BigDecimal fee;
}
