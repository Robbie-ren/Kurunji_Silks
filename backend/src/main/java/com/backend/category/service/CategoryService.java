package com.backend.category.service;

import com.backend.category.dto.request.CategoryCreateRequest;
import com.backend.category.dto.request.CategoryUpdateRequest;
import com.backend.category.dto.response.CategoryResponse;

import java.util.List;

public interface CategoryService {

    CategoryResponse createCategory(
            CategoryCreateRequest request
    );

    List<CategoryResponse> getAllCategories();

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