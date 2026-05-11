package com.community.entity.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PageDTO {

    private Integer pageNum = 1;

    private Integer pageSize = 10;

    private String keyword;

    private String sortBy;

    private Boolean isAsc = true;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Integer status;
}
