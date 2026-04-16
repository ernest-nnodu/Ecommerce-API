package com.jackalcode.ecommerceapi.dtos.responses;

public record PaymentResponse(
        Long orderId,
        PaymentStatus status
) {
}
