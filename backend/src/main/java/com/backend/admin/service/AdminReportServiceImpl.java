package com.backend.admin.service;

import com.backend.admin.dto.response.CustomerReportResponse;
import com.backend.admin.dto.response.LowStockProductResponse;
import com.backend.admin.dto.response.ProductSalesReportResponse;
import com.backend.admin.dto.response.SalesSummaryResponse;
import com.backend.auth.entity.User;
import com.backend.auth.repository.UserRepository;
import com.backend.order.entity.Order;
import com.backend.order.entity.OrderItem;
import com.backend.order.enums.OrderStatus;
import com.backend.order.repository.OrderItemRepository;
import com.backend.order.repository.OrderRepository;
import com.backend.product.entity.Product;
import com.backend.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

        List<Order> orders = orderRepository.findAll();

        BigDecimal revenue = orders.stream()
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long delivered = orders.stream()
                .filter(o -> o.getOrderStatus() == OrderStatus.DELIVERED)
                .count();

        return SalesSummaryResponse.builder()
                .totalOrders((long) orders.size())
                .totalRevenue(revenue)
                .totalCustomers((long) userRepository.count())
                .deliveredOrders(delivered)
                .build();
    }

    @Override
    public List<ProductSalesReportResponse> getProductSalesReport() {

        Map<Product, List<OrderItem>> grouped =
                orderItemRepository.findAll()
                        .stream()
                        .collect(Collectors.groupingBy(OrderItem::getProduct));

        return grouped.entrySet().stream()
                .map(entry -> {

                    long qty = entry.getValue().stream()
                            .mapToLong(OrderItem::getQuantity)
                            .sum();

                    BigDecimal sales = entry.getValue().stream()
                            .map(OrderItem::getSubtotal)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    return ProductSalesReportResponse.builder()
                            .productId(entry.getKey().getId())
                            .productName(entry.getKey().getName())
                            .totalQuantitySold(qty)
                            .totalSales(sales)
                            .build();

                })
                .toList();
    }

    @Override
    public List<LowStockProductResponse> getLowStockProducts() {

        return productRepository
                .findByStockQuantityLessThanEqualAndActiveTrue(5)
                .stream()
                .map(product ->
                        LowStockProductResponse.builder()
                                .productId(product.getId())
                                .productName(product.getName())
                                .stockQuantity(product.getStockQuantity())
                                .build())
                .toList();
    }

    @Override
    public List<CustomerReportResponse> getCustomerReport() {

        List<Order> orders = orderRepository.findAll();

        return userRepository.findAll()
                .stream()
                .map(user -> {

                    List<Order> customerOrders = orders.stream()
                            .filter(o -> o.getUser().getId().equals(user.getId()))
                            .toList();

                    BigDecimal spent = customerOrders.stream()
                            .map(Order::getTotalAmount)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    return CustomerReportResponse.builder()
                            .userId(user.getId())
                            .customerName(user.getName())
                            .email(user.getEmail())
                            .totalOrders((long) customerOrders.size())
                            .totalSpent(spent)
                            .build();
                })
                .toList();
    }
}