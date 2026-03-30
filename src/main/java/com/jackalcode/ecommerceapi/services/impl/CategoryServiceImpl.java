package com.jackalcode.ecommerceapi.services.impl;

import com.jackalcode.ecommerceapi.dtos.requests.CategoryRequest;
import com.jackalcode.ecommerceapi.entities.Category;
import com.jackalcode.ecommerceapi.exceptions.CategoryAlreadyExistsException;
import com.jackalcode.ecommerceapi.exceptions.CategoryNotFoundException;
import com.jackalcode.ecommerceapi.mappers.CategoryMapper;
import com.jackalcode.ecommerceapi.repositories.CategoryRepository;
import com.jackalcode.ecommerceapi.services.CategoryService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    @Transactional
    public Category createCategory(CategoryRequest categoryRequest) {

        checkCategoryNameExists(categoryRequest.name());

        Category category = categoryMapper.toCategory(categoryRequest);
        categoryRepository.save(category);

        return category;
    }

    @Override
    @Transactional
    public Category updateCategory(Long id, CategoryRequest categoryRequest) {

        Category exisitingCategory = categoryRepository.findById(id).orElseThrow(
                () -> new CategoryNotFoundException("Category not found with id: " + id));

        checkCategoryNameExists(categoryRequest.name());
        categoryMapper.updateCategory(categoryRequest, exisitingCategory);

        return exisitingCategory;
    }

    private void checkCategoryNameExists(String categoryName) {
        if (categoryRepository.existsByNameIgnoreCase(categoryName)) {
            throw new CategoryAlreadyExistsException("Category already exists with name: " +
                    categoryName);
        }
    }
}
