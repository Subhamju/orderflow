package com.orderflow.service;

import com.orderflow.domain.Order;

public interface TradeService {
    void execute(Order order);
}
