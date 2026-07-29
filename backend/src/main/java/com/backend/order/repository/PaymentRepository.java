package com.backend.order.repository;

import com.backend.order.entity.Payment;
import com.backend.order.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByOrderId(Long orderId);

    Optional<Payment> findByTransactionId(String transactionId);

    boolean existsByOrderId(Long orderId);

    long countByPaymentStatus(PaymentStatus paymentStatus);
}