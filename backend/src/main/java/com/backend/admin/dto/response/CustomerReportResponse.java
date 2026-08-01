package com.backend.admin.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerReportResponse {

    private Long userId;

    private String customerName;

    private String email;

    private Long totalOrders;

    private BigDecimal totalSpent;
}