package com.orderflow.controller;

import com.orderflow.dto.OrderDetailsResponse;
import com.orderflow.dto.OrderRequest;
import com.orderflow.dto.OrderResponse;
import com.orderflow.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;


    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }
    @PostMapping
    public ResponseEntity<OrderResponse> placeOrder(@RequestBody OrderRequest request){
        return ResponseEntity.ok(orderService.placeOrder(request));
    }
    @GetMapping("/{id}")
    public ResponseEntity<OrderDetailsResponse> getOrder(@PathVariable Long id){
        return ResponseEntity.ok(orderService.getOrderById(id));
    }
}
