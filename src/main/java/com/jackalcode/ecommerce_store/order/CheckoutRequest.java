package com.jackalcode.ecommerce_store.order;

import jakarta.validation.constraints.NotNull;

public record CheckoutRequest(

        @NotNull(message = "Cart id is required")
        Long cartId
) {
}
