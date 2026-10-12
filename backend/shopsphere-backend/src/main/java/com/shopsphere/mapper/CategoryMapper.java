
package com.shopsphere.mapper;

import com.shopsphere.dto.request.CreateCategoryRequest;
import com.shopsphere.dto.response.CategoryResponse;
import com.shopsphere.entity.Category;

public final class CategoryMapper {

    private CategoryMapper() {
    }

    public static Category toEntity(CreateCategoryRequest request) {
        Category category = new Category();
        category.setName(request.getName().trim());
        category.setSlug(request.getSlug().trim().toLowerCase());
        category.setDescription(request.getDescription());
        return category;
    }

    public static CategoryResponse toResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .slug(category.getSlug())
                .description(category.getDescription())
                .status(category.getStatus())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .build();
    }
}
