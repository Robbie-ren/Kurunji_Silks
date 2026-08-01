package com.backend.product.controller;

import com.backend.common.dto.PageResponse;
import com.backend.common.exception.BusinessValidationException;
import com.backend.product.dto.response.ProductResponse;
import com.backend.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private static final int MAX_PAGE_SIZE = 50;

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<PageResponse<ProductResponse>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(
                productService.getAllProducts(createPageable(page, size))
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<PageResponse<ProductResponse>> getProductsByCategory(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(
                productService.getProductsByCategory(categoryId, createPageable(page, size))
        );
    }

    @GetMapping("/search")
    public ResponseEntity<PageResponse<ProductResponse>> searchProducts(
            @RequestParam String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(
                productService.searchProducts(name, createPageable(page, size))
        );
    }

    @GetMapping("/fabric/{fabric}")
    public ResponseEntity<PageResponse<ProductResponse>> getProductsByFabric(
            @PathVariable String fabric,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(
                productService.getProductsByFabric(fabric, createPageable(page, size))
        );
    }

    @GetMapping("/color/{color}")
    public ResponseEntity<PageResponse<ProductResponse>> getProductsByColor(
            @PathVariable String color,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(
                productService.getProductsByColor(color, createPageable(page, size))
        );
    }

    @GetMapping("/occasion/{occasion}")
    public ResponseEntity<PageResponse<ProductResponse>> getProductsByOccasion(
            @PathVariable String occasion,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(
                productService.getProductsByOccasion(occasion, createPageable(page, size))
        );
    }

    @GetMapping("/price-range")
    public ResponseEntity<PageResponse<ProductResponse>> getProductsByPriceRange(
            @RequestParam BigDecimal minPrice,
            @RequestParam BigDecimal maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        if (minPrice.compareTo(maxPrice) > 0) {
            throw new BusinessValidationException("Minimum price cannot be greater than maximum price.");
        }

        return ResponseEntity.ok(
                productService.getProductsByPriceRange(
                        minPrice,
                        maxPrice,
                        createPageable(page, size)
                )
        );
    }

    private Pageable createPageable(int page, int size) {
        if (page < 0) {
            throw new BusinessValidationException("Page number cannot be negative.");
        }

        if (size <= 0) {
            throw new BusinessValidationException("Page size must be greater than zero.");
        }

        return PageRequest.of(page, Math.min(size, MAX_PAGE_SIZE));
    }
}