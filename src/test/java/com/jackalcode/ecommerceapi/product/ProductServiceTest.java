package com.jackalcode.ecommerceapi.product;

import com.jackalcode.ecommerceapi.category.Category;
import com.jackalcode.ecommerceapi.category.CategoryRepository;
import com.jackalcode.ecommerceapi.exceptions.CategoryNotFoundException;
import com.jackalcode.ecommerceapi.exceptions.ProductAlreadyExistException;
import com.jackalcode.ecommerceapi.exceptions.ProductNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    private final ProductMapper productMapper = Mappers.getMapper(ProductMapper.class);

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
    void getProducts_whenNoProducts_returnsEmptyList() {

        when(productRepository.findAll()).thenReturn(List.of());

        List<ProductResponse> results = productService.getProducts();

        assertNotNull(results);
        assertTrue(results.isEmpty(), "Expected empty product list");

        verify(productRepository).findAll();
    }

    @Test
    @DisplayName("getProduct: returns mapped ProductResponse when product exists")
    void getProduct_whenProductExists_returnsMappedProductResponse() {

        Long productId = 1L;
        Category category = createCategory(10L, "Electronics");
        Product product = createProductEntity(productId, "Phone", new BigDecimal("199.99"),
                category, 10L);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        ProductResponse result = productService.getProduct(productId);

        // Assert
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(productId, result.id()),
                () -> assertEquals("Phone", result.name()),
                () -> assertEquals(new BigDecimal("199.99"), result.price()),
                () -> assertEquals(10L, result.categoryId()),
                () -> assertEquals("Electronics", result.categoryName())
        );

        verify(productRepository).findById(productId);
    }

    @Test
    @DisplayName("getProduct: throws ProductNotFoundException when product does not exist")
    void getProduct_whenProductNotFound_throwsProductNotFoundException() {

        Long missingId = 999L;
        when(productRepository.findById(missingId)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class,
                () -> productService.getProduct(missingId));

        verify(productRepository).findById(missingId);
    }

    @Test
    @DisplayName("createProduct: saves and returns ProductResponse when valid request")
    void createProduct_withValidRequest_savesAndReturnsResponse() {

        Category category = createCategory(10L, "Electronics");
        ProductRequest request = new ProductRequest("Phone", new BigDecimal("199.99"),
                10L, 10L);

        when(productRepository.existsByNameIgnoreCase("Phone")).thenReturn(false);
        when(categoryRepository.findById(10L)).thenReturn(Optional.of(category));

        // simulate DB assigning id on save
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
            Product p = invocation.getArgument(0);
            p.setId(1L);
            return p;
        });

        ProductResponse response = productService.createProduct(request);

        assertAll(
                () -> assertNotNull(response),
                () -> assertEquals(1L, response.id()),
                () -> assertEquals("Phone", response.name()),
                () -> assertEquals(new BigDecimal("199.99"), response.price()),
                () -> assertEquals(10L, response.categoryId()),
                () -> assertEquals("Electronics", response.categoryName())
        );

        verify(productRepository).existsByNameIgnoreCase("Phone");
        verify(categoryRepository).findById(10L);
        verify(productRepository).save(any(Product.class));
    }

    @Test
    @DisplayName("createProduct: throws ProductAlreadyExistException when name already exists")
    void createProduct_whenNameExists_throwsException() {

        ProductRequest request = new ProductRequest("Phone", new BigDecimal("199.99"), 10L, 10L);

        when(productRepository.existsByNameIgnoreCase("Phone")).thenReturn(true);

        assertThrows(ProductAlreadyExistException.class,
                () -> productService.createProduct(request));

        verify(productRepository).existsByNameIgnoreCase("Phone");
        verify(categoryRepository, never()).findById(anyLong());
        verify(productRepository, never()).save(any());
    }

    @Test
    @DisplayName("createProduct: throws CategoryNotFoundException when category does not exist")
    void createProduct_whenCategoryNotFound_throwsException() {

        ProductRequest request = new ProductRequest("Phone", new BigDecimal("199.99"), 10L, 999L);

        when(productRepository.existsByNameIgnoreCase("Phone")).thenReturn(false);
        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(CategoryNotFoundException.class,
                () -> productService.createProduct(request));

        verify(productRepository).existsByNameIgnoreCase("Phone");
        verify(categoryRepository).findById(999L);
        verify(productRepository, never()).save(any());
    }

    @Test
    @DisplayName("updateProduct: updates existing product when name changed and category exists")
    void updateProduct_withValidRequest_updatesAndReturnsResponse() {

        Long productId = 1L;
        Category oldCategory = createCategory(10L, "Electronics");
        Product existing = createProductEntity(productId, "OldPhone",
                new BigDecimal("199.99"), oldCategory, 10L);

        Category newCategory = createCategory(99L, "Gadgets");
        ProductRequest request = new ProductRequest("NewPhone", new BigDecimal("299.99"),
                5L, 99L);

        when(productRepository.findById(productId)).thenReturn(Optional.of(existing));
        // because name is changing, service should check for existence
        when(productRepository.existsByNameIgnoreCase("NewPhone")).thenReturn(false);
        when(categoryRepository.findById(99L)).thenReturn(Optional.of(newCategory));

        ProductResponse result = productService.updateProduct(productId, request);

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(productId, result.id()),
                () -> assertEquals("NewPhone", result.name()),
                () -> assertEquals(new BigDecimal("299.99"), result.price()),
                () -> assertEquals(99L, result.categoryId()),
                () -> assertEquals("Gadgets", result.categoryName())
        );

        verify(productRepository).findById(productId);
        verify(productRepository).existsByNameIgnoreCase("NewPhone");
        verify(categoryRepository).findById(99L);
    }

    @Test
    @DisplayName("updateProduct: throws ProductNotFoundException when product id not found")
    void updateProduct_whenProductNotFound_throwsProductNotFoundException() {

        Long missingId = 999L;
        ProductRequest request = new ProductRequest("Whatever", new BigDecimal("10.00"),
                1L, 1L);

        when(productRepository.findById(missingId)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class,
                () -> productService.updateProduct(missingId, request));

        verify(productRepository).findById(missingId);
        verify(categoryRepository, never()).findById(anyLong());
    }

    @Test
    @DisplayName("updateProduct: throws ProductAlreadyExistException when new name already exists")
    void updateProduct_whenNameExists_throwsProductAlreadyExistException() {

        Long productId = 1L;
        Category oldCategory = createCategory(10L, "Electronics");
        Product existing = createProductEntity(productId, "OldPhone",
                new BigDecimal("199.99"), oldCategory, 10L);

        ProductRequest request = new ProductRequest("ConflictingName",
                new BigDecimal("299.99"), 5L, 10L);

        when(productRepository.findById(productId)).thenReturn(Optional.of(existing));
        when(productRepository.existsByNameIgnoreCase("ConflictingName")).thenReturn(true);

        assertThrows(ProductAlreadyExistException.class,
                () -> productService.updateProduct(productId, request));

        verify(productRepository).findById(productId);
        verify(productRepository).existsByNameIgnoreCase("ConflictingName");
        verify(categoryRepository, never()).findById(anyLong());
    }

    @Test
    @DisplayName("updateProduct: throws CategoryNotFoundException when new category id does not exist")
    void updateProduct_whenCategoryNotFound_throwsCategoryNotFoundException() {

        Long productId = 1L;
        Category oldCategory = createCategory(10L, "Electronics");
        Product existing = createProductEntity(productId, "OldPhone", new BigDecimal("199.99"), oldCategory, 10L);

        ProductRequest request = new ProductRequest("NewPhone", new BigDecimal("299.99"),
                5L, 12345L);

        when(productRepository.findById(productId)).thenReturn(Optional.of(existing));
        when(productRepository.existsByNameIgnoreCase("NewPhone")).thenReturn(false);
        when(categoryRepository.findById(12345L)).thenReturn(Optional.empty());

        assertThrows(CategoryNotFoundException.class,
                () -> productService.updateProduct(productId, request));

        verify(productRepository).findById(productId);
        verify(productRepository).existsByNameIgnoreCase("NewPhone");
        verify(categoryRepository).findById(12345L);
        verify(productRepository, never()).save(any());
    }

    @Test
    @DisplayName("searchProducts: returns mapped ProductResponse list when specification matches")
    void searchProducts_returnsMappedProductResponses() {

        Category category1 = createCategory(10L, "Electronics");
        Product product1 = createProductEntity(1L, "Phone", new BigDecimal("199.99"),
                category1, 10L);

        Category category2 = createCategory(20L, "Books");
        Product product2 = createProductEntity(2L, "Novel", new BigDecimal("9.99"),
                category2, 20L);

        ProductFilter filter = new ProductFilter("Phone", new BigDecimal("100"),
                new BigDecimal("300"), 10L);

        // Mock repository to return matching products regardless of the Specification instance
        when(productRepository.findAll(any(Specification.class)))
                .thenReturn(List.of(product1, product2));

        List<ProductResponse> results = productService.searchProducts(filter);

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

        verify(productRepository).findAll(any(Specification.class));
    }

    @Test
    @DisplayName("searchProducts: returns empty list when specification matches no products")
    void searchProducts_whenNoMatches_returnsEmptyList() {

        ProductFilter filter = new ProductFilter("NonExisting", null, null, null);

        when(productRepository.findAll(any(Specification.class)))
                .thenReturn(List.of());

        List<ProductResponse> results = productService.searchProducts(filter);

        assertNotNull(results);
        assertTrue(results.isEmpty(), "Expected empty search result list");

        verify(productRepository).findAll(any(Specification.class));
        verifyNoMoreInteractions(productRepository);
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
