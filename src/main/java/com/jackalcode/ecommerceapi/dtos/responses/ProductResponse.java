package com.jackalcode.ecommerceapi.dtos.responses;

import java.math.BigDecimal;

public record ProductResponse(
        Long id,
        String name,
        BigDecimal price,
        Long categoryId,
        String categoryName
) {
}
