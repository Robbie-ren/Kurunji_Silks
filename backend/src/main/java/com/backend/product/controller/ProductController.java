package com.backend.product.controller;

import com.backend.common.dto.PageResponse;
import com.backend.common.exception.BusinessValidationException;
import com.backend.product.dto.request.ProductCreateRequest;
import com.backend.product.dto.request.ProductUpdateRequest;
import com.backend.product.dto.response.ProductResponse;
import com.backend.product.service.ProductService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private static final int MAX_PAGE_SIZE = 50;

    private final ProductService productService;

    // ============================================================
    // CREATE PRODUCT
    // ============================================================

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(
            @Valid @RequestBody ProductCreateRequest request) {

        ProductResponse response = productService.createProduct(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // ============================================================
    // GET ALL PRODUCTS
    // ============================================================

    @GetMapping
    public ResponseEntity<PageResponse<ProductResponse>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, Math.min(size, MAX_PAGE_SIZE));

        return ResponseEntity.ok(
                productService.getAllProducts(pageable)
        );
    }

    // ============================================================
    // GET PRODUCT BY ID
    // ============================================================

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                productService.getProductById(id)
        );
    }

    // ============================================================
    // UPDATE PRODUCT
    // ============================================================

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductUpdateRequest request) {

        return ResponseEntity.ok(
                productService.updateProduct(id, request)
        );
    }

    // ============================================================
    // DELETE PRODUCT
    // ============================================================

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(
            @PathVariable Long id) {

        productService.deleteProduct(id);

        return ResponseEntity.noContent().build();
    }

    // ============================================================
    // GET PRODUCTS BY CATEGORY
    // ============================================================

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<PageResponse<ProductResponse>> getProductsByCategory(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, Math.min(size, MAX_PAGE_SIZE));

        return ResponseEntity.ok(
                productService.getProductsByCategory(categoryId, pageable)
        );
    }

    // ============================================================
    // SEARCH PRODUCTS
    // ============================================================

    @GetMapping("/search")
    public ResponseEntity<PageResponse<ProductResponse>> searchProducts(
            @RequestParam String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, Math.min(size, MAX_PAGE_SIZE));

        return ResponseEntity.ok(
                productService.searchProducts(name, pageable)
        );
    }

    // ============================================================
    // FILTER BY FABRIC
    // ============================================================

    @GetMapping("/fabric/{fabric}")
    public ResponseEntity<PageResponse<ProductResponse>> getProductsByFabric(
            @PathVariable String fabric,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, Math.min(size, MAX_PAGE_SIZE));

        return ResponseEntity.ok(
                productService.getProductsByFabric(fabric, pageable)
        );
    }

    // ============================================================
    // FILTER BY COLOR
    // ============================================================

    @GetMapping("/color/{color}")
    public ResponseEntity<PageResponse<ProductResponse>> getProductsByColor(
            @PathVariable String color,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, Math.min(size, MAX_PAGE_SIZE));

        return ResponseEntity.ok(
                productService.getProductsByColor(color, pageable)
        );
    }

    // ============================================================
    // FILTER BY OCCASION
    // ============================================================

    @GetMapping("/occasion/{occasion}")
    public ResponseEntity<PageResponse<ProductResponse>> getProductsByOccasion(
            @PathVariable String occasion,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, Math.min(size, MAX_PAGE_SIZE));

        return ResponseEntity.ok(
                productService.getProductsByOccasion(occasion, pageable)
        );
    }

    // ============================================================
    // FILTER BY PRICE RANGE
    // ============================================================

    @GetMapping("/price-range")
    public ResponseEntity<PageResponse<ProductResponse>> getProductsByPriceRange(
            @RequestParam BigDecimal minPrice,
            @RequestParam BigDecimal maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        if (minPrice.compareTo(maxPrice) > 0) {
            throw new BusinessValidationException(
                    "Minimum price cannot be greater than maximum price."
            );
        }

        Pageable pageable = PageRequest.of(page, Math.min(size, MAX_PAGE_SIZE));

        return ResponseEntity.ok(
                productService.getProductsByPriceRange(
                        minPrice,
                        maxPrice,
                        pageable
                )
        );
    }

    // ============================================================
    // LOW STOCK PRODUCTS
    // ============================================================

    @GetMapping("/low-stock")
    public ResponseEntity<List<ProductResponse>> getLowStockProducts(
            @RequestParam(defaultValue = "5") Integer quantity) {

        if (quantity <= 0) {
            throw new BusinessValidationException(
                    "Quantity must be greater than zero."
            );
        }

        return ResponseEntity.ok(
                productService.getLowStockProducts(quantity)
        );
    }
}