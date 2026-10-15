
package com.shopsphere.controller;

import com.shopsphere.dto.request.CreateProductRequest;
import com.shopsphere.dto.response.ProductResponse;
import com.shopsphere.entity.Product;
import com.shopsphere.mapper.ProductMapper;
import com.shopsphere.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;


@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponse createProduct(
            @Valid @RequestBody CreateProductRequest request) {
        Product product = ProductMapper.toEntity(request);
        return ProductMapper.toResponse(
                productService.createProduct(product)
        );
    }

    @GetMapping
    public Page<ProductResponse> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        if (page < 0) {
            throw new IllegalArgumentException("Page must be zero or greater");
        }

        if (size < 1 || size > 100) {
            throw new IllegalArgumentException("Page size must be between 1 and 100");
        }

        if (!List.of("id", "name", "price", "createdAt", "updatedAt",
                "stockQuantity", "status", "sku", "slug").contains(sortBy)) {
            throw new IllegalArgumentException("Unsupported sort field: " + sortBy);
        }

        if (!direction.equalsIgnoreCase("asc")
                && !direction.equalsIgnoreCase("desc")) {
            throw new IllegalArgumentException("Direction must be 'asc' or 'desc'");
        }

        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        PageRequest pageRequest = PageRequest.of(page, size, sort);

        return productService.getAllProducts(pageRequest)
                .map(ProductMapper::toResponse);
    }

    @GetMapping("/{id}")
    public ProductResponse getProductById(@PathVariable Long id) {
        return ProductMapper.toResponse(
                productService.getProductById(id)
        );
    }

    @PutMapping("/{id}")
    public ProductResponse updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody CreateProductRequest request) {
        Product product = ProductMapper.toEntity(request);
        return ProductMapper.toResponse(
                productService.updateProduct(id, product)
        );
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
    }
}
