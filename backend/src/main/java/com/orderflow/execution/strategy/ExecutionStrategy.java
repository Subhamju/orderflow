package com.orderflow.execution.strategy;

import com.orderflow.domain.entity.Order;
import com.orderflow.execution.ExecutionResult;

public interface ExecutionStrategy {
    ExecutionResult execute(Order order);
}
