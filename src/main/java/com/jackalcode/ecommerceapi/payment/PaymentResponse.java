package com.jackalcode.ecommerceapi.payment;

public record PaymentResponse(
        Long orderId,
        PaymentStatus status
) {
}
