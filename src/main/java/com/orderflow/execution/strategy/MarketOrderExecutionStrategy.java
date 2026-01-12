package com.orderflow.execution.strategy;

import com.orderflow.domain.entity.Order;
import com.orderflow.execution.ExecutionResult;
import com.orderflow.service.TradeService;
import org.springframework.stereotype.Component;

@Component
public class MarketOrderExecutionStrategy implements ExecutionStrategy{
    private final TradeService tradeService;

    public MarketOrderExecutionStrategy(TradeService tradeService) {
        this.tradeService = tradeService;
    }

    @Override
    public ExecutionResult execute(Order order) {
        try{
            tradeService.execute(order);
            return ExecutionResult.SUCCESS;
        } catch (Exception e) {
           return ExecutionResult.FAILURE;
        }

    }
}
