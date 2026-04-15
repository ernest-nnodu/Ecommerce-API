package com.jackalcode.ecommerceapi.dtos.responses;

public record CheckoutResponse(
        Long orderId,
        String paymentUrl
) {
}
