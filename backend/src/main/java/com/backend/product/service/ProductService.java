package com.backend.product.service;

import com.backend.common.dto.PageResponse;
import com.backend.product.dto.request.ProductCreateRequest;
import com.backend.product.dto.request.ProductUpdateRequest;
import com.backend.product.dto.response.ProductResponse;

import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface ProductService {

    ProductResponse createProduct(ProductCreateRequest request);

    PageResponse<ProductResponse> getAllProducts(Pageable pageable);

    ProductResponse getProductById(Long id);

    ProductResponse updateProduct(
            Long id,
            ProductUpdateRequest request
    );

    void deleteProduct(Long id);

    PageResponse<ProductResponse> getProductsByCategory(
            Long categoryId,
            Pageable pageable
    );

    PageResponse<ProductResponse> searchProducts(
            String name,
            Pageable pageable
    );

    PageResponse<ProductResponse> getProductsByFabric(
            String fabric,
            Pageable pageable
    );

    PageResponse<ProductResponse> getProductsByColor(
            String color,
            Pageable pageable
    );

    PageResponse<ProductResponse> getProductsByOccasion(
            String occasion,
            Pageable pageable
    );

    PageResponse<ProductResponse> getProductsByPriceRange(
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Pageable pageable
    );

    List<ProductResponse> getLowStockProducts(
            Integer quantity
    );
}