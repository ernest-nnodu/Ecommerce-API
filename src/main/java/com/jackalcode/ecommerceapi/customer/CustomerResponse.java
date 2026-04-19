package com.jackalcode.ecommerceapi.customer;

public record CustomerResponse(
        long id,
        String firstName,
        String lastName,
        String email
) {
}
