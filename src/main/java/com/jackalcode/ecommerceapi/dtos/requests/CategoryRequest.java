package com.jackalcode.ecommerceapi.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CategoryRequest(

        @NotNull(message = "Category name required")
        @NotBlank(message = "Category name should not be empty or blank")
        String name
) {
}
