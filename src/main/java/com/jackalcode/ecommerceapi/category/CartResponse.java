package com.jackalcode.ecommerceapi.category;

import com.jackalcode.ecommerceapi.cart.CartItemResponse;

import java.math.BigDecimal;
import java.util.List;

public record CartResponse(
        Long id,
        List<CartItemResponse> items,
        BigDecimal totalPrice
) {
}
