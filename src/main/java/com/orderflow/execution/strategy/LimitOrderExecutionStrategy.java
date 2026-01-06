package com.orderflow.execution.strategy;

import com.orderflow.domain.Order;
import com.orderflow.service.TradeService;
import org.springframework.stereotype.Component;

@Component
public class LimitOrderExecutionStrategy implements ExecutionStrategy{
    private final TradeService tradeService;

    public LimitOrderExecutionStrategy(TradeService tradeService) {
        this.tradeService = tradeService;
    }

    @Override
    public void execute(Order order) {
        //For now Treat Market as Limit
        tradeService.execute(order);

    }
}
