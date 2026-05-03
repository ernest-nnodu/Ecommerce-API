package com.jackalcode.ecommerceapi.product;

import java.math.BigDecimal;

public record ProductFilter(
        String name,
        BigDecimal minPrice,
        BigDecimal maxPrice,
        Long categoryId
) {
}
