package com.orderflow.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.orderflow.domain.entity.Order;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OrderKafkaProducer {

    private final KafkaTemplate<String, Order> kafkaTemplate;

    public void publishOrderForExecution(Order order) {
        kafkaTemplate.send("order.execution", order.getOrderId().toString(), order);
    }

}
