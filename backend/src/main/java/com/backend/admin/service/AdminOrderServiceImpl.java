package com.backend.admin.service;

import com.backend.admin.dto.request.UpdateOrderStatusRequest;
import com.backend.admin.dto.response.AdminOrderResponse;
import com.backend.auth.entity.User;
import com.backend.auth.repository.UserRepository;
import com.backend.order.entity.Order;
import com.backend.order.entity.OrderStatusHistory;
import com.backend.order.enums.OrderStatus;
import com.backend.order.exception.InvalidOrderStatusException;
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
        return orderRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public AdminOrderResponse getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new OrderNotFoundException("Order not found with id: " + orderId)
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
                        new OrderNotFoundException("Order not found with id: " + orderId)
                );

        OrderStatus currentStatus = order.getOrderStatus();
        OrderStatus newStatus = request.getStatus();

        validateStatusChange(currentStatus, newStatus);

        order.setOrderStatus(newStatus);
        order = orderRepository.save(order);

        User updatedBy = getCurrentAdmin();

        OrderStatusHistory history = OrderStatusHistory.builder()
                .order(order)
                .status(newStatus)
                .note(request.getNote())
                .updatedBy(updatedBy)
                .build();

        historyRepository.save(history);

        return mapToResponse(order);
    }

    private void validateStatusChange(
            OrderStatus currentStatus,
            OrderStatus newStatus
    ) {
        if (currentStatus == newStatus) {
            throw new InvalidOrderStatusException(
                    "Order is already in status: " + newStatus
            );
        }

        if (currentStatus == OrderStatus.CANCELLED) {
            throw new InvalidOrderStatusException(
                    "Cancelled order status cannot be changed."
            );
        }

        if (currentStatus == OrderStatus.DELIVERED) {
            throw new InvalidOrderStatusException(
                    "Delivered order status cannot be changed."
            );
        }

        boolean validTransition =
                (currentStatus == OrderStatus.PLACED && newStatus == OrderStatus.CONFIRMED)
                        || (currentStatus == OrderStatus.CONFIRMED && newStatus == OrderStatus.SHIPPED)
                        || (currentStatus == OrderStatus.SHIPPED && newStatus == OrderStatus.DELIVERED)
                        || (newStatus == OrderStatus.CANCELLED
                        && (currentStatus == OrderStatus.PLACED || currentStatus == OrderStatus.CONFIRMED));

        if (!validTransition) {
            throw new InvalidOrderStatusException(
                    "Invalid order status change from "
                            + currentStatus
                            + " to "
                            + newStatus
            );
        }
    }

    private User getCurrentAdmin() {
        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        return userRepository.findByEmailIgnoreCase(email)
                .orElse(null);
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