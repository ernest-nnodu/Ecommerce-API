package com.jackalcode.ecommerceapi.dtos.requests;

import jakarta.validation.constraints.*;

public record RegisterCustomerRequest(

        @NotNull(message = "First name is required")
        @NotBlank(message = "First name should not be empty or blank")
        String firstName,

        @NotNull(message = "Last name is required")
        @NotBlank(message = "Last name should not be empty or blank")
        String lastName,

        @NotNull(message = "Email is required")
        @Email(message = "Email should be a valid email")
        String email,

        @NotNull(message = "Password is required")
        @NotBlank(message = "First name should not be empty or blank")
        @Pattern(
                regexp = "^(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z\\d]).{8,}$",
                message = "Password must be at least 8 characters, include uppercase, number and special character"
        )
        String password
) {
}
