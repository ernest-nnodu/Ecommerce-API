package com.jackalcode.ecommerceapi.dtos.requests;

public record RegisterCustomerRequest(
        String firstName,
        String lastName,
        String email,
        String password
) {
}
