package com.community;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@SpringBootApplication
@MapperScan("com.community.mapper")
@EnableRedisHttpSession
public class CommunityApplication {

    public static void main(String[] args) {
        SpringApplication.run(CommunityApplication.class, args);
        System.out.println("============================================");
        System.out.println("  社区便民服务申请与进度管理系统 启动成功！");
        System.out.println("  Swagger文档地址: http://localhost:8080/api/swagger-ui.html");
        System.out.println("============================================");
    }
}
