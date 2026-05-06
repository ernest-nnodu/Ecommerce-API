package com.jackalcode.ecommerceapi.product;

import com.jackalcode.ecommerceapi.category.CategoryRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    private ProductMapper productMapper = Mappers.getMapper(ProductMapper.class);

    void setUp() {
        productService = new ProductServiceImpl(
                productRepository,
                categoryRepository,
                productMapper);
    }

}
