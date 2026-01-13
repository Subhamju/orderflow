package com.orderflow.dto;

import com.orderflow.domain.enums.OrderKind;
import com.orderflow.domain.enums.OrderType;

public class OrderRequest {
    private Long userId;
    private Long instrumentId;
    private OrderType orderType;
    private OrderKind orderKind;
    private Double price;
    private Integer quantity;

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
}
