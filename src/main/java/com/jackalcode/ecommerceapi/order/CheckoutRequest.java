package com.jackalcode.ecommerceapi.order;

import jakarta.validation.constraints.NotNull;

public record CheckoutRequest(

        @NotNull(message = "Cart id is required")
        Long cartId
) {
}
