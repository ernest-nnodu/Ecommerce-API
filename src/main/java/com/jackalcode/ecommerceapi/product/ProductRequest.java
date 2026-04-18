package com.jackalcode.ecommerceapi.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ProductRequest(

        @NotNull(message = "Product name is required")
        @NotBlank(message = "Product name should not be empty or blank")
        String name,

        @NotNull(message = "Product price is required")
        BigDecimal price,

        @NotNull(message = "Quantity in stock required")
        Long quantityInStock,

        @NotNull(message = "Category is required")
        Long categoryId
) {
}
