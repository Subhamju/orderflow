package com.orderflow.dto;

import com.orderflow.domain.enums.OrderStatus;

public class OrderResponse {
    private Long orderId;
    private OrderStatus orderStatus;
    private String message;

    public OrderResponse(Long orderId, OrderStatus orderStatus, String message) {
        this.orderId = orderId;
        this.orderStatus = orderStatus;
        this.message = message;
    }

    public Long getOrderId() {
        return orderId;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public String getMessage() {
        return message;
    }
}
