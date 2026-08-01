package com.backend.product.repository;

import com.backend.product.entity.Product;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Page<Product> findByActiveTrue(Pageable pageable);

    Page<Product> findByCategoryIdAndActiveTrue(Long categoryId, Pageable pageable);

    Page<Product> findByNameContainingIgnoreCaseAndActiveTrue(String name, Pageable pageable);

    Page<Product> findByFabricIgnoreCaseAndActiveTrue(String fabric, Pageable pageable);

    Page<Product> findByColorIgnoreCaseAndActiveTrue(String color, Pageable pageable);

    Page<Product> findByOccasionIgnoreCaseAndActiveTrue(String occasion, Pageable pageable);

    Page<Product> findByPriceBetweenAndActiveTrue(
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Pageable pageable
    );

    List<Product> findByStockQuantityLessThanEqualAndActiveTrue(Integer quantity);

    boolean existsByNameIgnoreCase(String name);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Product p WHERE p.id = :id")
    Optional<Product> findByIdForUpdate(@Param("id") Long id);
}