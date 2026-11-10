package com.community.audit;

import com.community.entity.po.ApplicationForm;
import com.community.entity.po.AuditRecord;
import com.community.mapper.ApplicationFormMapper;
import com.community.mapper.AuditRecordMapper;
import com.community.service.NotificationService;
import com.community.service.ResidentInfoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AuditDeliveryTest {
    private final ApplicationForm form = new ApplicationForm();
    private final AuditRecord record = new AuditRecord();
    private JdbcTemplate jdbc;
    private TransactionTemplate transaction;

    @BeforeEach
    void prepare() {
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setURL("jdbc:h2:mem:" + UUID.randomUUID() + ";MODE=MySQL;DB_CLOSE_DELAY=-1");
        jdbc = new JdbcTemplate(dataSource);
        transaction = new TransactionTemplate(new DataSourceTransactionManager(dataSource));
        form.setId(7L);
        form.setVersion(1);
        form.setApplyNo("AP2026111000000007");
        record.setId(11L);
        record.setApplicationId(7L);
        record.setAuditType("COMMUNITY");
        record.setAuditResult("PROCESSING");
        record.setAuditOpinion("现场审核意见");
    }

    @Test
    void auditOutboxRollsBackAndPublishesOnlyInternalFields() {
        jdbc.execute("CREATE ALIAS UTC_TIMESTAMP FOR 'com.community.audit.AuditDeliveryTest.utcTimestamp'");
        jdbc.execute("""
                CREATE TABLE application_event_outbox (
                    event_id VARCHAR(64) PRIMARY KEY, event_type VARCHAR(40), audit_record_id BIGINT,
                    application_id BIGINT, payload VARCHAR(4000), next_attempt_at TIMESTAMP, created_at TIMESTAMP
                )
                """);
        AuditOutbox outbox = new AuditOutbox(jdbc, new ObjectMapper().findAndRegisterModules());
        transaction.executeWithoutResult(status -> {
            outbox.append(form, record);
            status.setRollbackOnly();
        });
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM application_event_outbox", Long.class)).isZero();

        transaction.executeWithoutResult(status -> outbox.append(form, record));
        String payload = jdbc.queryForObject("SELECT payload FROM application_event_outbox", String.class);
        assertThat(payload).contains("audit-v1-11", "applicationVersion", "occurredAt")
                .doesNotContain("现场审核意见", form.getApplyNo());
    }

    @Test
    void replaysProduceOneNotificationUsingAuditTimeAndRecordOpinion() {
        jdbc.execute("""
                CREATE TABLE notification (
                    id BIGINT AUTO_INCREMENT PRIMARY KEY, title VARCHAR(200), content TEXT, type VARCHAR(20),
                    target_id BIGINT, target_name VARCHAR(50), target_phone VARCHAR(20),
                    is_read INTEGER, create_time TIMESTAMP, source_event_id VARCHAR(64) UNIQUE
                )
                """);
        ApplicationFormMapper forms = mock(ApplicationFormMapper.class);
        AuditRecordMapper records = mock(AuditRecordMapper.class);
        when(forms.selectById(7L)).thenReturn(form);
        when(records.selectById(11L)).thenReturn(record);
        AuditNotifications notifications = new AuditNotifications(jdbc, forms, records,
                mock(ResidentInfoService.class), mock(NotificationService.class));
        Instant when = Instant.parse("2026-11-10T02:00:00Z");
        AuditEvent event = new AuditEvent("audit-v1-11", 1, 7L, 11L, "COMMUNITY", "PROCESSING", 1, when);

        transaction.executeWithoutResult(status -> notifications.consume(event));
        transaction.executeWithoutResult(status -> notifications.consume(event));

        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM notification", Long.class)).isEqualTo(1);
        assertThat(jdbc.queryForObject("SELECT content FROM notification", String.class)).isEqualTo("现场审核意见");
        assertThat(jdbc.queryForObject("SELECT create_time FROM notification", LocalDateTime.class))
                .isEqualTo(LocalDateTime.ofInstant(when, ZoneId.systemDefault()));
    }

    public static Timestamp utcTimestamp(int precision) {
        return Timestamp.from(Instant.now());
    }
}
