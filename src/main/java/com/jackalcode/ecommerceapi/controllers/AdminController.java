package com.jackalcode.ecommerceapi.controllers;

import com.jackalcode.ecommerceapi.dtos.requests.CategoryRequest;
import com.jackalcode.ecommerceapi.dtos.responses.CustomerResponse;
import com.jackalcode.ecommerceapi.entities.Category;
import com.jackalcode.ecommerceapi.services.CategoryService;
import com.jackalcode.ecommerceapi.services.CustomerService;
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
}
