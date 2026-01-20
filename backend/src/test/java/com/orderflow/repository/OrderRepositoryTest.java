package com.orderflow.repository;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import com.orderflow.domain.entity.Order;
import com.orderflow.domain.enums.OrderKind;
import com.orderflow.domain.enums.OrderStatus;
import com.orderflow.domain.enums.OrderType;

@DataJpaTest
class OrderRepositoryTest {
    @Autowired
    private OrderRepository orderRepository;

    @BeforeEach
    void cleanDb() {
        orderRepository.deleteAll();
    }

    @Test
    public void shouldEnforceUniqueConstraintOnUserAndIdempotencyKey(){
        Order first = new Order();
        first.setUserId(1L);
        first.setInstrumentId(101L);
        first.setOrderType(OrderType.BUY);
        first.setOrderKind(OrderKind.LIMIT);
        first.setQuantity(120);
        first.setPrice(33.4);
        first.setIdempotencyKey("key-123");
        first.setOrderStatus(OrderStatus.CREATED);
        first.setCreatedAt(LocalDateTime.now());

        orderRepository.save(first);

        Order duplicate = new Order();
        duplicate.setUserId(1L);
        duplicate.setInstrumentId(101L);
        duplicate.setOrderType(OrderType.BUY);
        duplicate.setOrderKind(OrderKind.LIMIT);
        duplicate.setQuantity(120);
        duplicate.setPrice(33.4);
        duplicate.setIdempotencyKey("key-123");
        duplicate.setOrderStatus(OrderStatus.CREATED);
        duplicate.setCreatedAt(LocalDateTime.now());

        assertThrows(DataIntegrityViolationException.class, 
            () -> orderRepository.saveAndFlush(duplicate));

    }
    
}
