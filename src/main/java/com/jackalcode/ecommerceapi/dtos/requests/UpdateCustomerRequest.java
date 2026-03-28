package com.jackalcode.ecommerceapi.dtos.requests;

public record UpdateCustomerRequest(
        String firstName,
        String lastName,
        String email
) {
}
