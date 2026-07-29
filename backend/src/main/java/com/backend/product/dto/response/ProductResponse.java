package com.backend.product.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponse {

    private Long id;

    private String name;

    private String description;

    private BigDecimal price;

    private BigDecimal discountPrice;

    private String fabric;

    private String color;

    private String occasion;

    private Boolean blouseIncluded;

    private String sareeLength;

    private String washCare;

    private Integer stockQuantity;

    private Boolean active;

    private Long categoryId;

    private String categoryName;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}