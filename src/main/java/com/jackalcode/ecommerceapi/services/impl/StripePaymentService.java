package com.jackalcode.ecommerceapi.services.impl;

import com.jackalcode.ecommerceapi.dtos.requests.WebhookRequest;
import com.jackalcode.ecommerceapi.dtos.responses.PaymentResponse;
import com.jackalcode.ecommerceapi.dtos.responses.PaymentStatus;
import com.jackalcode.ecommerceapi.order.Order;
import com.jackalcode.ecommerceapi.order.OrderItem;
import com.jackalcode.ecommerceapi.order.CheckoutSession;
import com.jackalcode.ecommerceapi.services.PaymentService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class StripePaymentService implements PaymentService {

    @Value("${websiteUrl}")
    private String websiteUrl;

    @Value("${stripe.webhookSecret}")
    private String webhookSecret;

    @Override
    public CheckoutSession createCheckoutSession(Order order) {

        //Create checkout session
        try {
            var builder = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl(websiteUrl + "/checkout-success")
                    .setCancelUrl(websiteUrl + "/checkout-cancel")
                    .putMetadata("order_id", order.getId().toString());

            order.getOrderItems().forEach(item -> {
                var lineItem = createLineItem(item);
                builder.addLineItem(lineItem);
            });

            var session = Session.create(builder.build());

            return new CheckoutSession(session.getUrl());

        } catch (StripeException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public PaymentResponse processWebhookRequest(WebhookRequest webhookRequest) {

        var signature = webhookRequest.headers().get("Stripe-Signature");
        var payload = webhookRequest.payload();

        try {
            var event = Webhook.constructEvent(payload, signature, webhookSecret);
            return getPaymentResponse(event);

        } catch (SignatureVerificationException ex) {
            throw new RuntimeException(ex);
        }
    }

    private PaymentResponse getPaymentResponse(Event event) {

        if (!event.getType().startsWith("payment_intent")) {
            return null; // ignore other events like charge.succeeded
        }

        var stripeObject = event.getDataObjectDeserializer()
                .getObject()
                .orElse(null);

        if (!(stripeObject instanceof PaymentIntent paymentIntent)) {
            return null;
        }


        var orderId = paymentIntent.getMetadata().get("order_id");
        var paymentStatus = switch (event.getType()) {
            case "payment_intent.succeeded" -> PaymentStatus.SUCCESS;
            case "payment_intent.payment_failed" -> PaymentStatus.FAILED;
            default -> throw new IllegalStateException("Unexpected value: " + event.getType());
        };

        return new PaymentResponse(Long.valueOf(orderId), paymentStatus);

    }

    private SessionCreateParams.LineItem createLineItem(OrderItem item) {

        return SessionCreateParams.LineItem.builder()
                .setQuantity(Long.valueOf(item.getQuantity()))
                .setPriceData(createPriceData(item))
                .build();
    }

    private SessionCreateParams.LineItem.PriceData createPriceData(OrderItem item) {

        return SessionCreateParams.LineItem.PriceData.builder()
                .setCurrency("GBP")
                .setUnitAmountDecimal(item.getPrice().multiply(BigDecimal.valueOf(100)))
                .setProductData(createProductData(item))
                .build();
    }

    private SessionCreateParams.LineItem.PriceData.ProductData createProductData(OrderItem item) {

        return SessionCreateParams.LineItem.PriceData.ProductData.builder()
                .setName(item.getProduct().getName())
                .build();
    }
}