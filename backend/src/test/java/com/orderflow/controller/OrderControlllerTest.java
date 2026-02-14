package com.orderflow.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.orderflow.domain.enums.OrderStatus;
import com.orderflow.dto.OrderResponse;
import com.orderflow.repository.OrderEventRepository;
import com.orderflow.service.OrderService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
class OrderControlllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private OrderService orderService;
        @MockBean
        private OrderEventRepository orderEventRepository;

        @Test
        void shouldReturn201ForFirstOrder() throws Exception {

                OrderResponse response = new OrderResponse(1L, OrderStatus.SENT_TO_EXECUTOR,
                                "Order Accepted", false);

                when(orderService.placeOrder(any(), anyString()))
                                .thenReturn(response);

                mockMvc.perform(post("/api/v1/orders")
                                .header("Idempotency-Key", "Key-123")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{}"))
                                .andExpect(status().isCreated());
        }

        @Test
        void shouldReturn200ForDuplicateOrder() throws Exception {

                OrderResponse response = new OrderResponse(1L, OrderStatus.SENT_TO_EXECUTOR,
                                "Order already exists", true);

                when(orderService.placeOrder(any(), anyString()))
                                .thenReturn(response);

                mockMvc.perform(post("/api/v1/orders")
                                .header("Idempotency-Key", "key-123")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{}"))
                                .andExpect(status().isOk());
        }

}
