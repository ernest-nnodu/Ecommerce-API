package com.jackalcode.ecommerceapi.order;

import java.math.BigDecimal;

public record OrderItemResponse(
        OrderProductResponse product,
        int quantity,
        BigDecimal totalPrice
) {
}
