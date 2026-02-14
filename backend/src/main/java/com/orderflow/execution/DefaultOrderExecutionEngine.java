package com.orderflow.execution;

import com.orderflow.domain.entity.Order;
import com.orderflow.domain.entity.OrderEvent;
import com.orderflow.domain.enums.OrderEventType;
import com.orderflow.domain.enums.OrderStatus;
import com.orderflow.execution.strategy.ExecutionStrategy;
import com.orderflow.execution.strategy.ExecutionStrategyFactory;
import com.orderflow.repository.OrderEventRepository;
import com.orderflow.repository.OrderRepository;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class DefaultOrderExecutionEngine implements OrderExecutionEngine {
    private final ExecutionStrategyFactory strategyFactory;
    private final OrderRepository orderRepository;
    private final OrderEventRepository orderEventRepository;

    public DefaultOrderExecutionEngine(ExecutionStrategyFactory strategyFactory,
            OrderRepository orderRepository, OrderEventRepository orderEventRepository) {
        this.strategyFactory = strategyFactory;
        this.orderRepository = orderRepository;
        this.orderEventRepository = orderEventRepository;
    }

    @Override
    @Transactional
    public void execute(Order order) {

        Order dbOrder = orderRepository.findById(order.getOrderId())
                .orElseThrow();

        if (dbOrder.getOrderStatus() == OrderStatus.CANCELLED) {
            log.info("Order {} cancelled before execution", dbOrder.getOrderId());
            return;
        }

        try {
            dbOrder.transitionTo(OrderStatus.EXECUTING);
            orderRepository.save(dbOrder);
            recordEvent(dbOrder.getOrderId(), OrderEventType.EXECUTING);

            ExecutionStrategy strategy = strategyFactory.getStrategy(dbOrder.getOrderKind());

            ExecutionResult result = strategy.execute(dbOrder);

            if (result == ExecutionResult.SUCCESS) {
                dbOrder.transitionTo(OrderStatus.EXECUTED);
                recordEvent(dbOrder.getOrderId(), OrderEventType.EXECUTED);
            } else {
                dbOrder.transitionTo(OrderStatus.FAILED);
                recordEvent(dbOrder.getOrderId(), OrderEventType.FAILED);
            }

            orderRepository.save(dbOrder);

        } catch (Exception ex) {
            log.error("Execution failed for order {}", order.getOrderId(), ex);
        }
    }

    private void recordEvent(Long orderId, OrderEventType type) {
        orderEventRepository.save(new OrderEvent(orderId, type));
    }

}
