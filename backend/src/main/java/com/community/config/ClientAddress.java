package com.community.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@Component
public class ClientAddress {
    private final byte[] gatewayToken;

    public ClientAddress(@Value("${app.gateway.token:}") String gatewayToken) {
        this.gatewayToken = gatewayToken.getBytes(StandardCharsets.UTF_8);
    }

    public String resolve(HttpServletRequest request) {
        String token = request.getHeader("X-Community-Gateway-Token");
        String ip = request.getHeader("X-Community-Client-IP");
        if (gatewayToken.length > 0 && token != null && ip != null
                && MessageDigest.isEqual(gatewayToken, token.getBytes(StandardCharsets.UTF_8))
                && ip.matches("[0-9a-fA-F:.]{2,45}")) {
            return ip;
        }
        return request.getRemoteAddr();
    }
}
