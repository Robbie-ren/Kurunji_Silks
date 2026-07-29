package com.backend.product.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductUpdateRequest {

    private String name;

    private String description;

    @DecimalMin(
            value = "0.0",
            inclusive = false,
            message = "Price must be greater than 0"
    )
    private BigDecimal price;

    @PositiveOrZero(message = "Discount price cannot be negative")
    private BigDecimal discountPrice;

    private String fabric;

    private String color;

    private String occasion;

    private Boolean blouseIncluded;

    private String sareeLength;

    private String washCare;

    @PositiveOrZero(message = "Stock quantity cannot be negative")
    private Integer stockQuantity;

    private Boolean active;

    private Long categoryId;
}