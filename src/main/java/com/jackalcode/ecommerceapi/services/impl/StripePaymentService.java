package com.jackalcode.ecommerceapi.services.impl;

import com.jackalcode.ecommerceapi.entities.Order;
import com.jackalcode.ecommerceapi.entities.OrderItem;
import com.jackalcode.ecommerceapi.services.CheckoutSession;
import com.jackalcode.ecommerceapi.services.PaymentService;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class StripePaymentService implements PaymentService {

    @Value("${websiteUrl}")
    private String websiteUrl;

    @Override
    public CheckoutSession createCheckoutSession(Order order) {

        //Create checkout session
        try {
            var builder = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl(websiteUrl + "/checkout-success")
                    .setCancelUrl(websiteUrl + "/checkout-cancel");

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