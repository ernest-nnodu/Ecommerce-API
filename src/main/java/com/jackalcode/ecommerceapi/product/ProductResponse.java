package com.jackalcode.ecommerceapi.product;

import java.math.BigDecimal;

public record ProductResponse(
        Long id,
        String name,
        BigDecimal price,
        Long categoryId,
        String categoryName
) {
}
