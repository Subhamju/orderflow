package com.orderflow.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.orderflow.domain.entity.Order;
import com.orderflow.execution.OrderExecutionEngine;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderKafkaConsumer {

    private final OrderExecutionEngine executionEngine;

    @KafkaListener(topics = "order.execution", groupId = "order-executor-group")
    public void consume(Order order) {
        log.info("Received order {} for execution from Kafka", order.getOrderId());
        executionEngine.execute(order);
    }

}
