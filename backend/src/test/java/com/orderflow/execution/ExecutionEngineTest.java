package com.orderflow.execution;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.orderflow.OrderTestFactory;
import com.orderflow.domain.entity.Order;
import com.orderflow.domain.enums.OrderStatus;
import com.orderflow.repository.OrderRepository;

@SpringBootTest(properties = {
        "spring.kafka.listener.auto-startup=false"
})
@ActiveProfiles("test")
@Transactional
class ExecutionEngineTest {

    @Autowired
    private DefaultOrderExecutionEngine engine;

    @Autowired
    private OrderRepository orderRepository;

    @Test
    void shouldNotExecuteAlreadyCancelledOrder() {

        Order order = OrderTestFactory.limitBuy(100, 40.0);
        order.transitionTo(OrderStatus.CANCELLED);
        orderRepository.save(order);

        engine.execute(order.getOrderId());

        Order updated = orderRepository.findById(order.getOrderId()).orElseThrow();

        assertEquals(OrderStatus.CANCELLED, updated.getOrderStatus());
    }
}
