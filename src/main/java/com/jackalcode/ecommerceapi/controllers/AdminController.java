package com.jackalcode.ecommerceapi.controllers;

import com.jackalcode.ecommerceapi.dtos.responses.CustomerResponse;
import com.jackalcode.ecommerceapi.services.CustomerService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/admin")
@AllArgsConstructor
public class AdminController {

    private final CustomerService customerService;

    @GetMapping(path = "/customers")
    public ResponseEntity<List<CustomerResponse>> getCustomers() {

        return ResponseEntity.ok(customerService.getCustomers());
    }

    @GetMapping(path = "/customers/{id}")
    public ResponseEntity<CustomerResponse> getCustomer(@PathVariable(name = "id") Long customerId) {

        return ResponseEntity.ok(customerService.getCustomerById(customerId));
    }
}
