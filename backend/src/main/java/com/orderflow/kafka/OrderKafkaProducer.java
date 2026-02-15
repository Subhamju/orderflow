package com.orderflow.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import com.orderflow.kafka.dto.OrderExecutionEvent;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OrderKafkaProducer {

    private final KafkaTemplate<String, OrderExecutionEvent> kafkaTemplate;

    public void publishOrderForExecution(Long orderId) {
        OrderExecutionEvent event = new OrderExecutionEvent(orderId);
        kafkaTemplate.send("order.execution", event);
    }

}
