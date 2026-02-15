package com.orderflow.domain.enums;

public enum OrderStatus {
    CREATED,
    VALIDATED,
    SENT_TO_EXECUTOR,
    EXECUTING,
    PARTIALLY_FILLED,
    PARTIALLY_CANCELLED,
    EXECUTED,
    REJECTED,
    FAILED,
    CANCELLED
}
