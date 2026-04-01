package com.jackalcode.ecommerceapi.services.impl;

import com.jackalcode.ecommerceapi.dtos.requests.ProductRequest;
import com.jackalcode.ecommerceapi.dtos.responses.ProductResponse;
import com.jackalcode.ecommerceapi.entities.Category;
import com.jackalcode.ecommerceapi.entities.Product;
import com.jackalcode.ecommerceapi.exceptions.CategoryNotFoundException;
import com.jackalcode.ecommerceapi.exceptions.ProductAlreadyExistException;
import com.jackalcode.ecommerceapi.exceptions.ProductNotFoundException;
import com.jackalcode.ecommerceapi.mappers.ProductMapper;
import com.jackalcode.ecommerceapi.repositories.CategoryRepository;
import com.jackalcode.ecommerceapi.repositories.ProductRepository;
import com.jackalcode.ecommerceapi.services.ProductService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

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

        checkProductAlreadyExist(productRequest.name());

        Category category = getCategory(productRequest.categoryId());

        Product newProduct = productMapper.toProduct(productRequest);
        newProduct.setCategory(category);

        return productMapper.toProductResponse(productRepository.save(newProduct));
    }

    @Override
    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest productRequest) {

        Product existingProduct = getProductEntity(id);

        if (!existingProduct.getName().equalsIgnoreCase(productRequest.name())) {
            checkProductAlreadyExist(productRequest.name());
        }

        Category category = getCategory(productRequest.categoryId());
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

    private Category getCategory(Long categoryId) {

        return categoryRepository.findById(categoryId).orElseThrow(
                () -> new CategoryNotFoundException("Category not found with id: " + categoryId)
        );
    }

    private void checkProductAlreadyExist(String productName) {
        if (productRepository.existsByNameIgnoreCase(productName)) {
            throw new ProductAlreadyExistException("Product already exist with name: " + productName);
        }
    }

    private Product getProductEntity(Long id) {
        return productRepository.findById(id).orElseThrow(
                () -> new ProductNotFoundException("Product not found with id: " + id)
        );
    }
}
