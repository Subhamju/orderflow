package com.orderflow.service;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import com.orderflow.domain.entity.Order;
import com.orderflow.domain.entity.OutboxEvent;
import com.orderflow.domain.enums.OutboxEventStatus;
import com.orderflow.domain.enums.OrderKind;
import com.orderflow.domain.enums.OrderType;
import com.orderflow.dto.OrderRequest;
import com.orderflow.dto.OrderResponse;
import com.orderflow.exception.InvalidOrderException;
import com.orderflow.kafka.OrderKafkaProducer;
import com.orderflow.repository.OrderRepository;
import com.orderflow.repository.OutboxEventRepository;

import jakarta.transaction.Transactional;

@SpringBootTest(properties = {
        "spring.kafka.listener.auto-startup=false"
})
@ActiveProfiles("test")
@Transactional
class OrderServiceIdempotencyTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OutboxEventRepository outboxEventRepository;

    private OrderRequest baseRequest;

    @MockBean
    private OrderKafkaProducer orderKafkaProducer;

    @BeforeEach
    void setup() {
        baseRequest = new OrderRequest();
        baseRequest.setUserId(1L);
        baseRequest.setInstrumentId(101L);
        baseRequest.setOrderType(OrderType.BUY);
        baseRequest.setOrderKind(OrderKind.LIMIT);
        baseRequest.setQuantity(10);
        baseRequest.setPrice(new BigDecimal("100.55"));
    }

    @Test
    void sameIdempotencyKeyShouldReturnSameOrder() {
        OrderResponse first = orderService.placeOrder(baseRequest, "idm-123");

        OrderResponse retry = orderService.placeOrder(baseRequest, "idm-123");

        assertEquals(first.getOrderId(), retry.getOrderId());
        assertTrue(retry.isDuplicate());
        assertEquals(1, outboxEventRepository.count());
    }

    @Test
    void acceptedOrderShouldCreatePendingExecutionOutboxEvent() {
        OrderResponse response = orderService.placeOrder(baseRequest, "idm-outbox");

        OutboxEvent outboxEvent = outboxEventRepository.findAll().getFirst();

        assertEquals(response.getOrderId(), outboxEvent.getAggregateId());
        assertEquals(OutboxEventStatus.PENDING, outboxEvent.getStatus());
    }

    @Test
    void differentIdempotencyKeyShouldCreateNewOrder() {
        OrderResponse first = orderService.placeOrder(baseRequest, "idm-123");

        OrderResponse second = orderService.placeOrder(baseRequest, "idm-456");

        assertNotEquals(first.getOrderId(), second.getOrderId());

        assertTrue(
                orderRepository
                        .findByUserIdAndIdempotencyKey(1L, "idm-123")
                        .isPresent());

        assertTrue(
                orderRepository
                        .findByUserIdAndIdempotencyKey(1L, "idm-456")
                        .isPresent());
    }

    @Test
    void marketOrderShouldPersistNullPrice() {
        baseRequest.setOrderKind(OrderKind.MARKET);
        baseRequest.setPrice(new BigDecimal("999.99")); // ignored

        OrderResponse response = orderService.placeOrder(baseRequest, "idm-market");

        Order marketOrder = orderRepository
                .findById(response.getOrderId())
                .orElseThrow();

        assertNull(marketOrder.getPrice());
    }

    @Test
    void limitOrderWithoutPriceShouldFail() {
        baseRequest.setOrderKind(OrderKind.LIMIT);
        baseRequest.setPrice(null);

        assertThrows(
                InvalidOrderException.class,
                () -> orderService.placeOrder(baseRequest, "idm-limit"));
    }
}
