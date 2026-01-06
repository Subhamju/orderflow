package com.orderflow.execution.strategy;

import com.orderflow.domain.Order;

public interface ExecutionStrategy {
    void execute(Order order);
}
