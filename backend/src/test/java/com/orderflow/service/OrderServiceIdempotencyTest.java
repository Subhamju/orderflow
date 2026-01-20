package com.orderflow.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.orderflow.domain.enums.OrderKind;
import com.orderflow.domain.enums.OrderType;
import com.orderflow.dto.OrderRequest;
import com.orderflow.dto.OrderResponse;

import jakarta.transaction.Transactional;

@SpringBootTest
@ActiveProfiles("test")
class OrderServiceIdempotencyTest {

    @Autowired
    private OrderService orderService ;

    @Test
    void sameIdempotencyKeyShouldReturnSameOrder(){
    
    OrderRequest request = new OrderRequest();
    request.setUserId(1L);
    request.setInstrumentId(101L);
    request.setOrderType(OrderType.BUY);
    request.setOrderKind(OrderKind.MARKET);
    request.setQuantity(10);

    OrderResponse first = orderService.placeOrder(request, "idm-123");
    OrderResponse retry = orderService.placeOrder(request, "idm-123");

    assertEquals(first.getOrderId(), retry.getOrderId());
    assertTrue(retry.isDuplicate());

    }

    
    
}
