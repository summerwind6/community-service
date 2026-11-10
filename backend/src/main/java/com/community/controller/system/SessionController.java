package com.community.controller.system;

import com.community.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.session.Session;
import org.springframework.session.SessionRepository;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Tag(name = "会话管理", description = "在线用户查看与强制下线")
@RestController
@RequestMapping("/session")
public class SessionController {

    private static final String PREFIX = "spring:session:sessions:";
    private static final ScanOptions OPTIONS = ScanOptions.scanOptions().match(PREFIX + "*").count(100).build();

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private SessionRepository<? extends Session> sessionRepository;

    @Operation(summary = "获取在线用户列表")
    @RequiresPermissions("user:list")
    @GetMapping("/list")
    public Result<List<Map<String, Object>>> list() {
        List<Map<String, Object>> result = new ArrayList<>();
        Set<String> seen = new HashSet<>();
        long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(2);
        try (Cursor<String> cursor = redisTemplate.scan(OPTIONS)) {
            int scanned = 0;
            while (scanned < 1_000 && System.nanoTime() < deadline && cursor.hasNext()) {
                String sessionId = cursor.next().substring(PREFIX.length());
                scanned++;
                if (sessionId.contains(":") || !seen.add(sessionId)) continue;
                Session session = sessionRepository.findById(sessionId);
                if (session == null) continue;
                Map<String, Object> item = new LinkedHashMap<>();
                item.put("sessionId", session.getId());
                item.put("username", session.getAttribute("username"));
                item.put("host", "Spring Session");
                item.put("startTime", session.getCreationTime());
                item.put("lastAccess", session.getLastAccessedTime());
                item.put("timeout", session.getMaxInactiveInterval().getSeconds() / 60 + "分钟");
                result.add(item);
            }
        }
        return Result.success(result);
    }

    @Operation(summary = "强制下线")
    @RequiresPermissions("user:update")
    @DeleteMapping("/{sessionId}")
    public Result<Void> kickOut(@PathVariable String sessionId) {
        sessionRepository.deleteById(sessionId);
        return Result.success();
    }
}
