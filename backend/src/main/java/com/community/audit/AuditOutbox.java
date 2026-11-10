package com.community.audit;

import com.community.entity.po.ApplicationForm;
import com.community.entity.po.AuditRecord;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class AuditOutbox {
    private final JdbcTemplate jdbc;
    private final ObjectMapper mapper;

    public AuditOutbox(JdbcTemplate jdbc, ObjectMapper mapper) {
        this.jdbc = jdbc;
        this.mapper = mapper;
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void append(ApplicationForm form, AuditRecord record) {
        if (record.getId() == null) {
            throw new IllegalStateException("Audit record was not persisted");
        }
        AuditEvent event = AuditEvent.from(form, record);
        try {
            jdbc.update("""
                    INSERT INTO application_event_outbox
                        (event_id, event_type, audit_record_id, application_id, payload, next_attempt_at, created_at)
                    VALUES (?, 'APPLICATION_AUDITED', ?, ?, ?, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6))
                    """, event.eventId(), event.auditRecordId(), event.applicationId(), mapper.writeValueAsString(event));
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Cannot serialize audit event", e);
        }
    }

    @Transactional
    public List<Delivery> claim(String owner) {
        List<Delivery> batch = jdbc.query("""
                SELECT event_id, application_id, payload, attempts
                  FROM application_event_outbox
                 WHERE status IN ('PENDING', 'SENDING') AND next_attempt_at <= UTC_TIMESTAMP(6)
                 ORDER BY next_attempt_at, event_id
                 LIMIT 5 FOR UPDATE SKIP LOCKED
                """, (row, index) -> new Delivery(row.getString("event_id"), row.getLong("application_id"),
                        row.getString("payload"), row.getInt("attempts") + 1));
        for (Delivery entry : batch) {
            jdbc.update("""
                    UPDATE application_event_outbox SET status = 'SENDING', attempts = ?, lease_owner = ?,
                        next_attempt_at = DATE_ADD(UTC_TIMESTAMP(6), INTERVAL 90 SECOND)
                    WHERE event_id = ?
                    """, entry.attempts(), owner, entry.eventId());
        }
        return batch;
    }

    public boolean sent(String eventId, String owner) {
        return jdbc.update("""
                UPDATE application_event_outbox SET status = 'SENT', lease_owner = NULL,
                    sent_at = UTC_TIMESTAMP(6), last_error = NULL
                WHERE event_id = ? AND status = 'SENDING' AND lease_owner = ?
                """, eventId, owner) == 1;
    }

    public boolean failed(Delivery entry, String owner, String reason, boolean exhausted) {
        int delay = Math.min(300, 1 << Math.min(entry.attempts(), 9));
        return jdbc.update("""
                UPDATE application_event_outbox SET status = ?, lease_owner = NULL, last_error = ?,
                    next_attempt_at = TIMESTAMPADD(SECOND, ?, UTC_TIMESTAMP(6))
                WHERE event_id = ? AND status = 'SENDING' AND lease_owner = ?
                """, exhausted ? "FAILED" : "PENDING", reason, delay, entry.eventId(), owner) == 1;
    }

    public record Delivery(String eventId, long applicationId, String payload, int attempts) {
    }
}
