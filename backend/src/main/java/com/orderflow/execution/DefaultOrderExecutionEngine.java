package com.orderflow.execution;

import com.orderflow.domain.entity.Order;
import com.orderflow.domain.entity.OrderEvent;
import com.orderflow.domain.enums.OrderEventType;
import com.orderflow.domain.enums.OrderStatus;
import com.orderflow.execution.strategy.ExecutionStrategy;
import com.orderflow.execution.strategy.ExecutionStrategyFactory;
import com.orderflow.repository.OrderEventRepository;
import com.orderflow.repository.OrderRepository;

import ch.qos.logback.core.spi.ConfigurationEvent.EventType;

import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class DefaultOrderExecutionEngine implements OrderExecutionEngine {
    private final ExecutorService executorService;
    private final ExecutionStrategyFactory strategyFactory;
    private final OrderRepository orderRepository;
    private final OrderEventRepository orderEventRepository;

    public DefaultOrderExecutionEngine(ExecutorService executorService, ExecutionStrategyFactory strategyFactory,
            OrderRepository orderRepository, OrderEventRepository orderEventRepository) {
        this.executorService = executorService;
        this.strategyFactory = strategyFactory;
        this.orderRepository = orderRepository;
        this.orderEventRepository = orderEventRepository;
    }

    @Override
    public void execute(Order order) {
        executorService.submit(() -> {
            try {
                order.transitionTo(OrderStatus.EXECUTING);
                orderRepository.save(order);

                ExecutionStrategy strategy = strategyFactory.getStrategy(order.getOrderKind());

                ExecutionResult result = strategy.execute(order);
                if (result == ExecutionResult.SUCCESS) {
                    order.transitionTo(OrderStatus.EXECUTED);
                    orderEventRepository.save(
                            new OrderEvent(order.getOrderId(), OrderEventType.EXECUTED));

                } else {
                    order.transitionTo(OrderStatus.FAILED);
                }
                orderRepository.save(order);

            } catch (Exception ex) {
                log.error("Execution failed for order {}", order.getOrderId(), ex);
                order.transitionTo(OrderStatus.FAILED);
                orderRepository.save(order);
            }

        });

    }
}
