package com.community.gateway;

import org.junit.jupiter.api.Test;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.InetSocketAddress;

import static org.assertj.core.api.Assertions.assertThat;

class TrustedRequestHeadersTest {
    @Test
    void replacesSpoofedHeadersFromDirectClient() {
        TrustedRequestHeaders filter = new TrustedRequestHeaders("", "gateway-secret");
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest
                .get("/api/apply/progress")
                .remoteAddress(new InetSocketAddress("192.0.2.3", 4000))
                .header("Forwarded", "for=198.51.100.9")
                .header("X-Forwarded-For", "198.51.100.9")
                .header("X-Community-Client-IP", "198.51.100.9")
                .header("X-Community-Gateway-Token", "forged")
                .header("X-Community-Guide-Token", "forged")
                .header("X-Trace-Id", "test-trace")
                .header("Cookie", "SESSION=test")
                .build());

        filter.filter(exchange, forwarded -> {
            var headers = forwarded.getRequest().getHeaders();
            assertThat(headers.getFirst("X-Community-Client-IP")).isEqualTo("192.0.2.3");
            assertThat(headers.getFirst("X-Community-Gateway-Token")).isEqualTo("gateway-secret");
            assertThat(headers.getFirst("X-Trace-Id")).isEqualTo("test-trace");
            assertThat(headers.getFirst("Cookie")).isEqualTo("SESSION=test");
            assertThat(headers).doesNotContainKeys("X-Forwarded-For", "Forwarded", "X-Community-Guide-Token");
            return forwarded.getResponse().setComplete();
        }).block();

        assertThat(exchange.getResponse().getHeaders().getFirst("X-Trace-Id")).isEqualTo("test-trace");
    }

    @Test
    void takesNearestUntrustedAddressFromConfiguredProxyChain() {
        TrustedRequestHeaders filter = new TrustedRequestHeaders("192.0.2.2,192.0.2.3", "gateway-secret");
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest
                .get("/api/apply/progress")
                .remoteAddress(new InetSocketAddress("192.0.2.3", 4000))
                .header("X-Forwarded-For", "198.51.100.9, 203.0.113.4, 192.0.2.2")
                .build());

        filter.filter(exchange, forwarded -> {
            assertThat(forwarded.getRequest().getHeaders().getFirst("X-Community-Client-IP"))
                    .isEqualTo("203.0.113.4");
            return Mono.empty();
        }).block();
    }
}
