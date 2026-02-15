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
import com.orderflow.matching.MatchingEngine;
import com.orderflow.repository.OrderRepository;

@SpringBootTest(properties = {
        "spring.kafka.listener.auto-startup=false"
})
@ActiveProfiles("test")
@Transactional
class MatchingEngineTest {

    @Autowired
    private MatchingEngine matchingEngine;

    @Autowired
    private OrderRepository orderRepository;

    @Test
    void shouldFullyMatchBuyAndSellOrders() {

        Order buy = OrderTestFactory.limitBuy(100, 40.0);
        Order sell = OrderTestFactory.limitSell(100, 40.0);

        buy.setOrderStatus(OrderStatus.EXECUTING);
        sell.setOrderStatus(OrderStatus.EXECUTING);

        orderRepository.save(buy);
        orderRepository.save(sell);

        matchingEngine.match(buy);

        Order updatedBuy = orderRepository.findById(buy.getOrderId()).orElseThrow();
        Order updatedSell = orderRepository.findById(sell.getOrderId()).orElseThrow();

        assertEquals(OrderStatus.EXECUTED, updatedBuy.getOrderStatus());
        assertEquals(OrderStatus.EXECUTED, updatedSell.getOrderStatus());
        assertEquals(0, updatedBuy.getRemainingQuantity());
        assertEquals(0, updatedSell.getRemainingQuantity());
    }

    @Test
    void shouldPartiallyFillOrder() {

        Order buy = OrderTestFactory.limitBuy(100, 40.0);
        Order sell = OrderTestFactory.limitSell(50, 40.0);

        buy.setOrderStatus(OrderStatus.EXECUTING);
        sell.setOrderStatus(OrderStatus.EXECUTING);

        orderRepository.save(buy);
        orderRepository.save(sell);

        matchingEngine.match(buy);

        Order updatedBuy = orderRepository.findById(buy.getOrderId()).orElseThrow();
        Order updatedSell = orderRepository.findById(sell.getOrderId()).orElseThrow();

        assertEquals(OrderStatus.PARTIALLY_FILLED, updatedBuy.getOrderStatus());
        assertEquals(OrderStatus.EXECUTED, updatedSell.getOrderStatus());

        assertEquals(50, updatedBuy.getRemainingQuantity());
        assertEquals(0, updatedSell.getRemainingQuantity());
    }

}
