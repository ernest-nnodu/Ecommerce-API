package com.jackalcode.ecommerceapi.payment;

import com.jackalcode.ecommerceapi.order.WebhookRequest;
import com.jackalcode.ecommerceapi.order.CheckoutSession;
import com.jackalcode.ecommerceapi.order.Order;

public interface PaymentService {

    CheckoutSession createCheckoutSession(Order order);

    PaymentResponse processWebhookRequest(WebhookRequest webhookRequest);
}
