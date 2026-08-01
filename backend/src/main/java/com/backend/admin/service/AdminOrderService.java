package com.backend.admin.service;

import com.backend.admin.dto.request.UpdateOrderStatusRequest;
import com.backend.admin.dto.response.AdminOrderResponse;

import java.util.List;

public interface AdminOrderService {

    List<AdminOrderResponse> getAllOrders();

    AdminOrderResponse getOrderById(Long orderId);

    AdminOrderResponse updateOrderStatus(
            Long orderId,
            UpdateOrderStatusRequest request
    );
}