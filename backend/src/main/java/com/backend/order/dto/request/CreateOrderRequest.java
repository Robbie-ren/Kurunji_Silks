package com.backend.order.dto.request;

import com.backend.order.enums.PaymentMethod;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequest {

    @Valid
    @NotNull(message = "Address is required")
    private AddressRequest address;

    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;
}