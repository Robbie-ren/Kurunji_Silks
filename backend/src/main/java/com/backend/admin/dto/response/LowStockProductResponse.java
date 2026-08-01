package com.backend.admin.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LowStockProductResponse {

    private Long productId;

    private String productName;

    private Integer stockQuantity;
}