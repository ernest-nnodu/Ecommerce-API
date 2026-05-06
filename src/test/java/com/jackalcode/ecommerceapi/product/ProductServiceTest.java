package com.jackalcode.ecommerceapi.product;

import com.jackalcode.ecommerceapi.category.Category;
import com.jackalcode.ecommerceapi.category.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    private ProductMapper productMapper = Mappers.getMapper(ProductMapper.class);

    @BeforeEach
    void setUp() {
        productService = new ProductServiceImpl(
                productRepository,
                categoryRepository,
                productMapper);
    }

    @Test
    @DisplayName("getProducts: returns mapped ProductResponse list when products exist")
    void getProducts_returnsMappedProductResponses() {

        Category category1 = createCategory(10L, "Electronics");

        Product product1 = createProductEntity(1L, "Phone", new BigDecimal("199.99"),
                category1, 10L);

        Category category2 = createCategory(20L, "Books");

        Product product2 = createProductEntity(2L, "Novel", new BigDecimal("9.99"),
                category2, 20L);

        when(productRepository.findAll()).thenReturn(List.of(product1, product2));

        List<ProductResponse> results = productService.getProducts();

        assertAll(
                () -> assertNotNull(results),
                () -> assertEquals(2, results.size()),
                () -> assertEquals(1L, results.get(0).id()),
                () -> assertEquals("Phone", results.get(0).name()),
                () -> assertEquals(new BigDecimal("199.99"), results.get(0).price()),
                () -> assertEquals(10L, results.get(0).categoryId()),
                () -> assertEquals("Electronics", results.get(0).categoryName()),
                () -> assertEquals(2L, results.get(1).id()),
                () -> assertEquals("Novel", results.get(1).name())
        );

        verify(productRepository).findAll();
    }

    @Test
    @DisplayName("getProducts: returns empty list when no products available")
    void getProducts_returnsEmptyList_whenNoProducts() {

        when(productRepository.findAll()).thenReturn(List.of());

        List<ProductResponse> results = productService.getProducts();

        assertNotNull(results);
        assertTrue(results.isEmpty(), "Expected empty product list");

        verify(productRepository).findAll();
    }

    private Product createProductEntity(Long id, String name, BigDecimal price, Category category,
                                        Long quantityInStock) {
        Product product = new Product();
        product.setId(id);
        product.setName(name);
        product.setPrice(price);
        product.setCategory(category);
        product.setQuantityInStock(quantityInStock);

        return product;
    }

    private Category createCategory(Long id, String name) {
        Category category = new Category();
        category.setId(id);
        category.setName(name);

        return category;
    }

}
