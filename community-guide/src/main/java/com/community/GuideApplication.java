package com.community;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@MapperScan("com.community.mapper")
@SpringBootApplication
public class GuideApplication {
    public static void main(String[] args) {
        SpringApplication.run(GuideApplication.class, args);
    }
}
