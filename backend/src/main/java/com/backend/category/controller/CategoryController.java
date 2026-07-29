package com.backend.category.controller;

import com.backend.category.dto.request.CategoryCreateRequest;
import com.backend.category.dto.response.CategoryResponse;
import com.backend.category.service.CategoryService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@SecurityRequirement(name = "Bearer Authentication")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }


    // ============================================================
    // CREATE CATEGORY
    // ============================================================

    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(
            @Valid @RequestBody CategoryCreateRequest request) {

        CategoryResponse response =
                categoryService.createCategory(request);

        return new ResponseEntity<>(
                response,
                HttpStatus.CREATED
        );
    }


    // ============================================================
    // GET ALL CATEGORIES
    // ============================================================

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {

        List<CategoryResponse> response =
                categoryService.getAllCategories();

        return new ResponseEntity<>(
                response,
                HttpStatus.OK
        );
    }


    // ============================================================
    // GET CATEGORY BY ID
    // ============================================================

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getCategoryById(
            @PathVariable Long id) {

        CategoryResponse response =
                categoryService.getCategoryById(id);

        return new ResponseEntity<>(
                response,
                HttpStatus.OK
        );
    }


    // ============================================================
    // UPDATE CATEGORY
    // ============================================================

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryCreateRequest request) {

        CategoryResponse response =
                categoryService.updateCategory(
                        id,
                        request
                );

        return new ResponseEntity<>(
                response,
                HttpStatus.OK
        );
    }


    // ============================================================
    // DELETE CATEGORY
    // ============================================================

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(
            @PathVariable Long id) {

        categoryService.deleteCategory(id);

        return new ResponseEntity<>(
                HttpStatus.NO_CONTENT
        );
    }
}