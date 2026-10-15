
package com.shopsphere.service.impl;

import com.shopsphere.entity.Category;
import com.shopsphere.entity.Product;
import com.shopsphere.entity.ProductCategory;
import com.shopsphere.entity.ProductCategoryId;
import com.shopsphere.repository.CategoryRepository;
import com.shopsphere.repository.ProductCategoryRepository;
import com.shopsphere.repository.ProductRepository;
import com.shopsphere.service.ProductCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductCategoryServiceImpl implements ProductCategoryService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductCategoryRepository productCategoryRepository;


    @Override
    public void assignCategoriesToProduct(Long productId, List<Long> categoryIds) {

        if (productId == null || productId <= 0) {
            throw new IllegalArgumentException("Product ID must be a positive number");
        }

        if (categoryIds == null || categoryIds.isEmpty()) {
            throw new IllegalArgumentException("At least one category ID is required");
        }

        if (categoryIds.stream().anyMatch(id -> id == null || id <= 0)) {
            throw new IllegalArgumentException(
                    "Category IDs must be positive numbers");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Product not found with ID: " + productId));

        for (Long categoryId : categoryIds) {
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Category not found with ID: " + categoryId));

            boolean alreadyAssigned =
                    productCategoryRepository
                            .existsByIdProductIdAndIdCategoryId(productId, categoryId);

            if (!alreadyAssigned) {
                ProductCategory mapping = new ProductCategory();
                mapping.setId(new ProductCategoryId(productId, categoryId));
                mapping.setProduct(product);
                mapping.setCategory(category);
                productCategoryRepository.save(mapping);
            }
        }
    }


    @Override
    @Transactional(readOnly = true)
    public List<Category> getCategoriesByProductId(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new IllegalArgumentException(
                    "Product not found with ID: " + productId);
        }

        return productCategoryRepository.findByIdProductId(productId)
                .stream()
                .map(ProductCategory::getCategory)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> getProductsByCategoryId(Long categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new IllegalArgumentException(
                    "Category not found with ID: " + categoryId);
        }

        return productCategoryRepository.findByIdCategoryId(categoryId)
                .stream()
                .map(ProductCategory::getProduct)
                .toList();
    }

    @Override
    public void removeCategoryFromProduct(Long productId, Long categoryId) {
        ProductCategoryId id = new ProductCategoryId(productId, categoryId);

        if (!productCategoryRepository.existsById(id)) {
            throw new IllegalArgumentException(
                    "Product-category mapping not found for product ID "
                            + productId + " and category ID " + categoryId);
        }

        productCategoryRepository.deleteById(id);
    }
}
