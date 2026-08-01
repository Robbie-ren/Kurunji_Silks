package com.backend.admin.dto.response;

import com.backend.order.enums.OrderStatus;
import com.backend.order.enums.PaymentStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminOrderResponse {

    private Long id;
    private String orderNumber;

    private Long userId;
    private String customerName;
    private String customerEmail;

    private OrderStatus orderStatus;
    private PaymentStatus paymentStatus;

    private BigDecimal totalAmount;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}