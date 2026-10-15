
package com.shopsphere.service;

import com.shopsphere.entity.Category;
import com.shopsphere.entity.Product;

import java.util.List;

public interface ProductCategoryService {

    void assignCategoriesToProduct(Long productId, List<Long> categoryIds);

    List<Category> getCategoriesByProductId(Long productId);

    List<Product> getProductsByCategoryId(Long categoryId);

    void removeCategoryFromProduct(Long productId, Long categoryId);
}
