package com.jackalcode.ecommerceapi.dtos.responses;

import java.math.BigDecimal;

public record CartProductDto(
        Long id,
        String name,
        BigDecimal price
) {
}
