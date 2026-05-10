package com.jackalcode.ecommerce_store.order;

import java.math.BigDecimal;

public record OrderItemResponse(
        OrderProductResponse product,
        int quantity,
        BigDecimal totalPrice
) {
}
