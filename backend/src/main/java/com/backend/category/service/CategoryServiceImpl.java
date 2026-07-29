package com.backend.category.service;

import com.backend.category.dto.request.CategoryCreateRequest;
import com.backend.category.dto.request.CategoryUpdateRequest;
import com.backend.category.dto.response.CategoryResponse;
import com.backend.category.entity.Category;
import com.backend.category.repository.CategoryRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;


    // ============================================================
    // CREATE CATEGORY
    // ============================================================

    @Override
    public CategoryResponse createCategory(
            CategoryCreateRequest request
    ) {

        if (categoryRepository.existsByNameIgnoreCase(request.getName())) {
            throw new RuntimeException(
                    "Category already exists with name: "
                            + request.getName()
            );
        }

        Category category = Category.builder()
                .name(request.getName())
                .description(request.getDescription())
                .active(true)
                .build();

        Category savedCategory =
                categoryRepository.save(category);

        return mapToResponse(savedCategory);
    }


    // ============================================================
    // GET ALL ACTIVE CATEGORIES
    // ============================================================

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {

        return categoryRepository.findByActiveTrue()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


    // ============================================================
    // GET CATEGORY BY ID
    // ============================================================

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(
            Long id
    ) {

        Category category =
                categoryRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Category not found with id: " + id
                                )
                        );

        return mapToResponse(category);
    }


    // ============================================================
    // UPDATE CATEGORY
    // ============================================================

    @Override
    public CategoryResponse updateCategory(
            Long id,
            CategoryUpdateRequest request
    ) {

        Category category =
                categoryRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Category not found with id: " + id
                                )
                        );

        if (!category.getName()
                .equalsIgnoreCase(request.getName())
                &&
                categoryRepository.existsByNameIgnoreCase(
                        request.getName()
                )) {

            throw new RuntimeException(
                    "Category already exists with name: "
                            + request.getName()
            );
        }

        category.setName(request.getName());
        category.setDescription(request.getDescription());

        if (request.getActive() != null) {
            category.setActive(request.getActive());
        }

        Category updatedCategory =
                categoryRepository.save(category);

        return mapToResponse(updatedCategory);
    }


    // ============================================================
    // DELETE CATEGORY
    // ============================================================

    @Override
    public void deleteCategory(
            Long id
    ) {

        Category category =
                categoryRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Category not found with id: " + id
                                )
                        );

        // Soft delete
        category.setActive(false);

        categoryRepository.save(category);
    }


    // ============================================================
    // ENTITY -> RESPONSE
    // ============================================================

    private CategoryResponse mapToResponse(
            Category category
    ) {

        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .active(category.getActive())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .build();
    }
}