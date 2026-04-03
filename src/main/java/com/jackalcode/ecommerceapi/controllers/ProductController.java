package com.jackalcode.ecommerceapi.controllers;

import com.jackalcode.ecommerceapi.dtos.requests.ProductRequest;
import com.jackalcode.ecommerceapi.dtos.responses.ProductResponse;
import com.jackalcode.ecommerceapi.services.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(
            @Valid @RequestBody ProductRequest productRequest) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productService.createProduct(productRequest));
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable(name = "id") Long productId,
            @Valid @RequestBody ProductRequest productRequest) {

        return ResponseEntity.ok(productService.updateProduct(productId, productRequest));
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable(name = "id") Long productId) {

        productService.deleteProduct(productId);
        return ResponseEntity.noContent().build();
    }
}
