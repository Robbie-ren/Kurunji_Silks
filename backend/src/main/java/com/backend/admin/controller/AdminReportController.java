package com.backend.admin.controller;

import com.backend.admin.dto.response.CustomerReportResponse;
import com.backend.admin.dto.response.LowStockProductResponse;
import com.backend.admin.dto.response.ProductSalesReportResponse;
import com.backend.admin.dto.response.SalesSummaryResponse;
import com.backend.admin.service.AdminReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/reports")
@RequiredArgsConstructor
public class AdminReportController {

    private final AdminReportService adminReportService;

    @GetMapping("/sales-summary")
    public SalesSummaryResponse getSalesSummary() {
        return adminReportService.getSalesSummary();
    }

    @GetMapping("/product-sales")
    public List<ProductSalesReportResponse> getProductSalesReport() {
        return adminReportService.getProductSalesReport();
    }

    @GetMapping("/low-stock")
    public List<LowStockProductResponse> getLowStockProducts() {
        return adminReportService.getLowStockProducts();
    }

    @GetMapping("/customers")
    public List<CustomerReportResponse> getCustomerReport() {
        return adminReportService.getCustomerReport();
    }
}