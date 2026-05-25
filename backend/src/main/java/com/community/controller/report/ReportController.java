package com.community.controller.report;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.community.entity.po.ApplicationForm;
import com.community.entity.po.Evaluation;
import com.community.entity.po.ResidentInfo;
import com.community.mapper.ApplicationFormMapper;
import com.community.mapper.EvaluationMapper;
import com.community.mapper.ResidentInfoMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Tag(name = "Report", description = "Excel export")
@RestController
@RequestMapping("/report")
public class ReportController {

    @Autowired
    private ApplicationFormMapper applicationFormMapper;

    @Autowired
    private ResidentInfoMapper residentInfoMapper;

    @Autowired
    private EvaluationMapper evaluationMapper;

    @Operation(summary = "Export applications")
    @RequiresPermissions("report:export")
    @GetMapping("/export/apply")
    public void exportApply(@RequestParam(required = false) String status,
                            @RequestParam(required = false) String startTime,
                            @RequestParam(required = false) String endTime,
                            HttpServletResponse response) throws IOException {
        LambdaQueryWrapper<ApplicationForm> wrapper = new LambdaQueryWrapper<>();
        if (status != null && !status.isBlank()) {
            wrapper.eq(ApplicationForm::getStatus, status);
        }
        LocalDateTime start = parseTime(startTime);
        LocalDateTime end = parseTime(endTime);
        if (start != null) {
            wrapper.ge(ApplicationForm::getCreateTime, start);
        }
        if (end != null) {
            wrapper.le(ApplicationForm::getCreateTime, end);
        }
        wrapper.orderByDesc(ApplicationForm::getCreateTime);
        List<ApplicationForm> records = applicationFormMapper.selectList(wrapper);

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("applications");
        writeHeader(sheet, "Apply No", "Resident", "Service Item", "Status", "Fee", "Handler", "Create Time");
        for (int i = 0; i < records.size(); i++) {
            ApplicationForm item = records.get(i);
            Row row = sheet.createRow(i + 1);
            row.createCell(0).setCellValue(value(item.getApplyNo()));
            row.createCell(1).setCellValue(value(item.getResidentName()));
            row.createCell(2).setCellValue(value(item.getServiceItemName()));
            row.createCell(3).setCellValue(value(item.getStatus()));
            row.createCell(4).setCellValue(item.getFee() == null ? "" : item.getFee().toPlainString());
            row.createCell(5).setCellValue(value(item.getHandlerName()));
            row.createCell(6).setCellValue(formatTime(item.getCreateTime()));
        }
        writeWorkbook(response, workbook, "applications.xlsx");
    }

    @Operation(summary = "Export residents")
    @RequiresPermissions("report:export")
    @GetMapping("/export/resident")
    public void exportResident(HttpServletResponse response) throws IOException {
        List<ResidentInfo> records = residentInfoMapper.selectList(new LambdaQueryWrapper<ResidentInfo>()
                .orderByDesc(ResidentInfo::getCreateTime));
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("residents");
        writeHeader(sheet, "Name", "Phone", "ID Card", "Community", "Address", "Gender", "Create Time");
        for (int i = 0; i < records.size(); i++) {
            ResidentInfo item = records.get(i);
            Row row = sheet.createRow(i + 1);
            row.createCell(0).setCellValue(value(item.getName()));
            row.createCell(1).setCellValue(value(item.getPhone()));
            row.createCell(2).setCellValue(value(item.getIdCard()));
            row.createCell(3).setCellValue(value(item.getCommunity()));
            row.createCell(4).setCellValue(value(item.getAddress()));
            row.createCell(5).setCellValue(value(item.getGender()));
            row.createCell(6).setCellValue(formatTime(item.getCreateTime()));
        }
        writeWorkbook(response, workbook, "residents.xlsx");
    }

    @Operation(summary = "Export evaluations")
    @RequiresPermissions("report:export")
    @GetMapping("/export/evaluation")
    public void exportEvaluation(HttpServletResponse response) throws IOException {
        List<Evaluation> records = evaluationMapper.selectList(new LambdaQueryWrapper<Evaluation>()
                .orderByDesc(Evaluation::getCreateTime));
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("evaluations");
        writeHeader(sheet, "Application ID", "Score", "Content", "Create Time");
        for (int i = 0; i < records.size(); i++) {
            Evaluation item = records.get(i);
            Row row = sheet.createRow(i + 1);
            row.createCell(0).setCellValue(item.getApplicationId() == null ? "" : String.valueOf(item.getApplicationId()));
            row.createCell(1).setCellValue(item.getScore() == null ? "" : String.valueOf(item.getScore()));
            row.createCell(2).setCellValue(value(item.getContent()));
            row.createCell(3).setCellValue(formatTime(item.getCreateTime()));
        }
        writeWorkbook(response, workbook, "evaluations.xlsx");
    }

    private void writeHeader(Sheet sheet, String... headers) {
        Row row = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            row.createCell(i).setCellValue(headers[i]);
            sheet.setColumnWidth(i, 20 * 256);
        }
    }

    private void writeWorkbook(HttpServletResponse response, Workbook workbook, String filename) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        String encoded = URLEncoder.encode(filename, StandardCharsets.UTF_8).replace("+", "%20");
        response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + encoded);
        workbook.write(response.getOutputStream());
        workbook.close();
    }

    private LocalDateTime parseTime(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return LocalDateTime.parse(value, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    private String formatTime(LocalDateTime value) {
        return value == null ? "" : value.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    private String value(String value) {
        return value == null ? "" : value;
    }
}
