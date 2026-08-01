package com.backend.order.dto.response;

import com.backend.order.enums.OrderStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderStatusHistoryResponse {

    private Long id;
    private OrderStatus status;
    private String note;
    private LocalDateTime createdAt;
}