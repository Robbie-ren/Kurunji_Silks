package com.backend.image.controller;

import com.backend.image.dto.response.ProductImageResponse;
import com.backend.image.service.ProductImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/products/{productId}/images")
@RequiredArgsConstructor
public class ProductImageController {

    private final ProductImageService productImageService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductImageResponse> addImage(
            @PathVariable Long productId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(defaultValue = "false") Boolean mainImage
    ) {
        ProductImageResponse response =
                productImageService.addImage(productId, file, mainImage);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<ProductImageResponse>> getImages(
            @PathVariable Long productId
    ) {
        return ResponseEntity.ok(
                productImageService.getImagesByProduct(productId)
        );
    }

    @GetMapping("/main")
    public ResponseEntity<ProductImageResponse> getMainImage(
            @PathVariable Long productId
    ) {
        return ResponseEntity.ok(
                productImageService.getMainImage(productId)
        );
    }

    @GetMapping("/{imageId}")
    public ResponseEntity<ProductImageResponse> getImageById(
            @PathVariable Long productId,
            @PathVariable Long imageId
    ) {
        return ResponseEntity.ok(
                productImageService.getImageById(productId, imageId)
        );
    }

    @DeleteMapping("/{imageId}")
    public ResponseEntity<Void> deleteImage(
            @PathVariable Long productId,
            @PathVariable Long imageId
    ) {
        productImageService.deleteImage(productId, imageId);
        return ResponseEntity.noContent().build();
    }
}