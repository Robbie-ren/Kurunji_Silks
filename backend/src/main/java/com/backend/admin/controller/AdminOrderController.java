package com.backend.admin.controller;

import com.backend.admin.dto.request.UpdateOrderStatusRequest;
import com.backend.admin.dto.response.AdminOrderResponse;
import com.backend.admin.service.AdminOrderService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/orders")
@RequiredArgsConstructor
public class AdminOrderController {

    private final AdminOrderService adminOrderService;

    @GetMapping
    public ResponseEntity<List<AdminOrderResponse>> getAllOrders() {
        return ResponseEntity.ok(
                adminOrderService.getAllOrders()
        );
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<AdminOrderResponse> getOrderById(
            @PathVariable Long orderId
    ) {
        return ResponseEntity.ok(
                adminOrderService.getOrderById(orderId)
        );
    }

    @PutMapping("/{orderId}/status")
    public ResponseEntity<AdminOrderResponse> updateOrderStatus(
            @PathVariable Long orderId,
            @Valid @RequestBody UpdateOrderStatusRequest request
    ) {
        return ResponseEntity.ok(
                adminOrderService.updateOrderStatus(orderId, request)
        );
    }
}