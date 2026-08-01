package com.backend.image.service;

import com.backend.image.dto.response.ProductImageResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductImageService {

    ProductImageResponse addImage(
            Long productId,
            MultipartFile file,
            Boolean mainImage
    );

    List<ProductImageResponse> getImagesByProduct(
            Long productId
    );

    ProductImageResponse getImageById(
            Long productId,
            Long imageId
    );

    ProductImageResponse getMainImage(
            Long productId
    );

    void deleteImage(
            Long productId,
            Long imageId
    );
}