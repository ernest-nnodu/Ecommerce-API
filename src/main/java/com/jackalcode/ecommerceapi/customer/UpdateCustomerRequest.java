package com.jackalcode.ecommerceapi.customer;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateCustomerRequest(

        @NotNull(message = "First name is required")
        @NotBlank(message = "First name should not be empty or blank")
        String firstName,

        @NotNull(message = "Last name is required")
        @NotBlank(message = "Last name should not be empty or blank")
        String lastName,

        @NotNull(message = "Email is required")
        @Email(message = "Email should be a valid email")
        String email
) {
}
