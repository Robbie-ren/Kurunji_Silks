package com.backend.category.service;

import com.backend.category.dto.request.CategoryCreateRequest;
import com.backend.category.dto.request.CategoryUpdateRequest;
import com.backend.category.dto.response.CategoryResponse;
import com.backend.common.dto.PageResponse;
import org.springframework.data.domain.Pageable;

public interface CategoryService {

    CategoryResponse createCategory(
            CategoryCreateRequest request
    );

    PageResponse<CategoryResponse> getAllCategories(
            Pageable pageable
    );

    CategoryResponse getCategoryById(
            Long id
    );

    CategoryResponse updateCategory(
            Long id,
            CategoryUpdateRequest request
    );

    void deleteCategory(
            Long id
    );
}