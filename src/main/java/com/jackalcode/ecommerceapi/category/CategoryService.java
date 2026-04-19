package com.jackalcode.ecommerceapi.category;

import java.util.List;

public interface CategoryService {

    List<Category> getCategories();

    Category createCategory(CategoryRequest categoryRequest);

    Category updateCategory(Long id, CategoryRequest category);
}
