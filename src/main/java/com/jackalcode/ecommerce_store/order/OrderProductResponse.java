package com.jackalcode.ecommerce_store.order;

import java.math.BigDecimal;

public record OrderProductResponse(
        Long id,
        String name,
        BigDecimal price
) {
}
