package com.community.gateway;

import io.netty.util.NetUtil;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class TrustedRequestHeaders implements WebFilter {
    private final Set<String> trustedProxies;
    private final String gatewayToken;

    public TrustedRequestHeaders(@Value("${app.gateway.trusted-proxies:}") String trustedProxies,
                                 @Value("${app.gateway.token}") String gatewayToken) {
        Assert.hasText(gatewayToken, "GATEWAY_TOKEN is required");
        this.gatewayToken = gatewayToken;
        this.trustedProxies = Arrays.stream(trustedProxies.split(","))
                .map(String::trim).filter(value -> !value.isEmpty()).map(TrustedRequestHeaders::normalizeIp)
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String ip = clientIp(request);
        String supplied = request.getHeaders().getFirst("X-Trace-Id");
        String trace = supplied != null && supplied.matches("[a-zA-Z0-9._-]{1,64}")
                ? supplied : UUID.randomUUID().toString();
        exchange.getResponse().beforeCommit(() -> {
            exchange.getResponse().getHeaders().set("X-Trace-Id", trace);
            return Mono.empty();
        });
        ServerHttpRequest outgoing = request.mutate().headers(headers -> {
            List<String> discarded = headers.keySet().stream().filter(TrustedRequestHeaders::untrusted).toList();
            discarded.forEach(headers::remove);
            headers.set("X-Community-Client-IP", ip);
            headers.set("X-Community-Gateway-Token", gatewayToken);
            headers.set("X-Trace-Id", trace);
        }).build();
        return chain.filter(exchange.mutate().request(outgoing).build());
    }

    private String clientIp(ServerHttpRequest request) {
        InetSocketAddress remote = request.getRemoteAddress();
        if (remote == null || remote.getAddress() == null) {
            return "0.0.0.0";
        }
        String peer = normalizeIp(remote.getAddress().getHostAddress().split("%", 2)[0]);
        if (!trustedProxies.contains(peer)) {
            return peer;
        }
        List<String> forwarded = request.getHeaders().getOrEmpty("X-Forwarded-For");
        int hops = 0;
        for (int line = forwarded.size() - 1; line >= 0; line--) {
            String[] addresses = forwarded.get(line).split(",", -1);
            for (int i = addresses.length - 1; i >= 0; i--) {
                String candidate = addresses[i].trim();
                if (++hops > 16 || !isIp(candidate)) {
                    return peer;
                }
                String address = normalizeIp(candidate);
                if (!trustedProxies.contains(address)) {
                    return address;
                }
            }
        }
        return peer;
    }

    private static boolean untrusted(String name) {
        String key = name.toLowerCase(Locale.ROOT);
        return key.equals("forwarded") || key.startsWith("x-forwarded-") || key.equals("x-real-ip")
                || key.startsWith("x-community-");
    }

    private static boolean isIp(String value) {
        return value.indexOf('%') < 0 && value.indexOf('[') < 0
                && (NetUtil.isValidIpV4Address(value) || NetUtil.isValidIpV6Address(value));
    }

    private static String normalizeIp(String value) {
        Assert.isTrue(isIp(value), "Trusted proxy addresses must be IPv4 or IPv6 literals");
        return NetUtil.bytesToIpAddress(NetUtil.createByteArrayFromIpAddressString(value));
    }
}
