package com.backend.order.controller;

import com.backend.order.dto.request.CreateOrderRequest;
import com.backend.order.dto.response.OrderResponse;
import com.backend.order.service.OrderService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // CREATE ORDER FROM CART
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @Valid @RequestBody CreateOrderRequest request
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        orderService.createOrder(request)
                );
    }

    // GET MY ORDERS
    @GetMapping
    public ResponseEntity<List<OrderResponse>> getMyOrders() {

        return ResponseEntity.ok(
                orderService.getMyOrders()
        );
    }

    // GET ONE ORDER
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                orderService.getOrderById(id)
        );
    }

    // CANCEL ORDER
    @PutMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelOrder(
            @PathVariable Long id
    ) {

        orderService.cancelOrder(id);

        return ResponseEntity.noContent().build();
    }
}