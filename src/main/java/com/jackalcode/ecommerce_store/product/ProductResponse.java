package com.jackalcode.ecommerce_store.product;

import java.math.BigDecimal;

public record ProductResponse(
        Long id,
        String name,
        BigDecimal price,
        Long categoryId,
        String categoryName
) {
}
