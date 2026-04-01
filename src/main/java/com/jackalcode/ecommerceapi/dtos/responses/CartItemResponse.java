package com.jackalcode.ecommerceapi.dtos.responses;

import java.math.BigDecimal;

public record CartItemResponse(
        CartProductDto product,
        int quantity,
        BigDecimal totalPrice
) {
}
