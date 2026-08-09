package com.community.service.impl;

import com.community.common.BusinessException;
import com.community.entity.vo.AttachmentVO;
import com.community.service.TemporaryAttachmentService;
import com.community.util.FileUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class TemporaryAttachmentServiceImpl implements TemporaryAttachmentService {
    private static final String PREFIX = "upload:temp:";
    private final RedisTemplate<String, Object> redisTemplate;

    public TemporaryAttachmentServiceImpl(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public String create(String storedFilename) {
        if (!FileUtils.isSafeStoredName(storedFilename)) {
            throw new BusinessException(400, "附件标识无效");
        }
        String token = UUID.randomUUID().toString().replace("-", "");
        redisTemplate.opsForValue().set(PREFIX + token, storedFilename, Duration.ofMinutes(30));
        return token;
    }

    @Override
    public List<String> consumeAll(String attachmentsJson) {
        List<String> result = new ArrayList<>();
        for (AttachmentVO attachment : FileUtils.parseAttachments(attachmentsJson)) {
            String path = attachment.getFilePath();
            String token = path != null && path.startsWith("/upload/temp/") ? path.substring("/upload/temp/".length()) : "";
            if (!token.matches("^[a-f0-9]{32}$")) {
                throw new BusinessException(400, "临时附件已失效，请重新上传");
            }
            Object filename = redisTemplate.opsForValue().get(PREFIX + token);
            if (!(filename instanceof String storedFilename) || !FileUtils.isSafeStoredName(storedFilename)) {
                throw new BusinessException(400, "临时附件已失效，请重新上传");
            }
            redisTemplate.delete(PREFIX + token);
            result.add("/upload/" + storedFilename);
        }
        return result;
    }
}
