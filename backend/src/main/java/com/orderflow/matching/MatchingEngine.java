package com.orderflow.matching;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.orderflow.domain.entity.Order;
import com.orderflow.domain.entity.Trade;
import com.orderflow.domain.enums.OrderKind;
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

        List<Order> bookOrders = findMatchingOrders(incomingOrder);

        for (Order counterOrder : bookOrders) {

            if (incomingOrder.getRemainingQuantity() == 0) {
                break;
            }

            if (!isPriceMatch(incomingOrder, counterOrder)) {
                break;
            }

            int tradeQty = Math.min(
                    incomingOrder.getRemainingQuantity(),
                    counterOrder.getRemainingQuantity());

            double executionPrice = determineExecutionPrice(
                    incomingOrder,
                    counterOrder);

            executeTrade(incomingOrder, counterOrder, tradeQty, executionPrice);
        }

        finalizeIncomingOrder(incomingOrder);
    }

    private List<Order> findMatchingOrders(Order incoming) {

        if (incoming.getOrderType() == OrderType.BUY) {
            return orderRepository.findBestSellOrders(
                    incoming.getInstrumentId());
        } else {
            return orderRepository.findBestBuyOrders(
                    incoming.getInstrumentId());
        }
    }

    private boolean isPriceMatch(Order incoming, Order counter) {

        // Prevent MARKET vs MARKET
        if (incoming.getOrderKind() == OrderKind.MARKET &&
                counter.getOrderKind() == OrderKind.MARKET) {
            return false;
        }

        // MARKET always matches LIMIT
        if (incoming.getOrderKind() == OrderKind.MARKET)
            return true;

        if (counter.getOrderKind() == OrderKind.MARKET)
            return true;

        // LIMIT vs LIMIT
        if (incoming.getOrderType() == OrderType.BUY) {
            return incoming.getPrice() >= counter.getPrice();
        } else {
            return incoming.getPrice() <= counter.getPrice();
        }
    }

    private double determineExecutionPrice(Order incoming, Order counter) {

        if (incoming.getOrderKind() == OrderKind.MARKET)
            return counter.getPrice();

        if (counter.getOrderKind() == OrderKind.MARKET)
            return incoming.getPrice();

        // Limit vs Limit → price-time priority
        return counter.getPrice();
    }

    private void executeTrade(Order incoming,
            Order counter,
            int quantity,
            double price) {

        incoming.setRemainingQuantity(
                incoming.getRemainingQuantity() - quantity);

        counter.setRemainingQuantity(
                counter.getRemainingQuantity() - quantity);

        updateOrderStatus(counter);

        Trade trade = new Trade();
        trade.setBuyOrderId(
                incoming.getOrderType() == OrderType.BUY
                        ? incoming.getOrderId()
                        : counter.getOrderId());

        trade.setSellOrderId(
                incoming.getOrderType() == OrderType.SELL
                        ? incoming.getOrderId()
                        : counter.getOrderId());

        trade.setQuantity(quantity);
        trade.setPrice(price);
        trade.setExecutedAt(LocalDateTime.now());

        tradeRepository.save(trade);

        orderRepository.save(counter);
    }

    private void updateOrderStatus(Order order) {

        if (order.getRemainingQuantity() == 0 &&
                order.getOrderStatus() != OrderStatus.EXECUTED) {

            order.transitionTo(OrderStatus.EXECUTED);

        } else if (order.getRemainingQuantity() > 0 &&
                order.getOrderStatus() != OrderStatus.PARTIALLY_FILLED) {

            order.transitionTo(OrderStatus.PARTIALLY_FILLED);
        }
    }

    private void finalizeIncomingOrder(Order order) {

        if (order.getRemainingQuantity() == 0) {

            order.transitionTo(OrderStatus.EXECUTED);

        } else {

            if (order.getOrderKind() == OrderKind.MARKET) {
                order.transitionTo(OrderStatus.FAILED);
            } else {
                order.transitionTo(OrderStatus.PARTIALLY_FILLED);
            }
        }

        orderRepository.save(order);
    }
}
