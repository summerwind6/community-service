package com.community.entity.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class PublicEvaluationDTO {
    @NotBlank(message = "申请编号不能为空")
    private String applyNo;

    @NotBlank(message = "请输入手机号后四位")
    @Pattern(regexp = "^\\d{4}$", message = "请输入手机号后四位")
    private String phoneLast4;

    @NotNull(message = "请填写评分")
    @Min(value = 1, message = "评分范围为1到5")
    @Max(value = 5, message = "评分范围为1到5")
    private Integer score;

    private String content;
}
