
package com.shopsphere.service.impl;

import com.shopsphere.entity.Product;
import com.shopsphere.repository.ProductRepository;
import com.shopsphere.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    public Product createProduct(Product product) {
        if (productRepository.existsBySlug(product.getSlug())) {
            throw new IllegalArgumentException(
                    "Product slug already exists: " + product.getSlug()
            );
        }

        if (productRepository.existsBySku(product.getSku())) {
            throw new IllegalArgumentException(
                    "Product SKU already exists: " + product.getSku()
            );
        }

        return productRepository.save(product);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Product> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Product not found with ID: " + id
                ));
    }

    @Override
    public Product updateProduct(Long id, Product updatedProduct) {
        Product existingProduct = getProductById(id);

        productRepository.findBySlug(updatedProduct.getSlug())
                .filter(product -> !product.getId().equals(id))
                .ifPresent(product -> {
                    throw new IllegalArgumentException(
                            "Product slug already exists: "
                                    + updatedProduct.getSlug()
                    );
                });

        productRepository.findBySku(updatedProduct.getSku())
                .filter(product -> !product.getId().equals(id))
                .ifPresent(product -> {
                    throw new IllegalArgumentException(
                            "Product SKU already exists: "
                                    + updatedProduct.getSku()
                    );
                });

        existingProduct.setName(updatedProduct.getName());
        existingProduct.setSlug(updatedProduct.getSlug());
        existingProduct.setDescription(updatedProduct.getDescription());
        existingProduct.setSku(updatedProduct.getSku());
        existingProduct.setPrice(updatedProduct.getPrice());
        existingProduct.setStockQuantity(updatedProduct.getStockQuantity());
        existingProduct.setStatus(updatedProduct.getStatus());

        return productRepository.save(existingProduct);
    }

    @Override
    public void deleteProduct(Long id) {
        Product product = getProductById(id);
        productRepository.delete(product);
    }
}
