package com.jackalcode.ecommerceapi.dtos.responses;

import com.jackalcode.ecommerceapi.entities.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;

public record OrderResponse(

        Long id,
        BigDecimal totalAmount,
        Instant date,
        OrderStatus status
) {
}
