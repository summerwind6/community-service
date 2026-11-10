package com.community.audit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "app.audit.processing-enabled", havingValue = "true", matchIfMissing = true)
public class AuditNotificationListener {
    private final ObjectMapper mapper;
    private final AuditNotifications notifications;

    public AuditNotificationListener(ObjectMapper mapper, AuditNotifications notifications) {
        this.mapper = mapper;
        this.notifications = notifications;
    }

    @KafkaListener(topics = AuditEvent.TOPIC, groupId = AuditEvent.GROUP)
    public void receive(String payload) throws JsonProcessingException {
        notifications.consume(mapper.readValue(payload, AuditEvent.class));
    }
}
