package com.jackalcode.ecommerceapi.cart;

import java.math.BigDecimal;

public record CartProductResponse(
        Long id,
        String name,
        BigDecimal price
) {
}
