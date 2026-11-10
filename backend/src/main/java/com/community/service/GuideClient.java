package com.community.service;

import com.community.common.BusinessException;
import com.community.common.Result;
import com.community.entity.dto.PageDTO;
import com.community.entity.po.Guide;
import com.community.entity.vo.PageVO;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class GuideClient {
    private final RestClient client;

    public GuideClient(@LoadBalanced RestClient.Builder builder, @Value("${app.guide.token:}") String token) {
        this.client = builder.baseUrl("http://community-guide")
                .defaultHeader("X-Community-Guide-Token", token)
                .requestInterceptor((request, body, execution) -> {
                    String traceId = MDC.get("traceId");
                    if (traceId != null) {
                        request.getHeaders().set("X-Trace-Id", traceId);
                    }
                    return execution.execute(request, body);
                })
                .build();
    }

    public Result<PageVO<Guide>> list(PageDTO page, String category) {
        return call(client.get().uri(builder -> {
            builder.path("/api/internal/guide").queryParam("pageNum", page.getPageNum())
                    .queryParam("pageSize", page.getPageSize());
            Map<String, String> parameters = new HashMap<>();
            if (page.getKeyword() != null) {
                builder.queryParam("keyword", "{keyword}");
                parameters.put("keyword", page.getKeyword());
            }
            if (page.getStatus() != null) {
                builder.queryParam("status", page.getStatus());
            }
            if (category != null) {
                builder.queryParam("category", "{category}");
                parameters.put("category", category);
            }
            return builder.build(parameters);
        }), new ParameterizedTypeReference<>() {});
    }

    public Result<List<Guide>> publicList() {
        return call(client.get().uri("/api/guide/public"), new ParameterizedTypeReference<>() {});
    }

    public Result<Guide> detail(Long id) {
        return call(client.get().uri("/api/internal/guide/{id}", id), new ParameterizedTypeReference<>() {});
    }

    public Result<Guide> add(Guide guide) {
        return call(client.post().uri("/api/internal/guide").body(guide), new ParameterizedTypeReference<>() {});
    }

    public Result<Guide> update(Guide guide) {
        return call(client.put().uri("/api/internal/guide").body(guide), new ParameterizedTypeReference<>() {});
    }

    public Result<Void> delete(Long id) {
        return call(client.delete().uri("/api/internal/guide/{id}", id), new ParameterizedTypeReference<>() {});
    }

    private <T> Result<T> call(RestClient.RequestHeadersSpec<?> request, ParameterizedTypeReference<Result<T>> type) {
        try {
            Result<T> response = request.retrieve().body(type);
            if (response != null) {
                return response;
            }
        } catch (RestClientException | IllegalStateException e) {
            throw new BusinessException(503, "办事指南暂不可用，请稍后重试");
        }
        throw new BusinessException(503, "办事指南暂不可用，请稍后重试");
    }
}
