package com.jackalcode.ecommerceapi.product;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getProducts() {

        return ResponseEntity.ok(productService.getProducts());
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable(name = "id") Long productId) {
        return ResponseEntity.ok(productService.getProduct(productId));
    }

    @GetMapping(path = "/search")
    public ResponseEntity<List<ProductResponse>> searchProducts(@RequestBody ProductFilter productFilter) {

        return ResponseEntity.ok(productService.searchProducts(productFilter));
    }
}