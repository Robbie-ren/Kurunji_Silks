package com.backend.order.service;

import com.backend.order.dto.request.CreateOrderRequest;
import com.backend.order.dto.response.OrderResponse;

import java.util.List;

public interface OrderService {

    OrderResponse createOrder(CreateOrderRequest request);

    OrderResponse getOrderById(Long id);

    List<OrderResponse> getMyOrders();

    void cancelOrder(Long id);
}