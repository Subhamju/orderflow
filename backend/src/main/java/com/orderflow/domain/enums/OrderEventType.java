package com.orderflow.domain.enums;

public enum OrderEventType {
    ORDER_PLACED,
    SENT_TO_EXECUTOR,
    EXECUTED,
    CANCEL_REQUESTED,
    CANCELLED,
    FAILED
}