package com.orderflow.dto;

import com.orderflow.domain.enums.OrderKind;
import com.orderflow.domain.enums.OrderStatus;
import com.orderflow.domain.enums.OrderType;

import java.time.LocalDateTime;

public class OrderDetailsResponse {
    private final Long orderId;
    private final OrderStatus orderStatus;
    private final OrderType orderType;
    private final OrderKind orderKind;
    private final Double price;
    private final Integer quantity;
    private final LocalDateTime createdAt;

    public OrderDetailsResponse(Long orderId, OrderStatus orderStatus, OrderType orderType, OrderKind orderKind, Double price, Integer quantity, LocalDateTime createdAt) {
        this.orderId = orderId;
        this.orderStatus = orderStatus;
        this.orderType = orderType;
        this.orderKind = orderKind;
        this.price = price;
        this.quantity = quantity;
        this.createdAt = createdAt;
    }

    public Long getOrderId() {
        return orderId;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public OrderType getOrderType() {
        return orderType;
    }

    public OrderKind getOrderKind() {
        return orderKind;
    }

    public Double getPrice() {
        return price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
