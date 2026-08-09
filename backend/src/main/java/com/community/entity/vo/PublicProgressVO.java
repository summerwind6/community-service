package com.community.entity.vo;

import lombok.Data;

import java.time.LocalDateTime;

/** Safe-to-disclose view for the unauthenticated progress endpoint. */
@Data
public class PublicProgressVO {
    private String applyNo;
    private String serviceItemName;
    private String status;
    private LocalDateTime updateTime;

    private Boolean evaluated;
}
