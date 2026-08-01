package com.backend.image.controller;

import com.backend.image.dto.response.ProductImageResponse;
import com.backend.image.service.ProductImageService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products/{productId}/images")
@RequiredArgsConstructor
public class ProductImageController {

    private final ProductImageService productImageService;


    // ============================================================
    // ADD IMAGE
    // ============================================================

    @PostMapping
    public ResponseEntity<ProductImageResponse> addImage(
            @PathVariable Long productId,
            @RequestParam String imageUrl,
            @RequestParam(defaultValue = "false") Boolean mainImage
    ) {

        ProductImageResponse response =
                productImageService.addImage(
                        productId,
                        imageUrl,
                        mainImage
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }


    // ============================================================
    // GET ALL IMAGES
    // ============================================================

    @GetMapping
    public ResponseEntity<List<ProductImageResponse>> getImages(
            @PathVariable Long productId
    ) {

        return ResponseEntity.ok(
                productImageService.getImagesByProduct(productId)
        );
    }


    // ============================================================
    // GET MAIN IMAGE
    // ============================================================

    @GetMapping("/main")
    public ResponseEntity<ProductImageResponse> getMainImage(
            @PathVariable Long productId
    ) {

        return ResponseEntity.ok(
                productImageService.getMainImage(productId)
        );
    }


    // ============================================================
    // GET IMAGE BY ID
    // ============================================================

    @GetMapping("/{imageId}")
    public ResponseEntity<ProductImageResponse> getImageById(
            @PathVariable Long productId,
            @PathVariable Long imageId
    ) {

        return ResponseEntity.ok(
                productImageService.getImageById(imageId)
        );
    }


    // ============================================================
    // DELETE IMAGE
    // ============================================================

    @DeleteMapping("/{imageId}")
    public ResponseEntity<Void> deleteImage(
            @PathVariable Long productId,
            @PathVariable Long imageId
    ) {

        productImageService.deleteImage(imageId);

        return ResponseEntity.noContent().build();
    }
}