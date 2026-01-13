package com.orderflow.service;

import com.orderflow.dto.OrderDetailsResponse;
import com.orderflow.dto.OrderRequest;
import com.orderflow.dto.OrderResponse;

public interface OrderService {
    OrderResponse placeOrder(OrderRequest request);
    OrderDetailsResponse getOrderById(Long orderId);
}
