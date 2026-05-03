package com.jackalcode.ecommerceapi.product;

import java.util.List;

public interface ProductService {

    List<ProductResponse> getProducts();

    ProductResponse getProduct(Long id);

    ProductResponse createProduct(ProductRequest productRequest);

    ProductResponse updateProduct(Long id, ProductRequest productRequest);

    void deleteProduct(Long id);

    List<ProductResponse> searchProducts(ProductFilter productFilter);
}
