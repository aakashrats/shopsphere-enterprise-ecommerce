
package com.shopsphere.service.impl;

import com.shopsphere.entity.Category;
import com.shopsphere.repository.CategoryRepository;
import com.shopsphere.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public Category createCategory(Category category) {
        if (categoryRepository.existsBySlug(category.getSlug())) {
            throw new IllegalArgumentException(
                    "Category slug already exists: " + category.getSlug()
            );
        }

        return categoryRepository.save(category);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Category not found with ID: " + id
                ));
    }

    @Override
    public Category updateCategory(Long id, Category updatedCategory) {
        Category existingCategory = getCategoryById(id);

        categoryRepository.findBySlug(updatedCategory.getSlug())
                .filter(category -> !category.getId().equals(id))
                .ifPresent(category -> {
                    throw new IllegalArgumentException(
                            "Category slug already exists: "
                                    + updatedCategory.getSlug()
                    );
                });

        existingCategory.setName(updatedCategory.getName());
        existingCategory.setSlug(updatedCategory.getSlug());
        existingCategory.setDescription(updatedCategory.getDescription());
        existingCategory.setStatus(updatedCategory.getStatus());

        return categoryRepository.save(existingCategory);
    }

    @Override
    public void deleteCategory(Long id) {
        Category category = getCategoryById(id);
        categoryRepository.delete(category);
    }
}
