package com.backend.order.repository;

import com.backend.order.entity.Order;
import com.backend.order.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByOrderNumber(String orderNumber);

    Page<Order> findByUserId(
            Long userId,
            Pageable pageable
    );

    Page<Order> findByUserIdAndOrderStatus(
            Long userId,
            OrderStatus orderStatus,
            Pageable pageable
    );

    Page<Order> findByOrderStatus(
            OrderStatus orderStatus,
            Pageable pageable
    );

    boolean existsByOrderNumber(String orderNumber);
}