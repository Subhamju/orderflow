package com.orderflow.execution;

import com.orderflow.domain.entity.Order;

public interface OrderExecutionEngine {
    void execute(Order order);
}
