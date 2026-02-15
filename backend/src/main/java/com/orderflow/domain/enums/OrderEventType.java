package com.orderflow.domain.enums;

public enum OrderEventType {
    ORDER_PLACED,
    SENT_TO_EXECUTOR,
    EXECUTING,
    PARTIALLY_FILLED,
    EXECUTED,
    CANCEL_REQUESTED,
    CANCELLED,
    FAILED
}