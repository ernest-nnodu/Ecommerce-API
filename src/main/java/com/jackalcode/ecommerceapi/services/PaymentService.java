package com.jackalcode.ecommerceapi.services;

import com.jackalcode.ecommerceapi.entities.Order;

public interface PaymentService {

    CheckoutSession createCheckoutSession(Order order);
}
