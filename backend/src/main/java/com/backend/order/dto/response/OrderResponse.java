package com.backend.order.dto.response;

import com.backend.order.enums.OrderStatus;
import com.backend.order.enums.PaymentMethod;
import com.backend.order.enums.PaymentStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse {

    private Long id;
    private String orderNumber;

    private OrderStatus orderStatus;
    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;

    private BigDecimal totalAmount;

    private AddressResponse address;

    private List<OrderItemResponse> items;

    private PaymentResponse payment;

    private List<OrderStatusHistoryResponse> statusHistory;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}