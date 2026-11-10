package com.community.config;

import com.community.common.Result;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.dao.QueryTimeoutException;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.session.web.http.SessionRepositoryFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Order(SessionRepositoryFilter.DEFAULT_ORDER - 1)
public class SessionDependencyFilter extends OncePerRequestFilter {
    private static final Logger log = LoggerFactory.getLogger(SessionDependencyFilter.class);
    private final ObjectMapper mapper;

    public SessionDependencyFilter(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        try {
            chain.doFilter(request, response);
        } catch (RedisConnectionFailureException | QueryTimeoutException e) {
            if (response.isCommitted()) {
                throw e;
            }
            log.warn("Session dependency unavailable ({})", e.getClass().getSimpleName());
            response.resetBuffer();
            response.setStatus(503);
            response.setContentType("application/json");
            mapper.writeValue(response.getOutputStream(), Result.error(503, "服务暂不可用，请稍后重试"));
        }
    }
}
