
package com.shopsphere.controller;

import com.shopsphere.dto.request.CreateCategoryRequest;
import com.shopsphere.dto.response.CategoryResponse;
import com.shopsphere.entity.Category;
import com.shopsphere.mapper.CategoryMapper;
import com.shopsphere.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryResponse createCategory(
            @Valid @RequestBody CreateCategoryRequest request) {

        Category category = CategoryMapper.toEntity(request);

        return CategoryMapper.toResponse(
                categoryService.createCategory(category)
        );
    }

    @GetMapping
    public List<CategoryResponse> getAllCategories() {
        return categoryService.getAllCategories()
                .stream()
                .map(CategoryMapper::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public CategoryResponse getCategoryById(@PathVariable Long id) {
        return CategoryMapper.toResponse(
                categoryService.getCategoryById(id)
        );
    }

    @PutMapping("/{id}")
    public CategoryResponse updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CreateCategoryRequest request) {

        Category category = CategoryMapper.toEntity(request);

        return CategoryMapper.toResponse(
                categoryService.updateCategory(id, category)
        );
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
    }
}
