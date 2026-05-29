package com.community.util;

import com.community.entity.vo.AttachmentVO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FileUtils {

    private static final List<String> IMAGE_SUFFIX = List.of("png", "jpg", "jpeg", "gif", "webp", "bmp");

    public static String saveUploadFile(MultipartFile file, String uploadDir) throws IOException {
        Path dir = Paths.get(uploadDir);
        if (!Files.exists(dir)) {
            Files.createDirectories(dir);
        }
        String originalName = file.getOriginalFilename() == null ? "file" : file.getOriginalFilename();
        String suffix = "";
        int index = originalName.lastIndexOf('.');
        if (index >= 0) {
            suffix = originalName.substring(index);
        }
        String savedName = UUID.randomUUID().toString().replace("-", "") + suffix;
        Path target = dir.resolve(savedName);
        Files.copy(file.getInputStream(), target);
        return "/upload/" + savedName;
    }

    public static List<AttachmentVO> parseAttachments(String attachmentsJson) {
        List<AttachmentVO> list = new ArrayList<>();
        if (attachmentsJson == null || attachmentsJson.isBlank()) {
            return list;
        }
        String trimmed = attachmentsJson.trim();
        if (trimmed.startsWith("[") && trimmed.endsWith("]")) {
            trimmed = trimmed.substring(1, trimmed.length() - 1);
            if (!trimmed.isBlank()) {
                String[] arr = trimmed.split(",");
                for (String item : arr) {
                    String path = item.trim().replace("\"", "");
                    if (!path.isBlank()) {
                        list.add(buildAttachment(path));
                    }
                }
            }
        } else {
            list.add(buildAttachment(trimmed.replace("\"", "")));
        }
        return list;
    }

    private static AttachmentVO buildAttachment(String path) {
        AttachmentVO vo = new AttachmentVO();
        vo.setFilePath(path);
        String fileName = path;
        int slash = path.lastIndexOf('/');
        if (slash >= 0 && slash < path.length() - 1) {
            fileName = path.substring(slash + 1);
        }
        vo.setFileName(fileName);
        String suffix = "";
        int dot = fileName.lastIndexOf('.');
        if (dot >= 0 && dot < fileName.length() - 1) {
            suffix = fileName.substring(dot + 1).toLowerCase();
        }
        vo.setFileType(suffix);
        vo.setImage(IMAGE_SUFFIX.contains(suffix));
        return vo;
    }
}
