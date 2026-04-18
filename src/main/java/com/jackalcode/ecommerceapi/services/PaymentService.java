package com.jackalcode.ecommerceapi.services;

import com.jackalcode.ecommerceapi.dtos.requests.WebhookRequest;
import com.jackalcode.ecommerceapi.dtos.responses.PaymentResponse;
import com.jackalcode.ecommerceapi.order.CheckoutSession;
import com.jackalcode.ecommerceapi.order.Order;

public interface PaymentService {

    CheckoutSession createCheckoutSession(Order order);

    PaymentResponse processWebhookRequest(WebhookRequest webhookRequest);
}
