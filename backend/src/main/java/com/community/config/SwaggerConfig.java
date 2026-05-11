package com.community.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("社区便民服务申请与进度管理系统 API")
                        .description("政企简化版 - 接口文档<br/>" +
                                "权限角色：超级管理员(ADMIN)、社区受理员(CLERK)、街道审核员(REVIEWER)")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("开发小组")
                                .email("dev@community.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://www.apache.org/licenses/LICENSE-2.0")));
    }
}
