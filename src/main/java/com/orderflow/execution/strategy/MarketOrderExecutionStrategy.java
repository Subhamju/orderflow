package com.orderflow.execution.strategy;

import com.orderflow.domain.Order;
import com.orderflow.service.TradeService;
import org.springframework.stereotype.Component;

@Component
public class MarketOrderExecutionStrategy implements ExecutionStrategy{
    private final TradeService tradeService;

    public MarketOrderExecutionStrategy(TradeService tradeService) {
        this.tradeService = tradeService;
    }

    @Override
    public void execute(Order order) {
      tradeService.execute(order);
    }
}
