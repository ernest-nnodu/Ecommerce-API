package com.jackalcode.ecommerce_store.order;

import java.util.List;

public interface OrderService {

    CheckoutResponse createOrder(CheckoutRequest checkoutRequest);

    List<OrderResponse> getOrders();

    OrderResponse getOrder(Long orderId);

    void handleWebhookEvent(WebhookRequest webhookRequest);
}
