package com.community.entity.vo;

import lombok.Data;

@Data
public class AttachmentVO {

    private String fileName;
    private String filePath;
    private String fileType;
    private Boolean image;
}
