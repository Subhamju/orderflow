package com.orderflow.controller;

import com.orderflow.domain.entity.OrderEvent;
import com.orderflow.dto.OrderDetailsResponse;
import com.orderflow.dto.OrderRequest;
import com.orderflow.dto.OrderResponse;
import com.orderflow.repository.OrderEventRepository;
import com.orderflow.service.OrderService;

import lombok.extern.slf4j.Slf4j;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;
    private final OrderEventRepository orderEventRepository;

    public OrderController(OrderService orderService, OrderEventRepository orderEventRepository) {
        this.orderService = orderService;
        this.orderEventRepository = orderEventRepository;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> placeOrder(
            @RequestHeader(name = "Idempotency-Key", required = true) String idempotencyKey,
            @RequestBody OrderRequest request) {
        OrderResponse response = orderService.placeOrder(request, idempotencyKey);

        HttpStatus status = response.isDuplicate() ? HttpStatus.OK : HttpStatus.CREATED;

        return ResponseEntity.status(status).body(response);
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<OrderResponse> cancelOrder(@PathVariable Long id) {

        return ResponseEntity.ok(orderService.cancelOrder(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDetailsResponse> getOrder(
            @PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    @GetMapping(value = { "", "/" })
    public ResponseEntity<Page<OrderDetailsResponse>> getAllOrders(
            @PageableDefault(size = 10, sort = "createdAt", direction = Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(orderService.getAllOrders(pageable));
    }

    @GetMapping("/{id}/events")
    public ResponseEntity<List<OrderEvent>> getOrderEvents(@PathVariable Long id) {
        return ResponseEntity.ok(orderEventRepository.findByOrderIdOrderByCreatedAtAsc(id));
    }

}
