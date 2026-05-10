package com.jackalcode.ecommerce_store.cart;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record UpdateCartRequest(
        @NotNull(message = "Product quantity is required")
        @Min(value = 1, message = "Quantity should greater than zero")
        Integer quantity
) {
}
