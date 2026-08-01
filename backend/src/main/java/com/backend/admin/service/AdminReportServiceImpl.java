package com.backend.admin.service;

import com.backend.admin.dto.response.CustomerReportResponse;
import com.backend.admin.dto.response.LowStockProductResponse;
import com.backend.admin.dto.response.ProductSalesReportResponse;
import com.backend.admin.dto.response.SalesSummaryResponse;
import com.backend.auth.entity.User;
import com.backend.auth.repository.UserRepository;
import com.backend.order.enums.OrderStatus;
import com.backend.order.repository.OrderItemRepository;
import com.backend.order.repository.OrderRepository;
import com.backend.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminReportServiceImpl implements AdminReportService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Override
    public SalesSummaryResponse getSalesSummary() {
        return SalesSummaryResponse.builder()
                .totalOrders(orderRepository.count())
                .totalRevenue(orderRepository.getTotalRevenue())
                .totalCustomers(userRepository.count())
                .deliveredOrders(orderRepository.countByOrderStatus(OrderStatus.DELIVERED))
                .build();
    }

    @Override
    public List<ProductSalesReportResponse> getProductSalesReport() {
        return orderItemRepository.getProductSalesSummary()
                .stream()
                .map(row -> ProductSalesReportResponse.builder()
                        .productId((Long) row[0])
                        .productName((String) row[1])
                        .totalQuantitySold(((Number) row[2]).longValue())
                        .totalSales((BigDecimal) row[3])
                        .build())
                .toList();
    }

    @Override
    public List<LowStockProductResponse> getLowStockProducts() {
        return productRepository
                .findByStockQuantityLessThanEqualAndActiveTrue(5)
                .stream()
                .map(product -> LowStockProductResponse.builder()
                        .productId(product.getId())
                        .productName(product.getName())
                        .stockQuantity(product.getStockQuantity())
                        .build())
                .toList();
    }

    @Override
    public Page<CustomerReportResponse> getCustomerReport(
            int page,
            int size
    ) {

        Map<Long, Object[]> orderSummaryByUserId =
                orderRepository.getCustomerOrderSummary()
                        .stream()
                        .collect(Collectors.toMap(
                                row -> (Long) row[0],
                                row -> row
                        ));

        return userRepository
                .findAll(PageRequest.of(page, size))
                .map(user ->
                        buildCustomerReport(
                                user,
                                orderSummaryByUserId.get(user.getId())
                        )
                );
    }

    private CustomerReportResponse buildCustomerReport(User user, Object[] summary) {
        long totalOrders = 0L;
        BigDecimal totalSpent = BigDecimal.ZERO;

        if (summary != null) {
            totalOrders = ((Number) summary[1]).longValue();
            totalSpent = (BigDecimal) summary[2];
        }

        return CustomerReportResponse.builder()
                .userId(user.getId())
                .customerName(user.getName())
                .email(user.getEmail())
                .totalOrders(totalOrders)
                .totalSpent(totalSpent)
                .build();
    }
}