
package com.shopsphere.service;

import com.shopsphere.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface ProductService {

    Product createProduct(Product product);

    List<Product> getAllProducts();

    Page<Product> getAllProducts(Pageable pageable);

    Page<Product> searchProducts(
            String name,
            String status,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Pageable pageable
    );

    Product getProductById(Long id);

    Product updateProduct(Long id, Product product);

    void deleteProduct(Long id);
}
