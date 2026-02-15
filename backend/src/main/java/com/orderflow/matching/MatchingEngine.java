package com.orderflow.matching;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.orderflow.domain.entity.Order;
import com.orderflow.domain.entity.Trade;
import com.orderflow.domain.enums.OrderStatus;
import com.orderflow.domain.enums.OrderType;
import com.orderflow.repository.OrderRepository;
import com.orderflow.repository.TradeRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MatchingEngine {

    private final OrderRepository orderRepository;
    private final TradeRepository tradeRepository;

    @Transactional
    public void match(Order incomingOrder) {

        if (incomingOrder.getOrderType() == OrderType.BUY) {
            matchBuyOrder(incomingOrder);
        } else {
            matchSellOrder(incomingOrder);
        }

    }

    private void matchSellOrder(Order sellOrder) {

        List<Order> buyOrders = orderRepository.findMatchingBuyOrders(
                sellOrder.getInstrumentId(),
                sellOrder.getPrice());

        for (Order buy : buyOrders) {
            if (sellOrder.getRemainingQuantity() == 0)
                break;

            int tradeQty = Math.min(sellOrder.getRemainingQuantity(), buy.getRemainingQuantity());

            executeTrade(buy, sellOrder, tradeQty, buy.getPrice());
        }
        finalizeOrderStatus(sellOrder);
    }

    private void matchBuyOrder(Order buyOrder) {
        List<Order> sellOrders = orderRepository.findMatchingSellOrders(
                buyOrder.getInstrumentId(),
                buyOrder.getPrice());

        for (Order sell : sellOrders) {
            if (buyOrder.getRemainingQuantity() == 0)
                break;

            int tradeQty = Math.min(buyOrder.getRemainingQuantity(), sell.getRemainingQuantity());

            executeTrade(buyOrder, sell, tradeQty, sell.getPrice());
        }

        finalizeOrderStatus(buyOrder);
    }

    private void finalizeOrderStatus(Order order) {

        if (order.getRemainingQuantity() == 0) {
            order.transitionTo(OrderStatus.EXECUTED);
        } else {
            order.transitionTo(OrderStatus.PARTIALLY_FILLED);
        }
        orderRepository.save(order);
    }

    private void executeTrade(Order buy, Order sell, int quantity, double price) {

        buy.setRemainingQuantity(buy.getRemainingQuantity() - quantity);
        sell.setRemainingQuantity(sell.getRemainingQuantity() - quantity);

        Trade trade = new Trade();
        trade.setBuyOrderId(buy.getOrderId());
        trade.setSellOrderId(sell.getOrderId());
        trade.setQuantity(quantity);
        trade.setPrice(price);
        trade.setExecutedAt(LocalDateTime.now());

        tradeRepository.save(trade);

        orderRepository.save(buy);
        orderRepository.save(sell);
    }

}
