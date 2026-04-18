package com.jackalcode.ecommerceapi.cart;

import java.math.BigDecimal;

public record CartItemResponse(
        CartProductResponse product,
        int quantity,
        BigDecimal totalPrice
) {
}
