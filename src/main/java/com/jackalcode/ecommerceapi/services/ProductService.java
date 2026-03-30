package com.jackalcode.ecommerceapi.services;

import com.jackalcode.ecommerceapi.dtos.responses.ProductResponse;

import java.util.List;

public interface ProductService {

    List<ProductResponse> getProducts();
}
