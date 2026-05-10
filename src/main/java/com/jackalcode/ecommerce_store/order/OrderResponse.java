package com.jackalcode.ecommerce_store.order;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record OrderResponse(

        Long id,
        Long customerId,
        Instant date,
        OrderStatus status,
        List<OrderItemResponse> items,
        BigDecimal totalAmount
) {
}
