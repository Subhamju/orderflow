package com.orderflow;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.test.context.ActiveProfiles;

import com.orderflow.domain.entity.Order;
import com.orderflow.domain.enums.OrderKind;
import com.orderflow.domain.enums.OrderStatus;
import com.orderflow.domain.enums.OrderType;

@ActiveProfiles("test")

public class OrderTestFactory {

    public static Order limitBuy(int qty, double price) {
        Order o = baseOrder(qty);
        o.setOrderType(OrderType.BUY);
        o.setOrderKind(OrderKind.LIMIT);
        o.setPrice(price);
        o.transitionTo(OrderStatus.SENT_TO_EXECUTOR);
        return o;
    }

    public static Order limitSell(int qty, double price) {
        Order o = baseOrder(qty);
        o.setOrderType(OrderType.SELL);
        o.setOrderKind(OrderKind.LIMIT);
        o.setPrice(price);
        o.transitionTo(OrderStatus.SENT_TO_EXECUTOR);
        return o;
    }

    private static Order baseOrder(int qty) {
        Order o = new Order();
        o.setUserId(1L);
        o.setInstrumentId(1L);
        o.setQuantity(qty);
        o.setRemainingQuantity(qty);
        o.setCreatedAt(LocalDateTime.now());
        o.setIdempotencyKey(UUID.randomUUID().toString());
        o.setOrderStatus(OrderStatus.VALIDATED);
        return o;
    }
}
