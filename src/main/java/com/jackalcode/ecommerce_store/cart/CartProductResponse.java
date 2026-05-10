package com.jackalcode.ecommerce_store.cart;

import java.math.BigDecimal;

public record CartProductResponse(
        Long id,
        String name,
        BigDecimal price
) {
}
