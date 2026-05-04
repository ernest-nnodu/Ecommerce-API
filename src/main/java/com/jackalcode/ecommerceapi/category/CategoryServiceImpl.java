package com.jackalcode.ecommerceapi.category;

import com.jackalcode.ecommerceapi.exceptions.CategoryAlreadyExistsException;
import com.jackalcode.ecommerceapi.exceptions.CategoryNotFoundException;
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
    public List<Category> getCategories() {
        return categoryRepository.findAll();
    }

    @Override
    @Transactional
    public Category createCategory(CategoryRequest categoryRequest) {

        //Check if category already exist in the database
        checkCategoryNameExists(categoryRequest.name());

        //Create new category and save to database
        Category category = categoryMapper.toCategory(categoryRequest);

        return categoryRepository.save(category);
    }

    @Override
    @Transactional
    public Category updateCategory(Long id, CategoryRequest categoryRequest) {

        //Check category id is valid
        Category exisitingCategory = categoryRepository.findById(id).orElseThrow(
                () -> new CategoryNotFoundException("Category not found with id: " + id));

        //Check if update category name already exist in the database
        checkCategoryNameExists(categoryRequest.name());

        //Update exisiting category
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
