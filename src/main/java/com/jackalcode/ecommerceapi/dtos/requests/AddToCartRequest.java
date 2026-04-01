package com.jackalcode.ecommerceapi.dtos.requests;

import jakarta.validation.constraints.NotNull;

public record AddToCartRequest(

        @NotNull(message = "Product id required")
        Long productId
) {
}
