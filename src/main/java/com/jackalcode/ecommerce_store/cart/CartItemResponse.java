package com.jackalcode.ecommerce_store.cart;

import java.math.BigDecimal;

public record CartItemResponse(
        CartProductResponse product,
        int quantity,
        BigDecimal totalPrice
) {
}
