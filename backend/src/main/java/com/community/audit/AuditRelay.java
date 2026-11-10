package com.community.audit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Component
@ConditionalOnProperty(name = "app.audit.processing-enabled", havingValue = "true", matchIfMissing = true)
public class AuditRelay {
    private static final Logger log = LoggerFactory.getLogger(AuditRelay.class);
    private static final int MAX_ATTEMPTS = 12;
    private final AuditOutbox outbox;
    private final KafkaTemplate<String, String> kafka;

    public AuditRelay(AuditOutbox outbox, KafkaTemplate<String, String> kafka) {
        this.outbox = outbox;
        this.kafka = kafka;
    }

    @Scheduled(fixedDelayString = "${app.audit.poll-delay-ms:1000}")
    public void publishDue() {
        String owner = UUID.randomUUID().toString();
        for (AuditOutbox.Delivery entry : outbox.claim(owner)) {
            try {
                kafka.send(AuditEvent.TOPIC, Long.toString(entry.applicationId()), entry.payload())
                        .get(7, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            } catch (ExecutionException | TimeoutException | RuntimeException e) {
                Throwable cause = e.getCause() == null ? e : e.getCause();
                String reason = cause.getClass().getSimpleName();
                boolean exhausted = entry.attempts() >= MAX_ATTEMPTS;
                if (outbox.failed(entry, owner, reason, exhausted)) {
                    if (exhausted) {
                        log.error("Audit event {} needs replay after {} attempts ({})",
                                entry.eventId(), entry.attempts(), reason);
                    } else {
                        log.warn("Audit event {} will retry after attempt {} ({})",
                                entry.eventId(), entry.attempts(), reason);
                    }
                }
                continue;
            }
            if (!outbox.sent(entry.eventId(), owner)) {
                log.warn("Audit event {} was published after its lease changed", entry.eventId());
            }
        }
    }
}
