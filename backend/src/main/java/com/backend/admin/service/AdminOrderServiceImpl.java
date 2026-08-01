package com.backend.admin.service;

import com.backend.admin.dto.request.UpdateOrderStatusRequest;
import com.backend.admin.dto.response.AdminOrderResponse;
import com.backend.admin.service.AdminOrderService;
import com.backend.auth.entity.User;
import com.backend.auth.repository.UserRepository;
import com.backend.order.entity.Order;
import com.backend.order.entity.OrderStatusHistory;
import com.backend.order.enums.OrderStatus;
import com.backend.order.exception.OrderNotFoundException;
import com.backend.order.repository.OrderRepository;
import com.backend.order.repository.OrderStatusHistoryRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminOrderServiceImpl implements AdminOrderService {

    private final OrderRepository orderRepository;
    private final OrderStatusHistoryRepository historyRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<AdminOrderResponse> getAllOrders() {

        return orderRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public AdminOrderResponse getOrderById(Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new OrderNotFoundException(
                                "Order not found with id: " + orderId
                        )
                );

        return mapToResponse(order);
    }

    @Override
    public AdminOrderResponse updateOrderStatus(
            Long orderId,
            UpdateOrderStatusRequest request
    ) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new OrderNotFoundException(
                                "Order not found with id: " + orderId
                        )
                );

        OrderStatus newStatus = request.getStatus();

        order.setOrderStatus(newStatus);

        orderRepository.save(order);

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        User updatedBy = userRepository.findByEmailIgnoreCase(email)
                .orElse(null);

        OrderStatusHistory history = OrderStatusHistory.builder()
                .order(order)
                .status(newStatus)
                .note(request.getNote())
                .updatedBy(updatedBy)
                .build();

        historyRepository.save(history);

        return mapToResponse(order);
    }

    private AdminOrderResponse mapToResponse(Order order) {

        User user = order.getUser();

        return AdminOrderResponse.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .userId(user.getId())
                .customerName(user.getName())
                .customerEmail(user.getEmail())
                .orderStatus(order.getOrderStatus())
                .paymentStatus(order.getPaymentStatus())
                .totalAmount(order.getTotalAmount())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }
}