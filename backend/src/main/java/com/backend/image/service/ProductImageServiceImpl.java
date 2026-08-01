package com.backend.image.service;

import com.backend.common.exception.ResourceNotFoundException;
import com.backend.image.dto.response.ProductImageResponse;
import com.backend.image.entity.ProductImage;
import com.backend.image.exception.ImageUploadException;
import com.backend.image.repository.ProductImageRepository;
import com.backend.image.service.ProductImageService;
import com.backend.product.entity.Product;
import com.backend.product.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductImageServiceImpl implements ProductImageService {

    private final ProductImageRepository productImageRepository;
    private final ProductRepository productRepository;
    @Value("${app.file.upload-dir}")
    private String uploadDir;

    // ============================================================
    // ADD IMAGE
    // ============================================================

    @Override
    public ProductImageResponse addImage(
            Long productId,
            MultipartFile file,
            Boolean mainImage
    ) {

        if (file == null || file.isEmpty()) {
            throw new ImageUploadException("Image file is required.");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Product not found with id: " + productId
                        )
                );

        boolean isMainImage =
                mainImage != null && mainImage;

        // Only one main image is allowed per product.
        if (isMainImage) {

            productImageRepository
                    .findByProductIdAndMainImageTrue(productId)
                    .ifPresent(existingImage -> {
                        existingImage.setMainImage(false);
                        productImageRepository.save(existingImage);
                    });
        }

        String originalFilename = file.getOriginalFilename();

        String extension = "";

        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String filename = UUID.randomUUID().toString() + extension;

        try {

            Path uploadPath = Paths.get(uploadDir);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Files.copy(
                    file.getInputStream(),
                    uploadPath.resolve(filename),
                    StandardCopyOption.REPLACE_EXISTING
            );

        } catch (IOException e) {
            throw new ImageUploadException("Failed to upload image.");
        }

        ProductImage productImage = ProductImage.builder()
                .product(product)
                .imageUrl("/uploads/" + filename)
                .mainImage(isMainImage)
                .build();

        ProductImage savedImage =
                productImageRepository.save(productImage);

        return mapToResponse(savedImage);
    }


    // ============================================================
    // GET ALL IMAGES FOR PRODUCT
    // ============================================================

    @Override
    @Transactional(readOnly = true)
    public List<ProductImageResponse> getImagesByProduct(
            Long productId
    ) {

        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException(
                    "Product not found with id: " + productId
            );
        }

        return productImageRepository
                .findByProductId(productId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


    // ============================================================
    // GET IMAGE BY ID
    // ============================================================

    @Override
    @Transactional(readOnly = true)
    public ProductImageResponse getImageById(
            Long productId,
            Long imageId
    ) {

        ProductImage image =
                productImageRepository.findById(imageId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Image not found with id: " + imageId
                                )
                        );

        return mapToResponse(image);
    }


    // ============================================================
    // GET MAIN IMAGE
    // ============================================================

    @Override
    @Transactional(readOnly = true)
    public ProductImageResponse getMainImage(
            Long productId
    ) {

        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException(
                    "Product not found with id: " + productId
            );
        }

        ProductImage image =
                productImageRepository
                        .findByProductIdAndMainImageTrue(productId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Main image not found for product id: "
                                                + productId
                                )
                        );

        return mapToResponse(image);
    }


    // ============================================================
    // DELETE IMAGE
    // ============================================================

    @Override
    public void deleteImage(
            Long productId,
            Long imageId
    ){

        ProductImage image =
                productImageRepository.findByIdAndProductId(
                                imageId,
                                productId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Image not found with id: " + imageId
                                )
                        );

        productImageRepository.delete(image);
    }


    // ============================================================
    // ENTITY → RESPONSE
    // ============================================================

    private ProductImageResponse mapToResponse(
            ProductImage image
    ) {

        return ProductImageResponse.builder()
                .id(image.getId())
                .productId(image.getProduct().getId())
                .imageUrl(image.getImageUrl())
                .mainImage(image.getMainImage())
                .createdAt(image.getCreatedAt())
                .build();
    }
}