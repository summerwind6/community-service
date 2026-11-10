package com.community.audit;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Configuration
@ConditionalOnProperty(name = "app.audit.processing-enabled", havingValue = "true", matchIfMissing = true)
public class AuditKafkaConfig {
    private static final Logger log = LoggerFactory.getLogger(AuditKafkaConfig.class);
    private static final Pattern EVENT_ID = Pattern.compile("\"eventId\"\\s*:\\s*\"(audit-v1-[0-9]+)\"");

    @Bean
    NewTopic auditTopic() {
        return TopicBuilder.name(AuditEvent.TOPIC).partitions(3).replicas(1).build();
    }

    @Bean
    NewTopic auditDeadLetters() {
        return TopicBuilder.name(AuditEvent.DLT).partitions(3).replicas(1).build();
    }

    @Bean
    DefaultErrorHandler auditErrorHandler(KafkaTemplate<String, String> kafka) {
        DeadLetterPublishingRecoverer deadLetters = new DeadLetterPublishingRecoverer(
                kafka, (record, error) -> new TopicPartition(AuditEvent.DLT, record.partition()));
        deadLetters.setFailIfSendResultIsError(true);
        deadLetters.setWaitForSendResultTimeout(Duration.ofSeconds(7));
        DefaultErrorHandler handler = new DefaultErrorHandler(deadLetters, new FixedBackOff(1_000, 3));
        handler.setRetryListeners((record, error, attempt) ->
                log.warn("Audit consumption failed for event {}, attempt {} ({})",
                        eventId(record), attempt, error.getClass().getSimpleName()));
        return handler;
    }

    private static String eventId(ConsumerRecord<?, ?> record) {
        if (record.value() instanceof String value) {
            Matcher id = EVENT_ID.matcher(value);
            if (id.find()) {
                return id.group(1);
            }
        }
        return record.topic() + "/" + record.partition() + "/" + record.offset();
    }
}
