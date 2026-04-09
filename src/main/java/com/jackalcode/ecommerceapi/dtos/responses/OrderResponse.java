package com.jackalcode.ecommerceapi.dtos.responses;

import com.jackalcode.ecommerceapi.entities.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record OrderResponse(

        Long id,
        Instant date,
        OrderStatus status,
        List<OrderItemResponse> items,
        BigDecimal totalAmount
) {
}
