package com.jackalcode.ecommerceapi.order;

import java.util.List;

public interface OrderService {

    CheckoutResponse createOrder(CheckoutRequest checkoutRequest);

    List<OrderResponse> getOrders();

    OrderResponse getOrder(Long orderId);

    void handleWebhookEvent(WebhookRequest webhookRequest);
}
