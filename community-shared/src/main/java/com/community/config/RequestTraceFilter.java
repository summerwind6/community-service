package com.community.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
public class RequestTraceFilter extends OncePerRequestFilter {
    private static final Logger log = LoggerFactory.getLogger(RequestTraceFilter.class);
    private static final long SLOW_REQUEST_MS = 1_000;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String traceId = request.getHeader("X-Trace-Id");
        if (traceId == null || !traceId.matches("[a-zA-Z0-9._-]{1,64}")) {
            traceId = UUID.randomUUID().toString().replace("-", "");
        }
        long startedAt = System.currentTimeMillis();
        MDC.put("traceId", traceId);
        response.setHeader("X-Trace-Id", traceId);
        try {
            filterChain.doFilter(request, response);
        } finally {
            long elapsed = System.currentTimeMillis() - startedAt;
            if (elapsed >= SLOW_REQUEST_MS) {
                log.warn("slow request traceId={}, method={}, path={}, elapsedMs={}", traceId,
                        request.getMethod(), request.getRequestURI(), elapsed);
            }
            MDC.remove("traceId");
        }
    }
}
