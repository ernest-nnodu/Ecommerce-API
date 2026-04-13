package com.jackalcode.ecommerceapi.dtos.responses;

import java.math.BigDecimal;

public record CartProductResponse(
        Long id,
        String name,
        BigDecimal price
) {
}
