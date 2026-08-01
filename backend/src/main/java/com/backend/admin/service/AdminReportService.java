package com.backend.admin.service;

import com.backend.admin.dto.response.CustomerReportResponse;
import com.backend.admin.dto.response.LowStockProductResponse;
import com.backend.admin.dto.response.ProductSalesReportResponse;
import com.backend.admin.dto.response.SalesSummaryResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface AdminReportService {

    SalesSummaryResponse getSalesSummary();

    List<ProductSalesReportResponse> getProductSalesReport();

    List<LowStockProductResponse> getLowStockProducts();

    Page<CustomerReportResponse> getCustomerReport(
            int page,
            int size
    );
}