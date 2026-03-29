package com.jackalcode.ecommerceapi.services;

import com.jackalcode.ecommerceapi.dtos.requests.CreateCategoryRequest;
import com.jackalcode.ecommerceapi.entities.Category;

import java.util.List;

public interface CategoryService {

    List<Category> getAllCategories();

    Category createCategory(CreateCategoryRequest createCategoryRequest);
}
