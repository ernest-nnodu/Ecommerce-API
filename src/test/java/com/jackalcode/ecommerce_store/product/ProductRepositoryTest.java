package com.jackalcode.ecommerce_store.product;

import com.jackalcode.ecommerce_store.category.Category;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("existsByNameIgnoreCase should return true when name exists in the database")
    void existsByNameIgnoreCase_whenNameExists_returnsTrue() {

        var category = createCategory("category");

        var product = createProduct("Test Product", category);

        persistToDatabase(category);
        persistToDatabase(product);

        var response = productRepository.existsByNameIgnoreCaseAndCategory(
                "test product", category);

        assertTrue(response);

    }

    @Test
    @DisplayName("existsByNameIgnoreCase should return false when name does not exist in the database")
    void existsByNameIgnoreCase_whenNameNotExists_returnsFalse() {

        Category fakeCategory = new Category();
        fakeCategory.setName("Fake Category");

        persistToDatabase(fakeCategory);

        var response = productRepository.existsByNameIgnoreCaseAndCategory(
                "Fake Product", fakeCategory);

        assertFalse(response);
    }

    @Test
    @DisplayName("existsByNameIgnoreCase should return false when name exists but in a different category")
    void existsByNameIgnoreCase_whenNameExistsButDifferentCategory_returnsFalse() {
        Category category1 = createCategory("Electronics");
        Category category2 = createCategory("Books");

        Product product = createProduct("Test product", category1);

        persistToDatabase(category1);
        persistToDatabase(category2);
        persistToDatabase(product);

        var response = productRepository.existsByNameIgnoreCaseAndCategory(
                "Test Product", category2);

        assertFalse(response);


    }

    @Test
    @DisplayName("findAll should return all products and their categories")
    void findAll_whenProductsExist_returnsListOfProductsAndCategories() {
        Category category1 = createCategory("Electronics");
        Category category2 = createCategory("Books");

        persistToDatabase(category1);
        persistToDatabase(category2);

        Product product1 = createProduct("Product 1", category1);
        Product product2 = createProduct("Product 2", category2);
        Product product3 = createProduct("Product 3", category1);
        Product product4 = createProduct("Product 4", category2);

        persistToDatabase(product1);
        persistToDatabase(product2);
        persistToDatabase(product3);
        persistToDatabase(product4);

        var products = productRepository.findAll();

        assertAll(
                () -> assertEquals(4, products.size()),
                () -> assertTrue(
                        products.stream().anyMatch(p -> p.getName().equals("Product 1")
                                && p.getCategory().getName().equals("Electronics"))),
                () -> assertTrue(
                        products.stream().anyMatch(p -> p.getName().equals("Product 2")
                                && p.getCategory().getName().equals("Books"))),
                () -> assertTrue(
                        products.stream().anyMatch(p -> p.getName().equals("Product 3")
                                && p.getCategory().getName().equals("Electronics"))),
                () -> assertTrue(
                        products.stream().anyMatch(p -> p.getName().equals("Product 4")
                                && p.getCategory().getName().equals("Books")))
        );
    }

    @Test
    @DisplayName("findAll should return empty list when no products exist")
    void findAll_whenNoProductsExist_returnsEmptyList() {

        var products = productRepository.findAll();

        assertTrue(products.isEmpty());
    }

    private void persistToDatabase(Object entity) {
        entityManager.persist(entity);
        entityManager.flush();
        entityManager.clear();
    }

    private Product createProduct(String name, Category category) {
        var product = new Product();
        product.setName(name);
        product.setPrice(BigDecimal.valueOf(10.0));
        product.setQuantityInStock(10L);
        product.setCategory(category);
        return product;
    }

    private Category createCategory(String name) {
        var category = new Category();
        category.setName(name);
        return category;
    }
}
