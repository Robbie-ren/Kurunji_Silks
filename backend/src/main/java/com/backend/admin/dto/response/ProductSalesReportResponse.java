package com.backend.admin.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductSalesReportResponse {

    private Long productId;

    private String productName;

    private Long totalQuantitySold;

    private BigDecimal totalSales;
}