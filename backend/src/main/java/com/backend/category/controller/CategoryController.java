package com.backend.category.controller;

import com.backend.category.dto.request.CategoryCreateRequest;
import com.backend.category.dto.request.CategoryUpdateRequest;
import com.backend.category.dto.response.CategoryResponse;
import com.backend.category.service.CategoryService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;


    // ============================================================
    // CREATE
    // ============================================================

    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(
            @Valid @RequestBody CategoryCreateRequest request
    ) {

        CategoryResponse response =
                categoryService.createCategory(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }


    // ============================================================
    // GET ALL ACTIVE
    // ============================================================

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {

        return ResponseEntity.ok(
                categoryService.getAllCategories()
        );
    }


    // ============================================================
    // GET BY ID
    // ============================================================

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getCategoryById(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                categoryService.getCategoryById(id)
        );
    }


    // ============================================================
    // UPDATE
    // ============================================================

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryUpdateRequest request
    ) {

        CategoryResponse response =
                categoryService.updateCategory(
                        id,
                        request
                );

        return ResponseEntity.ok(response);
    }


    // ============================================================
    // DELETE
    // ============================================================

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(
            @PathVariable Long id
    ) {

        categoryService.deleteCategory(id);

        return ResponseEntity.noContent().build();
    }
}