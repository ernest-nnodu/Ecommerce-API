package com.jackalcode.ecommerceapi.order;

import com.jackalcode.ecommerceapi.dtos.requests.WebhookRequest;

import java.util.List;

public interface OrderService {

    CheckoutResponse createOrder(CheckoutRequest checkoutRequest);

    List<OrderResponse> getOrders();

    OrderResponse getOrder(Long orderId);

    void handleWebhookEvent(WebhookRequest webhookRequest);
}
