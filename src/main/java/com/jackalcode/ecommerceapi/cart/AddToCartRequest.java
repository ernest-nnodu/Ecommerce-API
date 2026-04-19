package com.jackalcode.ecommerceapi.cart;

import jakarta.validation.constraints.NotNull;

public record AddToCartRequest(

        @NotNull(message = "Product id required")
        Long productId
) {
}
