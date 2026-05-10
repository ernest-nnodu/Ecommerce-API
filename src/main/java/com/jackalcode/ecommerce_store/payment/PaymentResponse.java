package com.jackalcode.ecommerce_store.payment;

public record PaymentResponse(
        Long orderId,
        PaymentStatus status
) {
}
