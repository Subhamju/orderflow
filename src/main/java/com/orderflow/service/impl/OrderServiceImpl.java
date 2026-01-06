package com.orderflow.service.impl;

import com.orderflow.domain.Order;
import com.orderflow.domain.enums.OrderStatus;
import com.orderflow.dto.OrderRequest;
import com.orderflow.dto.OrderResponse;
import com.orderflow.exception.InvalidOrderException;
import com.orderflow.repository.OrderRepository;
import com.orderflow.service.OrderService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;

    public OrderServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public OrderResponse placeOrder(OrderRequest request) {
        validate(request);

        Order order = new Order();
        order.setUserId(request.getUserId());
        order.setInstrumentId(request.getInstrumentId());
        order.setOrderType(request.getOrderType());
        order.setOrderKind(request.getOrderKind());
        order.setPrice(request.getPrice());
        order.setQuantity(request.getQuantity());
        order.setOrderStatus(OrderStatus.CREATED);
        order.setCreatedAt(LocalDateTime.now());

        Order saveOrder = orderRepository.save(order);

        return new OrderResponse(
                saveOrder.getOrderId(),
                saveOrder.getOrderStatus(),
                "Order Placed Successfully"
        );
    }

    @Override
    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(()->new InvalidOrderException("Order not found"));
    }
    public void validate(OrderRequest request){
        if(request.getQuantity() == null || request.getQuantity() <= 0){
            throw new InvalidOrderException("Quantity must be positive");
        }
        if(request.getOrderKind().name().equals("LIMIT") &&
                (request.getPrice() == null || request.getPrice() <= 0)){
            throw new InvalidOrderException("Price required for LIMIT order");
        }
    }
}
