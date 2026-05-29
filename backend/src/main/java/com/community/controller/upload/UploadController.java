package com.community.controller.upload;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.community.common.Result;
import com.community.entity.po.ApplicationForm;
import com.community.entity.vo.AttachmentVO;
import com.community.mapper.ApplicationFormMapper;
import com.community.util.FileUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Tag(name = "文件上传", description = "附件上传、预览和申请单附件查看")
@RestController
@RequestMapping("/upload")
public class UploadController {

    @Value("${app.upload-dir:uploads}")
    private String uploadDir;

    @Autowired
    private ApplicationFormMapper applicationFormMapper;

    @Operation(summary = "上传附件")
    @PostMapping
    public Result<String> upload(@RequestParam("file") MultipartFile file) throws IOException {
        String filePath = FileUtils.saveUploadFile(file, uploadDir);
        return Result.success(filePath);
    }

    @Operation(summary = "预览/下载文件")
    @GetMapping("/{filename}")
    public ResponseEntity<byte[]> preview(@PathVariable String filename, HttpServletRequest request) throws IOException {
        Path file = Paths.get(uploadDir).resolve(filename).normalize();
        if (!Files.exists(file)) {
            return ResponseEntity.notFound().build();
        }
        String contentType = request.getServletContext().getMimeType(filename);
        if (contentType == null) {
            contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }
        byte[] bytes = Files.readAllBytes(file);
        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(contentType))
            .body(bytes);
    }

    @Operation(summary = "按申请单查看附件列表")
    @GetMapping("/attachment/list")
    public Result<List<AttachmentVO>> attachmentList(@RequestParam String applyNo) {
        LambdaQueryWrapper<ApplicationForm> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ApplicationForm::getApplyNo, applyNo);
        ApplicationForm form = applicationFormMapper.selectOne(wrapper);
        return Result.success(FileUtils.parseAttachments(form == null ? null : form.getAttachments()));
    }
}
