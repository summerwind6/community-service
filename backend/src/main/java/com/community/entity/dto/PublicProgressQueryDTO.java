package com.community.entity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class PublicProgressQueryDTO {
    @NotBlank(message = "申请编号不能为空")
    private String applyNo;

    @Pattern(regexp = "^\\d{4}$", message = "请输入手机号后四位")
    private String phoneLast4;
}
