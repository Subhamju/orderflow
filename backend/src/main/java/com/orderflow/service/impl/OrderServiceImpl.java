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

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
    public OrderResponse placeOrder(OrderRequest request, String idempotencyKey) {

        validate(request);

        Optional<Order> existing = orderRepository.findByUserIdAndIdempotencyKey(
                request.getUserId(), idempotencyKey);

        if (existing.isPresent()) {
            Order order = existing.get();
            return new OrderResponse(order.getOrderId(),
                    order.getOrderStatus(),
                    "Order already exists",
                    true);
        }

        Order order = new Order();
        order.setUserId(request.getUserId());
        order.setInstrumentId(request.getInstrumentId());
        order.setOrderType(request.getOrderType());
        order.setOrderKind(request.getOrderKind());
        order.setQuantity(request.getQuantity());
        order.setCreatedAt(LocalDateTime.now());
        order.setIdempotencyKey(idempotencyKey);
        if (request.getOrderKind() == OrderKind.LIMIT) {
            order.setPrice(request.getPrice());
        } else {
            // MARKET order: price is not applicable
            order.setPrice(null);
        }
        try {
            order.transitionTo(OrderStatus.CREATED);
            orderRepository.save(order);

        } catch (DataIntegrityViolationException ex) {
            Order winner = orderRepository.findByUserIdAndIdempotencyKey(request.getUserId(),
                    idempotencyKey).orElseThrow(() -> ex);

            return new OrderResponse(winner.getOrderId(),
                    winner.getOrderStatus(),
                    "Order already exists",
                    true);
        }

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
                "Order Accepted",
                false);
    }

    @Override
    public OrderDetailsResponse getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new InvalidOrderException(
                        ErrorCode.ORDER_NOT_FOUND,
                        "Order not found"));
        return mapToOrderDetailsResponse(order);
    }

    @Override
    public List<OrderDetailsResponse> getAllOrders() {
        return orderRepository.findAll()
                .stream()
                .map(this::mapToOrderDetailsResponse)
                .toList();
    }

    @Override
    public Page<OrderDetailsResponse> getAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable)
                .map(this::mapToOrderDetailsResponse);
    }

    private OrderDetailsResponse mapToOrderDetailsResponse(Order order) {
        return new OrderDetailsResponse(
                order.getOrderId(),
                order.getOrderStatus(),
                order.getOrderType(),
                order.getOrderKind(),
                order.getPrice(),
                order.getQuantity(),
                order.getCreatedAt());
    }

    private void validate(OrderRequest request) {
        if (request.getQuantity() == null || request.getQuantity() <= 0) {
            throw new InvalidOrderException(
                    ErrorCode.INVALID_ORDER,
                    "Quantity must be positive");
        }
        if (request.getOrderKind().name().equals("LIMIT") &&
                (request.getPrice() == null || request.getPrice() <= 0)) {
            throw new InvalidOrderException(
                    ErrorCode.INVALID_ORDER,
                    "Price required for LIMIT order");
        }
    }
}
