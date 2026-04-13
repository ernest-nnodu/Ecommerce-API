package com.jackalcode.ecommerceapi.dtos.responses;

import java.math.BigDecimal;

public record OrderItemResponse(
        OrderProductResponse product,
        int quantity,
        BigDecimal totalPrice
) {
}
