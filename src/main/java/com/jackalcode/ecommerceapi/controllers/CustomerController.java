package com.jackalcode.ecommerceapi.controllers;

import com.jackalcode.ecommerceapi.dtos.requests.RegisterCustomerRequest;
import com.jackalcode.ecommerceapi.dtos.requests.UpdateCustomerRequest;
import com.jackalcode.ecommerceapi.dtos.responses.CustomerResponse;
import com.jackalcode.ecommerceapi.services.CustomerService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customers")
@AllArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping
    public ResponseEntity<List<CustomerResponse>> getAllCustomers() {
        return ResponseEntity.ok(customerService.getAllCustomers());
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<CustomerResponse> getCustomerById(
            @PathVariable(name = "id") Long customerId) {
        return ResponseEntity.ok(customerService.getCustomerById(customerId));
    }

    @PostMapping
    public ResponseEntity<CustomerResponse> registerCustomer(
            @RequestBody RegisterCustomerRequest registerCustomerRequest) {

        return new ResponseEntity<>(customerService.registerCustomer(registerCustomerRequest),
                HttpStatus.CREATED);
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<CustomerResponse> updateCustomer(
            @PathVariable(name = "id") Long customerId,
            @RequestBody UpdateCustomerRequest updateCustomerRequest) {

        return ResponseEntity.ok(customerService.updateCustomer(customerId, updateCustomerRequest));
    }
}
