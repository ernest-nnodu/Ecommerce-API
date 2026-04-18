package com.jackalcode.ecommerceapi.order;

import java.math.BigDecimal;

public record OrderProductResponse(
        Long id,
        String name,
        BigDecimal price
) {
}
