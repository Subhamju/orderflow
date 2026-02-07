package com.orderflow.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.orderflow.dto.OrderDetailsResponse;
import com.orderflow.dto.OrderRequest;
import com.orderflow.dto.OrderResponse;

public interface OrderService {
    OrderResponse placeOrder(OrderRequest request, String idempotencyKey);

    OrderDetailsResponse getOrderById(Long orderId);

    List<OrderDetailsResponse> getAllOrders();

    Page<OrderDetailsResponse> getAllOrders(Pageable pageable);
}
