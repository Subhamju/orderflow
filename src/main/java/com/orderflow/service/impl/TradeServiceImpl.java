package com.orderflow.service.impl;

import com.orderflow.domain.Order;
import com.orderflow.domain.Trade;
import com.orderflow.domain.enums.OrderKind;
import com.orderflow.domain.enums.OrderStatus;
import com.orderflow.repository.OrderRepository;
import com.orderflow.repository.TradeRepository;
import com.orderflow.service.TradeService;

import java.time.LocalDateTime;

public class TradeServiceImpl implements TradeService {
    private final TradeRepository tradeRepository;
    private final OrderRepository orderRepository;

    public TradeServiceImpl(TradeRepository tradeRepository, OrderRepository orderRepository) {
        this.tradeRepository = tradeRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    public void execute(Order order) {

        Trade trade = new Trade();
        trade.setBuyOrderId(order.getOrderType().name().equals("BUY") ? order.getOrderId() : null);
        trade.setSellOrderId(order.getOrderType().name().equals("SELL") ? order.getOrderId() : null);
        trade.setPrice(order.getPrice());
        trade.setQuantity(order.getQuantity());
        trade.setExecutedAt(LocalDateTime.now());

        tradeRepository.save(trade);

        order.setOrderStatus(OrderStatus.EXECUTED);
        orderRepository.save(order);
    }
}
