package com.jackalcode.ecommerce_store.product;

import com.jackalcode.ecommerce_store.category.Category;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void existsByNameIgnoreCase_whenNameExists_returnsTrue() {

        var category = createCategory();

        var product = createProduct(category);

        persistToDatabase(category);
        persistToDatabase(product);

        var response = productRepository.existsByNameIgnoreCaseAndCategory(
                "test product", category);

        assertTrue(response);

    }

    @Test
    void existsByNameIgnoreCase_whenNameNotExists_returnsFalse() {

        Category fakeCategory = new Category();
        fakeCategory.setName("Fake Category");

        persistToDatabase(fakeCategory);

        var response = productRepository.existsByNameIgnoreCaseAndCategory(
                "Fake Product", fakeCategory);

        assertFalse(response);
    }

    private void persistToDatabase(Object entity) {
        entityManager.persist(entity);
        entityManager.flush();
        entityManager.clear();
    }

    private Product createProduct(Category category) {
        var product = new Product();
        product.setName("Test Product");
        product.setPrice(BigDecimal.valueOf(10.0));
        product.setQuantityInStock(10L);
        product.setCategory(category);
        return product;
    }

    private Category createCategory() {
        var category = new Category();
        category.setName("Test Category");
        return category;
    }
}
