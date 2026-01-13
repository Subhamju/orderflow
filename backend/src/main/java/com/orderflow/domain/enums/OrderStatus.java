package com.orderflow.domain.enums;

public enum OrderStatus {
    CREATED,
    VALIDATED,
    SENT_TO_EXECUTOR,
    EXECUTING,
    EXECUTED,
    REJECTED,
    FAILED,
    CANCELLED
}
