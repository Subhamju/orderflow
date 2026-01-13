package com.orderflow.service.impl;

import com.orderflow.domain.entity.Order;
import com.orderflow.domain.enums.OrderKind;
import com.orderflow.domain.enums.OrderStatus;
import com.orderflow.dto.OrderDetailsResponse;
import com.orderflow.dto.OrderRequest;
import com.orderflow.dto.OrderResponse;
import com.orderflow.exception.ErrorCode;
import com.orderflow.exception.InvalidOrderException;
import com.orderflow.execution.OrderExecutionEngine;
import com.orderflow.repository.OrderRepository;
import com.orderflow.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderExecutionEngine executionEngine;

    public OrderServiceImpl(OrderRepository orderRepository, OrderExecutionEngine executionEngine) {
        this.orderRepository = orderRepository;
        this.executionEngine = executionEngine;
    }
    @Override
    public OrderResponse placeOrder(OrderRequest request) {

        validate(request);

        Order order = new Order();
        order.setUserId(request.getUserId());
        order.setInstrumentId(request.getInstrumentId());
        order.setOrderType(request.getOrderType());
        order.setOrderKind(request.getOrderKind());
        order.setQuantity(request.getQuantity());
        order.setCreatedAt(LocalDateTime.now());
        if (request.getOrderKind() == OrderKind.LIMIT) {
            order.setPrice(request.getPrice());
        } else {
            // MARKET order: price is not applicable
            order.setPrice(null);
        }

        order.transitionTo(OrderStatus.CREATED);
        orderRepository.save(order);

        order.transitionTo(OrderStatus.VALIDATED);
        orderRepository.save(order);

        order.transitionTo(OrderStatus.SENT_TO_EXECUTOR);
        orderRepository.save(order);

        OrderStatus ackStatus = order.getOrderStatus();

        executionEngine.execute(order);

        log.info("Order {} accepted for execution", order.getOrderId());

        return new OrderResponse(
                order.getOrderId(),
                ackStatus,
                "Order Accepted"
        );
    }

    @Override
    public OrderDetailsResponse getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(()->new InvalidOrderException(
                        ErrorCode.ORDER_NOT_FOUND,
                        "Order not found"));
        return mapToDetailsResponse(order);
    }

    private OrderDetailsResponse mapToDetailsResponse(Order order) {
        return new OrderDetailsResponse(
                order.getOrderId(),
                order.getOrderStatus(),
                order.getOrderType(),
                order.getOrderKind(),
                order.getPrice(),
                order.getQuantity(),
                order.getCreatedAt()
        );
    }

    private void validate(OrderRequest request){
        if(request.getQuantity() == null || request.getQuantity() <= 0){
            throw new InvalidOrderException(
                    ErrorCode.INVALID_ORDER,
                    "Quantity must be positive");
        }
        if(request.getOrderKind().name().equals("LIMIT") &&
                (request.getPrice() == null || request.getPrice() <= 0)){
            throw new InvalidOrderException(
                    ErrorCode.INVALID_ORDER,
                    "Price required for LIMIT order");
        }
    }
}
