package com.backend.image.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductImageResponse {

    private Long id;

    private Long productId;

    private String imageUrl;

    private Boolean mainImage;

    private LocalDateTime createdAt;
}