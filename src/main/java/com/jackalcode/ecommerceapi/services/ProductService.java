package com.jackalcode.ecommerceapi.services;

import com.jackalcode.ecommerceapi.dtos.requests.ProductRequest;
import com.jackalcode.ecommerceapi.dtos.responses.ProductResponse;

import java.util.List;

public interface ProductService {

    List<ProductResponse> getProducts();

    ProductResponse getProduct(Long id);

    ProductResponse createProduct(ProductRequest productRequest);

    ProductResponse updateProduct(Long id, ProductRequest productRequest);
}
