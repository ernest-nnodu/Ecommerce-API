package com.jackalcode.ecommerce_store.cart;

import com.jackalcode.ecommerce_store.category.Category;
import com.jackalcode.ecommerce_store.category.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void findByNameIgnoreCase_whenNameExists_returnsTrue() {
        var category = createCategoryEntity();

        persistToDatabase(category);

        var response = categoryRepository.existsByNameIgnoreCase(category.getName());

        assertTrue(response);
    }

    @Test
    void findByNameIgnoreCase_whenNameNotExists_returnsFalse() {

        var category = createCategoryEntity();
        persistToDatabase(category);

        var response = categoryRepository.existsByNameIgnoreCase("Fake category");

        assertFalse(response);
    }

    private void persistToDatabase(Category category) {

        entityManager.persist(category);
        entityManager.flush();
        entityManager.clear();
    }

    private Category createCategoryEntity() {

        Category category = new Category();
        category.setName("Test Category");

        return category;
    }
}
