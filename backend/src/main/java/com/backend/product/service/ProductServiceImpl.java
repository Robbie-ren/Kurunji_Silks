package com.backend.product.service;

import com.backend.category.entity.Category;
import com.backend.category.repository.CategoryRepository;
import com.backend.common.dto.PageResponse;
import com.backend.product.dto.request.ProductCreateRequest;
import com.backend.product.dto.request.ProductUpdateRequest;
import com.backend.product.dto.response.ProductResponse;
import com.backend.product.entity.Product;
import com.backend.product.exception.DuplicateProductException;
import com.backend.product.exception.InvalidProductException;
import com.backend.product.exception.ProductNotFoundException;
import com.backend.product.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    private final CategoryRepository categoryRepository;


    // ============================================================
    // CREATE PRODUCT
    // ============================================================

    @Override
    public ProductResponse createProduct(ProductCreateRequest request) {

        validateProductPrice(
                request.getPrice(),
                request.getDiscountPrice()
        );

        if (productRepository.existsByNameIgnoreCase(request.getName())) {

            throw new DuplicateProductException(
                    "A product already exists with name: "
                            + request.getName()
            );
        }

        Category category = categoryRepository
                .findById(request.getCategoryId())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Category not found with ID: "
                                        + request.getCategoryId()
                        )
                );

        Product product = new Product();

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setDiscountPrice(request.getDiscountPrice());
        product.setFabric(request.getFabric());
        product.setColor(request.getColor());
        product.setOccasion(request.getOccasion());

        if (request.getBlouseIncluded() != null) {
            product.setBlouseIncluded(
                    request.getBlouseIncluded()
            );
        } else {
            product.setBlouseIncluded(false);
        }

        product.setSareeLength(request.getSareeLength());
        product.setWashCare(request.getWashCare());
        product.setStockQuantity(request.getStockQuantity());

        if (request.getActive() != null) {
            product.setActive(
                    request.getActive()
            );
        } else {
            product.setActive(true);
        }

        product.setCategory(category);

        Product savedProduct =
                productRepository.save(product);

        return convertToResponse(savedProduct);
    }


    // ============================================================
    // GET ALL ACTIVE PRODUCTS
    // ============================================================

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ProductResponse> getAllProducts(
            Pageable pageable) {

        Page<Product> productPage =
                productRepository.findByActiveTrue(pageable);

        return convertToPageResponse(productPage);
    }


    // ============================================================
    // GET PRODUCT BY ID
    // ============================================================

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {

        Product product =
                productRepository.findById(id)
                        .orElseThrow(() ->
                                new ProductNotFoundException(
                                        "Product not found with ID: "
                                                + id
                                )
                        );

        return convertToResponse(product);
    }


    // ============================================================
    // UPDATE PRODUCT
    // ============================================================

    @Override
    public ProductResponse updateProduct(
            Long id,
            ProductUpdateRequest request) {

        Product product =
                productRepository.findById(id)
                        .orElseThrow(() ->
                                new ProductNotFoundException(
                                        "Product not found with ID: "
                                                + id
                                )
                        );


        // --------------------------------------------------------
        // Check duplicate product name
        // --------------------------------------------------------

        if (request.getName() != null
                && !request.getName()
                .equalsIgnoreCase(product.getName())
                && productRepository.existsByNameIgnoreCase(
                request.getName())) {

            throw new DuplicateProductException(
                    "A product already exists with name: "
                            + request.getName()
            );
        }


        // --------------------------------------------------------
        // Validate final price
        // --------------------------------------------------------

        BigDecimal finalPrice =
                request.getPrice() != null
                        ? request.getPrice()
                        : product.getPrice();

        BigDecimal finalDiscountPrice =
                request.getDiscountPrice() != null
                        ? request.getDiscountPrice()
                        : product.getDiscountPrice();

        validateProductPrice(
                finalPrice,
                finalDiscountPrice
        );


        // --------------------------------------------------------
        // Update fields
        // --------------------------------------------------------

        if (request.getName() != null) {
            product.setName(request.getName());
        }

        if (request.getDescription() != null) {
            product.setDescription(
                    request.getDescription()
            );
        }

        if (request.getPrice() != null) {
            product.setPrice(
                    request.getPrice()
            );
        }

        if (request.getDiscountPrice() != null) {
            product.setDiscountPrice(
                    request.getDiscountPrice()
            );
        }

        if (request.getFabric() != null) {
            product.setFabric(
                    request.getFabric()
            );
        }

        if (request.getColor() != null) {
            product.setColor(
                    request.getColor()
            );
        }

        if (request.getOccasion() != null) {
            product.setOccasion(
                    request.getOccasion()
            );
        }

        if (request.getBlouseIncluded() != null) {
            product.setBlouseIncluded(
                    request.getBlouseIncluded()
            );
        }

        if (request.getSareeLength() != null) {
            product.setSareeLength(
                    request.getSareeLength()
            );
        }

        if (request.getWashCare() != null) {
            product.setWashCare(
                    request.getWashCare()
            );
        }

        if (request.getStockQuantity() != null) {
            product.setStockQuantity(
                    request.getStockQuantity()
            );
        }

        if (request.getActive() != null) {
            product.setActive(
                    request.getActive()
            );
        }


        // --------------------------------------------------------
        // Update category
        // --------------------------------------------------------

        if (request.getCategoryId() != null) {

            Category category =
                    categoryRepository.findById(
                            request.getCategoryId()
                    ).orElseThrow(() ->
                            new IllegalArgumentException(
                                    "Category not found with ID: "
                                            + request.getCategoryId()
                            )
                    );

            product.setCategory(category);
        }


        // --------------------------------------------------------
        // Save updated product
        // --------------------------------------------------------

        Product updatedProduct =
                productRepository.save(product);

        return convertToResponse(updatedProduct);
    }


    // ============================================================
    // DELETE PRODUCT
    // ============================================================

    @Override
    public void deleteProduct(Long id) {

        Product product =
                productRepository.findById(id)
                        .orElseThrow(() ->
                                new ProductNotFoundException(
                                        "Product not found with ID: "
                                                + id
                                )
                        );

        /*
         * Soft delete.
         *
         * We do not physically remove the product.
         * Instead, active is changed to false.
         */

        product.setActive(false);

        productRepository.save(product);
    }


    // ============================================================
    // GET PRODUCTS BY CATEGORY
    // ============================================================

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ProductResponse> getProductsByCategory(
            Long categoryId,
            Pageable pageable) {

        Page<Product> productPage =
                productRepository
                        .findByCategoryIdAndActiveTrue(
                                categoryId,
                                pageable
                        );

        return convertToPageResponse(productPage);
    }


    // ============================================================
    // SEARCH PRODUCTS BY NAME
    // ============================================================

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ProductResponse> searchProducts(
            String name,
            Pageable pageable) {

        if (name == null || name.isBlank()) {

            throw new InvalidProductException(
                    "Search name cannot be empty"
            );
        }

        Page<Product> productPage =
                productRepository
                        .findByNameContainingIgnoreCaseAndActiveTrue(
                                name,
                                pageable
                        );

        return convertToPageResponse(productPage);
    }


    // ============================================================
    // FILTER PRODUCTS BY FABRIC
    // ============================================================

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ProductResponse> getProductsByFabric(
            String fabric,
            Pageable pageable) {

        if (fabric == null || fabric.isBlank()) {

            throw new InvalidProductException(
                    "Fabric cannot be empty"
            );
        }

        Page<Product> productPage =
                productRepository
                        .findByFabricIgnoreCaseAndActiveTrue(
                                fabric,
                                pageable
                        );

        return convertToPageResponse(productPage);
    }


    // ============================================================
    // FILTER PRODUCTS BY COLOR
    // ============================================================

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ProductResponse> getProductsByColor(
            String color,
            Pageable pageable) {

        if (color == null || color.isBlank()) {

            throw new InvalidProductException(
                    "Color cannot be empty"
            );
        }

        Page<Product> productPage =
                productRepository
                        .findByColorIgnoreCaseAndActiveTrue(
                                color,
                                pageable
                        );

        return convertToPageResponse(productPage);
    }


    // ============================================================
    // FILTER PRODUCTS BY OCCASION
    // ============================================================

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ProductResponse> getProductsByOccasion(
            String occasion,
            Pageable pageable) {

        if (occasion == null || occasion.isBlank()) {

            throw new InvalidProductException(
                    "Occasion cannot be empty"
            );
        }

        Page<Product> productPage =
                productRepository
                        .findByOccasionIgnoreCaseAndActiveTrue(
                                occasion,
                                pageable
                        );

        return convertToPageResponse(productPage);
    }


    // ============================================================
    // FILTER PRODUCTS BY PRICE RANGE
    // ============================================================

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ProductResponse> getProductsByPriceRange(
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Pageable pageable) {

        if (minPrice == null || maxPrice == null) {

            throw new InvalidProductException(
                    "Both minimum and maximum price are required"
            );
        }

        if (minPrice.compareTo(BigDecimal.ZERO) < 0) {

            throw new InvalidProductException(
                    "Minimum price cannot be negative"
            );
        }

        if (maxPrice.compareTo(minPrice) < 0) {

            throw new InvalidProductException(
                    "Maximum price cannot be less than minimum price"
            );
        }

        Page<Product> productPage =
                productRepository
                        .findByPriceBetweenAndActiveTrue(
                                minPrice,
                                maxPrice,
                                pageable
                        );

        return convertToPageResponse(productPage);
    }


    // ============================================================
    // GET LOW STOCK PRODUCTS
    // ============================================================

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getLowStockProducts(
            Integer quantity) {

        if (quantity == null || quantity < 0) {

            throw new InvalidProductException(
                    "Stock quantity cannot be negative"
            );
        }

        return productRepository
                .findByStockQuantityLessThanEqualAndActiveTrue(
                        quantity
                )
                .stream()
                .map(this::convertToResponse)
                .toList();
    }


    // ============================================================
    // VALIDATE PRODUCT PRICE
    // ============================================================

    private void validateProductPrice(
            BigDecimal price,
            BigDecimal discountPrice) {

        if (price == null) {

            throw new InvalidProductException(
                    "Price is required"
            );
        }

        if (price.compareTo(BigDecimal.ZERO) <= 0) {

            throw new InvalidProductException(
                    "Price must be greater than zero"
            );
        }

        if (discountPrice != null) {

            if (discountPrice.compareTo(BigDecimal.ZERO) < 0) {

                throw new InvalidProductException(
                        "Discount price cannot be negative"
                );
            }

            if (discountPrice.compareTo(price) > 0) {

                throw new InvalidProductException(
                        "Discount price cannot be greater than original price"
                );
            }
        }
    }


    // ============================================================
    // CONVERT PAGE TO PAGE RESPONSE
    // ============================================================

    private PageResponse<ProductResponse> convertToPageResponse(
            Page<Product> productPage) {

        List<ProductResponse> products =
                productPage.getContent()
                        .stream()
                        .map(this::convertToResponse)
                        .toList();

        return PageResponse
                .<ProductResponse>builder()
                .content(products)
                .pageNumber(productPage.getNumber())
                .pageSize(productPage.getSize())
                .totalElements(productPage.getTotalElements())
                .totalPages(productPage.getTotalPages())
                .first(productPage.isFirst())
                .last(productPage.isLast())
                .build();
    }


    // ============================================================
    // CONVERT PRODUCT ENTITY TO PRODUCT RESPONSE
    // ============================================================

    private ProductResponse convertToResponse(
            Product product) {

        return ProductResponse
                .builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .discountPrice(product.getDiscountPrice())
                .fabric(product.getFabric())
                .color(product.getColor())
                .occasion(product.getOccasion())
                .blouseIncluded(product.getBlouseIncluded())
                .sareeLength(product.getSareeLength())
                .washCare(product.getWashCare())
                .stockQuantity(product.getStockQuantity())
                .active(product.getActive())
                .categoryId(product.getCategory().getId())
                .categoryName(product.getCategory().getName())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }
}