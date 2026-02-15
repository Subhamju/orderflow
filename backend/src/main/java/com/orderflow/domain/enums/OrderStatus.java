package com.orderflow.domain.enums;

public enum OrderStatus {
    CREATED,
    VALIDATED,
    SENT_TO_EXECUTOR,
    EXECUTING,
    PARTIALLY_FILLED,
    EXECUTED,
    REJECTED,
    FAILED,
    CANCELLED
}
