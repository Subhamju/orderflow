package com.orderflow.domain.entity;

import java.time.LocalDateTime;

import com.orderflow.domain.enums.OrderEventType;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "order_events")
public class OrderEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long orderId;

    @Enumerated(EnumType.STRING)
    private OrderEventType eventType;

    private LocalDateTime createdAt;

    public OrderEvent() {
    }

    public OrderEvent(Long orderId, OrderEventType eventType) {
        this.orderId = orderId;
        this.eventType = eventType;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public OrderEventType getEventType() {
        return eventType;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

}
