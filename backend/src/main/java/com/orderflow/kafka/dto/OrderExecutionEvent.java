package com.orderflow.kafka.dto;

import java.io.Serializable;

public record OrderExecutionEvent(Long orderId) implements Serializable {

    public OrderExecutionEvent {
        if (orderId == null) {
            throw new IllegalArgumentException("orderId cannot be null");
        }
    }

}
