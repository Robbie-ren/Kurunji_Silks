package com.backend.admin.dto.request;

import com.backend.order.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateOrderStatusRequest {

    @NotNull(message = "Order status is required")
    private OrderStatus status;

    private String note;
}