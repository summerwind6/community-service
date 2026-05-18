package com.community.controller.system;

import com.community.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.subject.support.DefaultSubjectContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Tag(name = "会话管理", description = "在线用户查看与强制下线")
@RestController
@RequestMapping("/session")
public class SessionController {

    @Autowired
    private SessionDAO sessionDAO;

    @Operation(summary = "获取在线用户列表")
    @RequiresPermissions("user:list")
    @GetMapping("/list")
    public Result<List<Map<String, Object>>> list() {
        List<Map<String, Object>> result = new ArrayList<>();
        Collection<Session> sessions = sessionDAO.getActiveSessions();
        for (Session session : sessions) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("sessionId", session.getId().toString());
            Object principal = session.getAttribute(DefaultSubjectContext.PRINCIPALS_SESSION_KEY);
            item.put("username", principal != null ? principal.toString() : "未知");
            item.put("host", session.getHost() != null ? session.getHost() : "未知");
            item.put("startTime", session.getStartTimestamp());
            item.put("lastAccess", session.getLastAccessTime());
            item.put("timeout", session.getTimeout() / 1000 / 60 + "分钟");
            result.add(item);
        }
        return Result.success(result);
    }

    @Operation(summary = "强制下线")
    @RequiresPermissions("user:update")
    @DeleteMapping("/{sessionId}")
    public Result<Void> kickOut(@PathVariable String sessionId) {
        Session session = sessionDAO.readSession(sessionId);
        if (session != null) {
            session.stop();
            sessionDAO.delete(session);
        }
        return Result.success();
    }
}
