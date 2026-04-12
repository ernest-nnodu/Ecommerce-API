package com.jackalcode.ecommerceapi.controllers;

import com.jackalcode.ecommerceapi.dtos.requests.CategoryRequest;
import com.jackalcode.ecommerceapi.dtos.requests.ProductRequest;
import com.jackalcode.ecommerceapi.dtos.responses.CustomerResponse;
import com.jackalcode.ecommerceapi.dtos.responses.OrderResponse;
import com.jackalcode.ecommerceapi.dtos.responses.ProductResponse;
import com.jackalcode.ecommerceapi.entities.Category;
import com.jackalcode.ecommerceapi.services.CategoryService;
import com.jackalcode.ecommerceapi.services.CustomerService;
import com.jackalcode.ecommerceapi.services.OrderService;
import com.jackalcode.ecommerceapi.services.ProductService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/admin")
@AllArgsConstructor
public class AdminController {

    private final CustomerService customerService;
    private final CategoryService categoryService;
    private final ProductService productService;
    private final OrderService orderService;

    @GetMapping(path = "/customers")
    public ResponseEntity<List<CustomerResponse>> getCustomers() {

        return ResponseEntity.ok(customerService.getCustomers());
    }

    @GetMapping(path = "/customers/{id}")
    public ResponseEntity<CustomerResponse> getCustomer(@PathVariable(name = "id") Long customerId) {

        return ResponseEntity.ok(customerService.getCustomerById(customerId));
    }

    @PostMapping(path = "/categories")
    public ResponseEntity<Category> createCategory(
            @Valid @RequestBody CategoryRequest categoryRequest) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(categoryService.createCategory(categoryRequest));
    }

    @PutMapping(path = "/categories/{id}")
    public ResponseEntity<Category> updateCategory(
            @PathVariable(name = "id") Long categoryId,
            @Valid @RequestBody CategoryRequest categoryRequest) {

        return ResponseEntity.ok(categoryService.updateCategory(categoryId, categoryRequest));
    }

    @PostMapping(path = "/products")
    public ResponseEntity<ProductResponse> createProduct(
            @Valid @RequestBody ProductRequest productRequest) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productService.createProduct(productRequest));
    }

    @PutMapping(path = "/products/{id}")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable(name = "id") Long productId,
            @Valid @RequestBody ProductRequest productRequest) {

        return ResponseEntity.ok(productService.updateProduct(productId, productRequest));
    }

    @DeleteMapping(path = "/products/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable(name = "id") Long productId) {

        productService.deleteProduct(productId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(path = "/orders")
    public ResponseEntity<List<OrderResponse>> getOrders() {

        return ResponseEntity.ok(orderService.getOrders());
    }

    @GetMapping(path = "/orders/{id}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable(name = "id") Long orderId) {

        return ResponseEntity.ok(orderService.getOrder(orderId));
    }
}
