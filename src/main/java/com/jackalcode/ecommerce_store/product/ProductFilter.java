package com.jackalcode.ecommerce_store.product;

import java.math.BigDecimal;

public record ProductFilter(
        String name,
        BigDecimal minPrice,
        BigDecimal maxPrice,
        Long categoryId
) {
}
