
package com.shopsphere.mapper;

import com.shopsphere.dto.request.CreateProductRequest;
import com.shopsphere.dto.response.ProductResponse;
import com.shopsphere.entity.Product;

public final class ProductMapper {

    private ProductMapper() {
    }

    public static Product toEntity(CreateProductRequest request) {
        Product product = new Product();
        product.setName(request.getName().trim());
        product.setSlug(request.getSlug().trim().toLowerCase());
        product.setDescription(request.getDescription());
        product.setSku(request.getSku().trim().toUpperCase());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());
        return product;
    }

    public static ProductResponse toResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .slug(product.getSlug())
                .description(product.getDescription())
                .sku(product.getSku())
                .price(product.getPrice())
                .stockQuantity(product.getStockQuantity())
                .status(product.getStatus())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }
}
