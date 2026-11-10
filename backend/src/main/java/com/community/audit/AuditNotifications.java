package com.community.audit;

import com.community.entity.po.ApplicationForm;
import com.community.entity.po.AuditRecord;
import com.community.entity.po.Notification;
import com.community.entity.po.ResidentInfo;
import com.community.mapper.ApplicationFormMapper;
import com.community.mapper.AuditRecordMapper;
import com.community.service.NotificationService;
import com.community.service.ResidentInfoService;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
public class AuditNotifications {
    private final JdbcTemplate jdbc;
    private final ApplicationFormMapper forms;
    private final AuditRecordMapper records;
    private final ResidentInfoService residents;
    private final NotificationService notifications;

    public AuditNotifications(JdbcTemplate jdbc, ApplicationFormMapper forms, AuditRecordMapper records,
                              ResidentInfoService residents, NotificationService notifications) {
        this.jdbc = jdbc;
        this.forms = forms;
        this.records = records;
        this.residents = residents;
        this.notifications = notifications;
    }

    @Transactional
    public void consume(AuditEvent event) {
        if (event == null || event.schemaVersion() != 1 || event.applicationVersion() < 1
                || event.applicationId() == null || event.auditRecordId() == null || event.occurredAt() == null
                || !("audit-v1-" + event.auditRecordId()).equals(event.eventId())) {
            throw new IllegalArgumentException("Invalid audit event");
        }
        if (alreadyProcessed(event.eventId(), false)) {
            return;
        }
        AuditRecord record = records.selectById(event.auditRecordId());
        ApplicationForm form = forms.selectById(event.applicationId());
        if (record == null || form == null || !event.applicationId().equals(record.getApplicationId())
                || !record.getAuditType().equals(event.auditType()) || !record.getAuditResult().equals(event.auditResult())) {
            throw new IllegalArgumentException("Audit event does not match the audit record");
        }
        Notification notification = build(form, record);
        LocalDateTime occurredAt = LocalDateTime.ofInstant(event.occurredAt(), ZoneId.systemDefault());
        try {
            jdbc.update("""
                    INSERT INTO notification (title, content, type, target_id, target_name, target_phone,
                                              is_read, create_time, source_event_id)
                    VALUES (?, ?, ?, ?, ?, ?, 0, ?, ?)
                    """, notification.getTitle(), notification.getContent(), notification.getType(),
                    notification.getTargetId(), notification.getTargetName(), notification.getTargetPhone(),
                    occurredAt, event.eventId());
        } catch (DuplicateKeyException e) {
            if (!alreadyProcessed(event.eventId(), true)) {
                throw e;
            }
        }
    }

    private boolean alreadyProcessed(String eventId, boolean currentRead) {
        String sql = "SELECT id FROM notification WHERE source_event_id = ?" + (currentRead ? " FOR UPDATE" : "");
        return Boolean.TRUE.equals(jdbc.query(sql, result -> { return result.next(); }, eventId));
    }

    public void sendSynchronously(ApplicationForm form, AuditRecord record) {
        notifications.sendNotification(build(form, record));
    }

    private Notification build(ApplicationForm form, AuditRecord record) {
        String phone = "";
        if (form.getResidentId() != null) {
            ResidentInfo resident = residents.getResidentById(form.getResidentId());
            if (resident != null) {
                phone = resident.getPhone();
            }
        }
        String action = "REJECTED".equals(record.getAuditResult()) ? "已被驳回" : "已通过";
        String opinion = record.getAuditOpinion();
        Notification notification = new Notification();
        notification.setTitle("您的申请" + form.getApplyNo() + action);
        notification.setContent(opinion != null && !opinion.isEmpty() ? opinion
                : "您的便民服务申请" + form.getApplyNo() + action + "，请知悉。");
        notification.setType("AUDIT");
        notification.setTargetId(form.getId());
        notification.setTargetName(form.getResidentName());
        notification.setTargetPhone(phone);
        return notification;
    }
}
