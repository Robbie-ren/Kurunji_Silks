package com.backend.admin.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalesSummaryResponse {

    private Long totalOrders;

    private BigDecimal totalRevenue;

    private Long totalCustomers;

    private Long deliveredOrders;
}