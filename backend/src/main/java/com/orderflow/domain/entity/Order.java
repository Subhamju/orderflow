package com.orderflow.domain.entity;

import com.orderflow.domain.enums.OrderKind;
import com.orderflow.domain.enums.OrderStatus;
import com.orderflow.domain.enums.OrderType;
import com.orderflow.domain.statemachine.OrderStateMachine;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "orders", uniqueConstraints = @UniqueConstraint(name = "uk_orders_user_idempotency", columnNames = {
        "user_id", "idempotency_key" }))
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;
    @Column(name = "user_id", nullable = false)
    private Long userId;
    @Column(name = "instrument_id", nullable = false)
    private Long instrumentId;
    @Enumerated(EnumType.STRING)
    private OrderType orderType;
    @Enumerated(EnumType.STRING)
    private OrderKind orderKind;
    private Double price;
    private Integer quantity;
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    @Column(name = "idempotency_key", length = 100, nullable = false)
    private String idempotencyKey;

    @Version
    private Long version;

    private Integer remainingQuantity;

    public Integer getRemainingQuantity() {
        return remainingQuantity;
    }

    public void setRemainingQuantity(Integer remainingQuantity) {
        this.remainingQuantity = remainingQuantity;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public void setIdempotencyKey(String idempotencyKey) {
        this.idempotencyKey = idempotencyKey;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getInstrumentId() {
        return instrumentId;
    }

    public void setInstrumentId(Long instrumentId) {
        this.instrumentId = instrumentId;
    }

    public OrderType getOrderType() {
        return orderType;
    }

    public void setOrderType(OrderType orderType) {
        this.orderType = orderType;
    }

    public OrderKind getOrderKind() {
        return orderKind;
    }

    public void setOrderKind(OrderKind orderKind) {
        this.orderKind = orderKind;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void transitionTo(OrderStatus newStatus) {
        OrderStateMachine.validateState(this.orderStatus, newStatus);
        this.orderStatus = newStatus;
    }
}
