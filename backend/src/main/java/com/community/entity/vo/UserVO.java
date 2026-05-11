package com.community.entity.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
public class UserVO {

    private Long id;
    private String username;
    private String password;
    private String realName;
    private String phone;
    private String email;
    private String avatar;
    private  String salt;
    private Integer status;
    private LocalDateTime createTime;
    private Set<String> roles;
}


