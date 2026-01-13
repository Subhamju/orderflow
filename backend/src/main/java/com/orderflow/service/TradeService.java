package com.orderflow.service;

import com.orderflow.domain.entity.Order;

public interface TradeService {
    void execute(Order order);
}
