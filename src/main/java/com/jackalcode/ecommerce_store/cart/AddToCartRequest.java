package com.jackalcode.ecommerce_store.cart;

import jakarta.validation.constraints.NotNull;

public record AddToCartRequest(

        @NotNull(message = "Product id required")
        Long productId
) {
}
