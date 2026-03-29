package com.jackalcode.ecommerceapi.services.impl;

import com.jackalcode.ecommerceapi.entities.Category;
import com.jackalcode.ecommerceapi.repositories.CategoryRepository;
import com.jackalcode.ecommerceapi.services.CategoryService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }
}
