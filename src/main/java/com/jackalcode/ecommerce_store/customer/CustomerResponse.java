package com.jackalcode.ecommerce_store.customer;

public record CustomerResponse(
        long id,
        String firstName,
        String lastName,
        String email
) {
}
