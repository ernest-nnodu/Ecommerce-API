package com.jackalcode.ecommerce_store.product;

import com.jackalcode.ecommerce_store.category.Category;
import com.jackalcode.ecommerce_store.exceptions.CategoryNotFoundException;
import com.jackalcode.ecommerce_store.exceptions.ProductAlreadyExistException;
import com.jackalcode.ecommerce_store.exceptions.ProductNotFoundException;
import com.jackalcode.ecommerce_store.category.CategoryRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;

    @Override
    public List<ProductResponse> getProducts() {

        List<Product> products = productRepository.findAll();

        return products.stream()
                .map(productMapper::toProductResponse)
                .toList();
    }

    @Override
    public ProductResponse getProduct(Long id) {

        return productMapper.toProductResponse(getProductEntity(id));
    }

    @Override
    @Transactional
    public ProductResponse createProduct(ProductRequest productRequest) {

        Category category = getCategory(productRequest.categoryId());

        //Check if the product already exists in the database
        checkProductAlreadyExist(productRequest.name(), category);

        //Create a new product and save to the database
        Product newProduct = productMapper.toProduct(productRequest);
        newProduct.setCategory(category);
        return productMapper.toProductResponse(productRepository.save(newProduct));
    }

    @Override
    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest productRequest) {

        Product existingProduct = getProductEntity(id);
        Category category = getCategory(productRequest.categoryId());

        //If the product name needs updating, check if the name already exists in the database
        if (!existingProduct.getName().equalsIgnoreCase(productRequest.name())) {
            checkProductAlreadyExist(productRequest.name(), category);
        }

        //Update product and save to the database
        productMapper.updateProduct(productRequest, existingProduct);
        existingProduct.setCategory(category);

        return productMapper.toProductResponse(existingProduct);
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {

        Product existingProduct = getProductEntity(id);
        productRepository.delete(existingProduct);
    }

    @Override
    public List<ProductResponse> searchProducts(ProductFilter productFilter) {

        var specification = Specification.allOf(
                ProductSpecifications.hasName(productFilter.name())
                        .and(ProductSpecifications.hasMinPrice(productFilter.minPrice()))
                        .and(ProductSpecifications.hasMaxPrice(productFilter.maxPrice()))
                        .and(ProductSpecifications.hasCategoryId(productFilter.categoryId()))
        );

        return productRepository.findAll(specification)
                .stream()
                .map(productMapper::toProductResponse)
                .toList();
    }

    private Category getCategory(Long categoryId) {

        return categoryRepository.findById(categoryId).orElseThrow(
                () -> new CategoryNotFoundException("Category not found with id: " + categoryId)
        );
    }

    private void checkProductAlreadyExist(String productName, Category category) {
        if (productRepository.existsByNameIgnoreCaseAndCategory(productName, category)) {
            throw new ProductAlreadyExistException("Product already exist with name: " + productName);
        }
    }

    private Product getProductEntity(Long id) {
        return productRepository.findById(id).orElseThrow(
                () -> new ProductNotFoundException("Product not found with id: " + id)
        );
    }
}
