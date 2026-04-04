package com.jackalcode.ecommerceapi.services;

import com.jackalcode.ecommerceapi.dtos.requests.CategoryRequest;
import com.jackalcode.ecommerceapi.entities.Category;

import java.util.List;

public interface CategoryService {

    List<Category> getAllCategories();

    Category createCategory(CategoryRequest categoryRequest);

    Category updateCategory(Long id, CategoryRequest category);
}
