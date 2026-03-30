package com.jackalcode.ecommerceapi.services.impl;

import com.jackalcode.ecommerceapi.dtos.responses.ProductResponse;
import com.jackalcode.ecommerceapi.entities.Product;
import com.jackalcode.ecommerceapi.exceptions.ProductNotFoundException;
import com.jackalcode.ecommerceapi.mappers.ProductMapper;
import com.jackalcode.ecommerceapi.repositories.ProductRepository;
import com.jackalcode.ecommerceapi.services.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
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

    private Product getProductEntity(Long id) {
        return productRepository.findById(id).orElseThrow(
                () -> new ProductNotFoundException("Product not found with id: " + id)
        );
    }
}
