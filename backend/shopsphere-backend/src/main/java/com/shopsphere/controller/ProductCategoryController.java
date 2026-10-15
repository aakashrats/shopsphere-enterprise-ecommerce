
package com.shopsphere.controller;

import com.shopsphere.dto.response.CategoryResponse;
import com.shopsphere.dto.response.ProductResponse;
import com.shopsphere.service.ProductCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ProductCategoryController {

    private final ProductCategoryService productCategoryService;

    @PostMapping("/products/{productId}/categories")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void assignCategoriesToProduct(
            @PathVariable Long productId,
            @RequestBody List<Long> categoryIds) {
        productCategoryService.assignCategoriesToProduct(productId, categoryIds);
    }

    @GetMapping("/products/{productId}/categories")
    public List<CategoryResponse> getCategoriesByProductId(
            @PathVariable Long productId) {
        return productCategoryService.getCategoriesByProductId(productId)
                .stream()
                .map(category -> CategoryResponse.builder()
                        .id(category.getId())
                        .name(category.getName())
                        .slug(category.getSlug())
                        .description(category.getDescription())
                        .status(category.getStatus().toString())
                        .createdAt(category.getCreatedAt())
                        .updatedAt(category.getUpdatedAt())
                        .build())
                .toList();
    }

    @GetMapping("/categories/{categoryId}/products")
    public List<ProductResponse> getProductsByCategoryId(
            @PathVariable Long categoryId) {
        return productCategoryService.getProductsByCategoryId(categoryId)
                .stream()
                .map(product -> ProductResponse.builder()
                        .id(product.getId())
                        .name(product.getName())
                        .slug(product.getSlug())
                        .description(product.getDescription())
                        .sku(product.getSku())
                        .price(product.getPrice())
                        .stockQuantity(product.getStockQuantity())
                        .status(product.getStatus().toString())
                        .createdAt(product.getCreatedAt())
                        .updatedAt(product.getUpdatedAt())
                        .build())
                .toList();
    }

    @DeleteMapping("/products/{productId}/categories/{categoryId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeCategoryFromProduct(
            @PathVariable Long productId,
            @PathVariable Long categoryId) {
        productCategoryService.removeCategoryFromProduct(productId, categoryId);
    }
}
