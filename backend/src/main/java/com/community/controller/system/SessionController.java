package com.community.controller.system;

import com.community.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.session.Session;
import org.springframework.session.SessionRepository;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Tag(name = "会话管理", description = "在线用户查看与强制下线")
@RestController
@RequestMapping("/session")
public class SessionController {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private SessionRepository<? extends Session> sessionRepository;

    @Operation(summary = "获取在线用户列表")
    @RequiresPermissions("user:list")
    @GetMapping("/list")
    public Result<List<Map<String, Object>>> list() {
        List<Map<String, Object>> result = new ArrayList<>();
        // Spring Session 的 Redis key 格式: spring:session:sessions:<id>
        Set<String> keys = redisTemplate.keys("spring:session:sessions:*");
        if (keys == null || keys.isEmpty()) return Result.success(result);

        for (String key : keys) {
            String sessionId = key.replace("spring:session:sessions:", "");
            // 跳过过期/索引等辅助 key
            if (sessionId.contains(":")) continue;
            try {
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
            } catch (Exception ignored) {
                // session expired between key scan and fetch
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
