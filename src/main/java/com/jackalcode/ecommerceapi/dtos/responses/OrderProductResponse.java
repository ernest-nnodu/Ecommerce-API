package com.jackalcode.ecommerceapi.dtos.responses;

import java.math.BigDecimal;

public record OrderProductResponse(
        Long id,
        String name,
        BigDecimal price
) {
}
