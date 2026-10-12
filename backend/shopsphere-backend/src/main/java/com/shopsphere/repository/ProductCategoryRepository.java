
package com.shopsphere.repository;

import com.shopsphere.entity.ProductCategory;
import com.shopsphere.entity.ProductCategoryId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductCategoryRepository
        extends JpaRepository<ProductCategory, ProductCategoryId> {

    List<ProductCategory> findByIdCategoryId(Long categoryId);

    List<ProductCategory> findByIdProductId(Long productId);

    boolean existsByIdProductIdAndIdCategoryId(
            Long productId,
            Long categoryId
    );
}
