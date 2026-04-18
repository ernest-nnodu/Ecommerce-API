package com.jackalcode.ecommerceapi.customer;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customers")
@AllArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping(path = "/me")
    public ResponseEntity<CustomerResponse> getCustomer() {
        return ResponseEntity.ok(customerService.getCustomer());
    }

    @PostMapping
    public ResponseEntity<CustomerResponse> registerCustomer(
            @Valid @RequestBody RegisterCustomerRequest registerCustomerRequest) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(customerService.registerCustomer(registerCustomerRequest));
    }

    @PutMapping(path = "/me")
    public ResponseEntity<CustomerResponse> updateCustomer(
            @Valid @RequestBody UpdateCustomerRequest updateCustomerRequest) {

        return ResponseEntity.ok(customerService.updateCustomer(updateCustomerRequest));
    }
}
