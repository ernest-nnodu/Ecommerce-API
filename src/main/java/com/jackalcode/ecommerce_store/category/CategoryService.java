package com.jackalcode.ecommerce_store.category;

import java.util.List;

public interface CategoryService {

    List<Category> getCategories();

    Category createCategory(CategoryRequest categoryRequest);

    Category updateCategory(Long id, CategoryRequest category);
}
