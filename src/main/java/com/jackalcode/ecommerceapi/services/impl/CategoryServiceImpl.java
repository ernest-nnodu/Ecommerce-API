package com.jackalcode.ecommerceapi.services.impl;

import com.jackalcode.ecommerceapi.dtos.requests.CreateCategoryRequest;
import com.jackalcode.ecommerceapi.entities.Category;
import com.jackalcode.ecommerceapi.mappers.CategoryMapper;
import com.jackalcode.ecommerceapi.mappers.CustomerMapper;
import com.jackalcode.ecommerceapi.repositories.CategoryRepository;
import com.jackalcode.ecommerceapi.services.CategoryService;
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
    public Category createCategory(CreateCategoryRequest createCategoryRequest) {

        Category category = categoryMapper.toCategory(createCategoryRequest);
        categoryRepository.save(category);

        return category;
    }
}
