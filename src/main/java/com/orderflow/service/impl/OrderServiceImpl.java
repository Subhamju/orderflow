package com.orderflow.service.impl;

import com.orderflow.domain.entity.Order;
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

        Order order = new Order();
        order.setUserId(request.getUserId());
        order.setInstrumentId(request.getInstrumentId());
        order.setOrderType(request.getOrderType());
        order.setOrderKind(request.getOrderKind());
        order.setPrice(request.getPrice());
        order.setQuantity(request.getQuantity());
        order.transitionTo(OrderStatus.CREATED);
        order.setCreatedAt(LocalDateTime.now());
        orderRepository.save(order);

        try{
            validate(request);
            order.transitionTo(OrderStatus.VALIDATED);
            orderRepository.save(order);
        } catch (InvalidOrderException ex) {
            order.transitionTo(OrderStatus.REJECTED);
            orderRepository.save(order);
            throw ex;
        }

        executionEngine.execute(order);
        order.transitionTo(OrderStatus.SENT_TO_EXECUTOR);
        orderRepository.save(order);

        log.info("Order {} accepted for execution", order.getOrderId());

        return new OrderResponse(
                order.getOrderId(),
                order.getOrderStatus(),
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
