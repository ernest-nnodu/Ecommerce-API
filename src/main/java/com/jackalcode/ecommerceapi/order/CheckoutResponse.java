package com.jackalcode.ecommerceapi.order;

public record CheckoutResponse(
        Long orderId,
        String paymentUrl
) {
}
