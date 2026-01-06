package com.orderflow.execution.strategy;

import com.orderflow.domain.enums.OrderKind;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ExecutionStrategyFactory {
    private final Map<OrderKind,ExecutionStrategy> strategies;

    public ExecutionStrategyFactory(ExecutionStrategy marketOrderExecutionStrategy ,ExecutionStrategy limitOrderExecutionStrategy) {
        this.strategies = Map.of(
                OrderKind.MARKET,marketOrderExecutionStrategy,
                OrderKind.LIMIT,limitOrderExecutionStrategy
        );
    }
    public ExecutionStrategy getStrategy(OrderKind kind){
        return strategies.get(kind);
    }
}
