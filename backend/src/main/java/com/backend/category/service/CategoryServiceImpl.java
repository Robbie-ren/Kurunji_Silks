package com.backend.category.service;

import com.backend.category.dto.request.CategoryCreateRequest;
import com.backend.category.dto.response.CategoryResponse;
import com.backend.category.entity.Category;
import com.backend.category.exception.CategoryAlreadyExistsException;
import com.backend.category.exception.CategoryNotFoundException;
import com.backend.category.repository.CategoryRepository;
import com.backend.category.service.CategoryService;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    // ============================================================
    // CREATE CATEGORY
    // ============================================================

    @Override
    public CategoryResponse createCategory(CategoryCreateRequest request) {

        // 1. Check whether category already exists

        boolean categoryExists =
                categoryRepository.existsByNameIgnoreCase(request.getName());

        if (categoryExists) {

            throw new CategoryAlreadyExistsException(
                    "Category already exists with name: "
                            + request.getName()
            );
        }

        // 2. Create Category entity

        Category category = new Category();

        category.setName(request.getName());

        category.setDescription(request.getDescription());

        category.setActive(true);

        // 3. Save category

        Category savedCategory =
                categoryRepository.save(category);

        // 4. Convert entity to response

        return convertToResponse(savedCategory);
    }


    // ============================================================
    // GET ALL CATEGORIES
    // ============================================================

    @Override
    public List<CategoryResponse> getAllCategories() {

        // 1. Get all categories

        List<Category> categories =
                categoryRepository.findAll();

        // 2. Convert entities to response objects

        return categories.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }


    // ============================================================
    // GET CATEGORY BY ID
    // ============================================================

    @Override
    public CategoryResponse getCategoryById(Long id) {

        // 1. Find category

        Category category =
                categoryRepository.findById(id)
                        .orElseThrow(() ->
                                new CategoryNotFoundException(
                                        "Category not found with id: " + id
                                )
                        );

        // 2. Convert entity to response

        return convertToResponse(category);
    }


    // ============================================================
    // UPDATE CATEGORY
    // ============================================================

    @Override
    public CategoryResponse updateCategory(
            Long id,
            CategoryCreateRequest request) {

        // 1. Find existing category

        Category category =
                categoryRepository.findById(id)
                        .orElseThrow(() ->
                                new CategoryNotFoundException(
                                        "Category not found with id: " + id
                                )
                        );

        // 2. Check if another category already has this name

        boolean nameAlreadyExists =
                categoryRepository.existsByNameIgnoreCase(
                        request.getName()
                );

        if (nameAlreadyExists
                && !category.getName().equalsIgnoreCase(request.getName())) {

            throw new CategoryAlreadyExistsException(
                    "Another category already exists with name: "
                            + request.getName()
            );
        }

        // 3. Update fields

        category.setName(request.getName());

        category.setDescription(request.getDescription());

        // 4. Save updated category

        Category updatedCategory =
                categoryRepository.save(category);

        // 5. Convert to response

        return convertToResponse(updatedCategory);
    }


    // ============================================================
    // DELETE CATEGORY
    // ============================================================

    @Override
    public void deleteCategory(Long id) {

        // 1. Check whether category exists

        Category category =
                categoryRepository.findById(id)
                        .orElseThrow(() ->
                                new CategoryNotFoundException(
                                        "Category not found with id: " + id
                                )
                        );

        // 2. Delete category

        categoryRepository.delete(category);
    }


    // ============================================================
    // ENTITY -> RESPONSE
    // ============================================================

    private CategoryResponse convertToResponse(Category category) {

        CategoryResponse response =
                new CategoryResponse();

        response.setId(category.getId());

        response.setName(category.getName());

        response.setDescription(category.getDescription());

        response.setActive(category.getActive());

        response.setCreatedAt(category.getCreatedAt());

        response.setUpdatedAt(category.getUpdatedAt());

        return response;
    }
}