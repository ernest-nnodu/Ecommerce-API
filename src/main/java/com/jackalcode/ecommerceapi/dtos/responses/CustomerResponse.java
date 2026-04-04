package com.jackalcode.ecommerceapi.dtos.responses;

public record CustomerResponse(
        long id,
        String firstName,
        String lastName,
        String email
) {
}
