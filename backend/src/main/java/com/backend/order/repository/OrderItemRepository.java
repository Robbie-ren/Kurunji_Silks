package com.backend.order.repository;

import com.backend.order.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    List<OrderItem> findByOrderId(Long orderId);

    @Query("""
        SELECT oi.product.id, oi.productName, SUM(oi.quantity), COALESCE(SUM(oi.subtotal), 0)
        FROM OrderItem oi
        GROUP BY oi.product.id, oi.productName
        ORDER BY SUM(oi.quantity) DESC
        """)
    List<Object[]> getProductSalesSummary();
}