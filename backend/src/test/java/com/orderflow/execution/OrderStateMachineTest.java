package com.orderflow.execution;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import com.orderflow.domain.entity.Order;
import com.orderflow.domain.enums.OrderStatus;

@ActiveProfiles("test")
public class OrderStateMachineTest {

    @Test
    void shouldAllowValidTransition() {

        Order order = new Order();
        order.transitionTo(OrderStatus.CREATED);
        order.transitionTo(OrderStatus.VALIDATED);

        assertEquals(OrderStatus.VALIDATED, order.getOrderStatus());
    }

    @Test
    void shouldThrowOnInvalidTransition() {

        Order order = new Order();
        order.transitionTo(OrderStatus.CREATED);

        assertThrows(IllegalStateException.class,
                () -> order.transitionTo(OrderStatus.EXECUTED));
    }

}
