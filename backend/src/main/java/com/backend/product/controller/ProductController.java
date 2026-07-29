package com.backend.product.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import com.backend.common.dto.PageResponse;
import com.backend.product.dto.request.ProductCreateRequest;
import com.backend.product.dto.request.ProductUpdateRequest;
import com.backend.product.dto.response.ProductResponse;
import com.backend.product.service.ProductService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
public class ProductController {

    private final ProductService productService;


    // ============================================================
    // CREATE PRODUCT
    // ============================================================

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(
            @Valid @RequestBody ProductCreateRequest request) {

        ProductResponse response =
                productService.createProduct(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }


    // ============================================================
    // GET ALL ACTIVE PRODUCTS
    // ============================================================

    @GetMapping
    public ResponseEntity<PageResponse<ProductResponse>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable =
                PageRequest.of(page, size);

        PageResponse<ProductResponse> response =
                productService.getAllProducts(pageable);

        return ResponseEntity.ok(response);
    }


    // ============================================================
    // GET PRODUCT BY ID
    // ============================================================

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(
            @PathVariable Long id) {

        ProductResponse response =
                productService.getProductById(id);

        return ResponseEntity.ok(response);
    }


    // ============================================================
    // UPDATE PRODUCT
    // ============================================================

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductUpdateRequest request) {

        ProductResponse response =
                productService.updateProduct(id, request);

        return ResponseEntity.ok(response);
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

        Pageable pageable =
                PageRequest.of(page, size);

        PageResponse<ProductResponse> response =
                productService.getProductsByCategory(
                        categoryId,
                        pageable
                );

        return ResponseEntity.ok(response);
    }


    // ============================================================
    // SEARCH PRODUCTS BY NAME
    // ============================================================

    @GetMapping("/search")
    public ResponseEntity<PageResponse<ProductResponse>> searchProducts(
            @RequestParam String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable =
                PageRequest.of(page, size);

        PageResponse<ProductResponse> response =
                productService.searchProducts(
                        name,
                        pageable
                );

        return ResponseEntity.ok(response);
    }


    // ============================================================
    // FILTER BY FABRIC
    // ============================================================

    @GetMapping("/fabric/{fabric}")
    public ResponseEntity<PageResponse<ProductResponse>> getProductsByFabric(
            @PathVariable String fabric,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable =
                PageRequest.of(page, size);

        PageResponse<ProductResponse> response =
                productService.getProductsByFabric(
                        fabric,
                        pageable
                );

        return ResponseEntity.ok(response);
    }


    // ============================================================
    // FILTER BY COLOR
    // ============================================================

    @GetMapping("/color/{color}")
    public ResponseEntity<PageResponse<ProductResponse>> getProductsByColor(
            @PathVariable String color,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable =
                PageRequest.of(page, size);

        PageResponse<ProductResponse> response =
                productService.getProductsByColor(
                        color,
                        pageable
                );

        return ResponseEntity.ok(response);
    }


    // ============================================================
    // FILTER BY OCCASION
    // ============================================================

    @GetMapping("/occasion/{occasion}")
    public ResponseEntity<PageResponse<ProductResponse>> getProductsByOccasion(
            @PathVariable String occasion,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable =
                PageRequest.of(page, size);

        PageResponse<ProductResponse> response =
                productService.getProductsByOccasion(
                        occasion,
                        pageable
                );

        return ResponseEntity.ok(response);
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

        Pageable pageable =
                PageRequest.of(page, size);

        PageResponse<ProductResponse> response =
                productService.getProductsByPriceRange(
                        minPrice,
                        maxPrice,
                        pageable
                );

        return ResponseEntity.ok(response);
    }


    // ============================================================
    // LOW STOCK PRODUCTS
    // ============================================================

    @GetMapping("/low-stock")
    public ResponseEntity<List<ProductResponse>> getLowStockProducts(
            @RequestParam(defaultValue = "5") Integer quantity) {

        List<ProductResponse> response =
                productService.getLowStockProducts(quantity);

        return ResponseEntity.ok(response);
    }
}