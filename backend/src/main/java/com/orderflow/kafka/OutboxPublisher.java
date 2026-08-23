package com.orderflow.kafka;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.orderflow.domain.entity.OutboxEvent;
import com.orderflow.domain.enums.OutboxEventStatus;
import com.orderflow.domain.enums.OutboxEventType;
import com.orderflow.repository.OutboxEventRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
@Profile("!test")
public class OutboxPublisher {

    private static final int BATCH_SIZE = 100;

    private final OutboxEventRepository outboxEventRepository;
    private final OrderKafkaProducer orderKafkaProducer;

    @Scheduled(fixedDelayString = "${orderflow.outbox.publish-interval-ms:1000}")
    @Transactional
    public void publishPendingEvents() {
        List<OutboxEvent> events = outboxEventRepository.findByStatusOrderByCreatedAtAsc(
                OutboxEventStatus.PENDING, PageRequest.of(0, BATCH_SIZE));

        for (OutboxEvent event : events) {
            try {
                publish(event);
                event.markPublished();
            } catch (Exception ex) {
                event.recordPublishFailure(ex.getMessage());
                log.warn("Unable to publish outbox event {} for order {}", event.getId(),
                        event.getAggregateId(), ex);
            }
        }
    }

    private void publish(OutboxEvent event) throws Exception {
        if (event.getEventType() == OutboxEventType.ORDER_EXECUTION_REQUESTED) {
            orderKafkaProducer.publishOrderForExecution(event.getAggregateId());
            return;
        }
        throw new IllegalStateException("Unsupported outbox event type: " + event.getEventType());
    }
}
