package com.community.util;

import com.community.common.BusinessException;
import com.community.entity.vo.AttachmentVO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FileUtils {

    private static final List<String> IMAGE_SUFFIX = List.of("png", "jpg", "jpeg", "gif", "webp", "bmp");

    private static final List<String> ALLOWED_EXTENSIONS = List.of(
            "jpg", "jpeg", "png", "gif", "webp", "bmp",
            "pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx", "txt"
    );

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB

    public static String saveUploadFile(MultipartFile file, String uploadDir) throws IOException {
        String originalName = file.getOriginalFilename() == null ? "file" : file.getOriginalFilename();
        String suffix = "";
        int index = originalName.lastIndexOf('.');
        if (index >= 0) {
            suffix = originalName.substring(index);
        }
        String ext = suffix.isEmpty() ? "" : suffix.substring(1).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(ext)) {
            throw new BusinessException("不支持的文件类型: " + ext);
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BusinessException("文件大小不能超过10MB");
        }
        assertTrustedContent(file, ext);
        Path dir = Paths.get(uploadDir).toAbsolutePath().normalize();
        if (!Files.exists(dir)) {
            Files.createDirectories(dir);
        }
        String savedName = UUID.randomUUID().toString().replace("-", "") + suffix;
        Path target = dir.resolve(savedName);
        Files.copy(file.getInputStream(), target);
        return "/upload/" + savedName;
    }

    public static boolean isSafeStoredName(String filename) {
        return filename != null && filename.matches("^[a-f0-9]{32}\\.[a-zA-Z0-9]{1,10}$");
    }

    private static void assertTrustedContent(MultipartFile file, String ext) throws IOException {
        try (InputStream input = file.getInputStream()) {
            byte[] header = input.readNBytes(8);
            boolean isPdf = header.length >= 4 && new String(header, 0, 4, StandardCharsets.US_ASCII).equals("%PDF");
            boolean isZip = header.length >= 2 && header[0] == 'P' && header[1] == 'K';
            boolean isOle = header.length >= 8 && header[0] == (byte) 0xD0 && header[1] == (byte) 0xCF
                    && header[2] == 0x11 && header[3] == (byte) 0xE0;
            if ("pdf".equals(ext) && !isPdf) {
                throw new BusinessException("文件内容与扩展名不匹配");
            }
            if (List.of("docx", "xlsx", "pptx").contains(ext) && !isZip) {
                throw new BusinessException("文件内容与扩展名不匹配");
            }
            if (List.of("doc", "xls", "ppt").contains(ext) && !isOle) {
                throw new BusinessException("文件内容与扩展名不匹配");
            }
            if (IMAGE_SUFFIX.contains(ext)) {
                try (InputStream imageInput = file.getInputStream()) {
                    if (javax.imageio.ImageIO.read(imageInput) == null) {
                        throw new BusinessException("图片文件无效");
                    }
                }
            }
        }
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
