package com.orderflow.execution;

public interface OrderExecutionEngine {
    void execute(Long orderId);
}
