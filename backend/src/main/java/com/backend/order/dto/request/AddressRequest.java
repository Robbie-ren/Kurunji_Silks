package com.backend.order.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddressRequest {

    @NotBlank
    private String fullName;

    @NotBlank
    private String phone;

    @NotBlank
    private String addressLine;

    @NotBlank
    private String city;

    @NotBlank
    private String state;

    @NotBlank
    private String pincode;

    private String landmark;
}