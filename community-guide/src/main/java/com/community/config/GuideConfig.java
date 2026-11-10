package com.community.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.community.common.Result;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@Configuration
public class GuideConfig {
    @Bean
    MybatisPlusInterceptor guidePagination() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }

    @Bean
    FilterRegistrationBean<OncePerRequestFilter> guideInternalAuthentication(
            @Value("${app.guide.token}") String token, ObjectMapper mapper) {
        Assert.hasText(token, "GUIDE_SERVICE_TOKEN is required");
        byte[] expected = token.getBytes(StandardCharsets.UTF_8);
        OncePerRequestFilter auth = new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain chain) throws ServletException, IOException {
                String supplied = request.getHeader("X-Community-Guide-Token");
                if (supplied != null && MessageDigest.isEqual(expected, supplied.getBytes(StandardCharsets.UTF_8))) {
                    chain.doFilter(request, response);
                    return;
                }
                response.setStatus(403);
                response.setContentType("application/json");
                mapper.writeValue(response.getOutputStream(), Result.error(403, "没有权限访问该资源"));
            }
        };
        FilterRegistrationBean<OncePerRequestFilter> registration = new FilterRegistrationBean<>(auth);
        registration.addUrlPatterns("/internal/*");
        return registration;
    }
}
