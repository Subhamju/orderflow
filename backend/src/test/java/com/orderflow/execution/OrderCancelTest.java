package com.orderflow.execution;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.orderflow.OrderTestFactory;
import com.orderflow.domain.entity.Order;
import com.orderflow.domain.enums.OrderStatus;
import com.orderflow.repository.OrderRepository;
import com.orderflow.service.OrderService;

@SpringBootTest(properties = {
        "spring.kafka.listener.auto-startup=false"
})
@ActiveProfiles("test")
public class OrderCancelTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    OrderService orderService;

    @Test
    void shouldCancelUnfilledOrder() {

        Order order = OrderTestFactory.limitBuy(100, 40.0);
        orderRepository.save(order);

        orderService.cancelOrder(order.getOrderId());

        Order updated = orderRepository.findById(order.getOrderId()).orElseThrow();

        assertEquals(OrderStatus.CANCELLED, updated.getOrderStatus());
    }

    @Test
    void shouldMarkPartiallyCancelledWhenPartiallyFilled() {

        Order order = OrderTestFactory.limitBuy(100, 40.0);
        order.transitionTo(OrderStatus.EXECUTING);
        orderRepository.save(order);

        order.setRemainingQuantity(50);
        order.transitionTo(OrderStatus.PARTIALLY_FILLED);

        orderRepository.save(order);

        orderService.cancelOrder(order.getOrderId());

        Order updated = orderRepository.findById(order.getOrderId()).orElseThrow();

        assertEquals(OrderStatus.PARTIALLY_CANCELLED, updated.getOrderStatus());
    }

}
