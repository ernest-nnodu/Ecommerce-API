package com.jackalcode.ecommerce_store.payment;

import com.jackalcode.ecommerce_store.order.WebhookRequest;
import com.jackalcode.ecommerce_store.order.CheckoutSession;
import com.jackalcode.ecommerce_store.order.Order;

public interface PaymentService {

    CheckoutSession createCheckoutSession(Order order);

    PaymentResponse processWebhookRequest(WebhookRequest webhookRequest);
}
