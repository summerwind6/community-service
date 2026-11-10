package com.community.entity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class PublicProgressQueryDTO {
    @NotBlank(message = "申请编号不能为空")
    @Pattern(regexp = "(?i)AP[0-9]{12,27}", message = "申请编号格式错误")
    private String applyNo;

    @NotBlank(message = "请输入手机号后四位")
    @Pattern(regexp = "^\\d{4}$", message = "请输入手机号后四位")
    private String phoneLast4;
}
