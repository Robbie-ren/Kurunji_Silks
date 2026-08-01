package com.backend.image.service;

import com.backend.image.dto.response.ProductImageResponse;

import java.util.List;

public interface ProductImageService {

    ProductImageResponse addImage(
            Long productId,
            String imageUrl,
            Boolean mainImage
    );

    List<ProductImageResponse> getImagesByProduct(
            Long productId
    );

    ProductImageResponse getImageById(
            Long imageId
    );

    ProductImageResponse getMainImage(
            Long productId
    );

    void deleteImage(
            Long imageId
    );
}