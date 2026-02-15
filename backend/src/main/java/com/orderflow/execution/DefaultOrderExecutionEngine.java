package com.orderflow.execution;

import com.orderflow.domain.entity.Order;
import com.orderflow.domain.entity.OrderEvent;
import com.orderflow.domain.enums.OrderEventType;
import com.orderflow.domain.enums.OrderStatus;
import com.orderflow.matching.MatchingEngine;
import com.orderflow.repository.OrderEventRepository;
import com.orderflow.repository.OrderRepository;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class DefaultOrderExecutionEngine implements OrderExecutionEngine {

    private final MatchingEngine matchingEngine;
    private final OrderRepository orderRepository;
    private final OrderEventRepository orderEventRepository;

    @Override
    public void execute(Long orderId) {

        Order dbOrder = orderRepository.findById(orderId)
                .orElseThrow();

        if (dbOrder.getOrderStatus() != OrderStatus.SENT_TO_EXECUTOR) {
            log.info("Order {} not executable. Current state: {}",
                    dbOrder.getOrderId(),
                    dbOrder.getOrderStatus());
            return;
        }

        try {
            dbOrder.transitionTo(OrderStatus.EXECUTING);
            orderRepository.save(dbOrder);
            recordEvent(dbOrder.getOrderId(), OrderEventType.EXECUTING);

            matchingEngine.match(dbOrder);

            if (dbOrder.getRemainingQuantity() == 0) {
                dbOrder.transitionTo(OrderStatus.EXECUTED);
                recordEvent(dbOrder.getOrderId(), OrderEventType.EXECUTED);
            } else if (dbOrder.getRemainingQuantity() < dbOrder.getQuantity()) {
                dbOrder.transitionTo(OrderStatus.PARTIALLY_FILLED);
                recordEvent(dbOrder.getOrderId(), OrderEventType.PARTIALLY_FILLED);
            }

            orderRepository.save(dbOrder);

        } catch (Exception ex) {
            log.error("Execution failed for order {}", orderId, ex);
            dbOrder.transitionTo(OrderStatus.FAILED);
            recordEvent(dbOrder.getOrderId(), OrderEventType.FAILED);
            orderRepository.save(dbOrder);
        }
    }

    private void recordEvent(Long orderId, OrderEventType type) {
        orderEventRepository.save(new OrderEvent(orderId, type));
    }

}
