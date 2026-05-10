package com.jackalcode.ecommerce_store.order;

public record CheckoutResponse(
        Long orderId,
        String paymentUrl
) {
}
