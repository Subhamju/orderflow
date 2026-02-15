package com.orderflow.execution;

import com.orderflow.domain.entity.Order;
import com.orderflow.domain.entity.OrderEvent;
import com.orderflow.domain.enums.OrderEventType;
import com.orderflow.domain.enums.OrderStatus;
import com.orderflow.matching.MatchingEngine;
import com.orderflow.repository.OrderEventRepository;
import com.orderflow.repository.OrderRepository;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional
    public void execute(Long orderId) {

        Order dbOrder = orderRepository.findById(orderId)
                .orElseThrow();

        if (dbOrder.getOrderStatus() != OrderStatus.SENT_TO_EXECUTOR) {
            log.info("Order {} not executable. Current state: {}",
                    dbOrder.getOrderId(),
                    dbOrder.getOrderStatus());
            return;
        }

        dbOrder.transitionTo(OrderStatus.EXECUTING);
        orderRepository.save(dbOrder);
        recordEvent(orderId, OrderEventType.EXECUTING);
        matchingEngine.match(dbOrder);

    }

    private void recordEvent(Long orderId, OrderEventType type) {
        orderEventRepository.save(new OrderEvent(orderId, type));
    }

}
