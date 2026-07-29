package com.backend.product.repository;

import com.backend.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Page<Product> findByActiveTrue(Pageable pageable);

    Page<Product> findByCategoryIdAndActiveTrue(
            Long categoryId,
            Pageable pageable
    );

    Page<Product> findByNameContainingIgnoreCaseAndActiveTrue(
            String name,
            Pageable pageable
    );

    Page<Product> findByFabricIgnoreCaseAndActiveTrue(
            String fabric,
            Pageable pageable
    );

    Page<Product> findByColorIgnoreCaseAndActiveTrue(
            String color,
            Pageable pageable
    );

    Page<Product> findByOccasionIgnoreCaseAndActiveTrue(
            String occasion,
            Pageable pageable
    );

    Page<Product> findByPriceBetweenAndActiveTrue(
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Pageable pageable
    );

    List<Product> findByStockQuantityLessThanEqualAndActiveTrue(
            Integer quantity
    );

    boolean existsByNameIgnoreCase(String name);
}