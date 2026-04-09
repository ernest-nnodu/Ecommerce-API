package com.jackalcode.ecommerceapi.services;

import com.jackalcode.ecommerceapi.dtos.requests.CheckoutRequest;
import com.jackalcode.ecommerceapi.dtos.responses.OrderResponse;

public interface OrderService {

    OrderResponse createOrder(CheckoutRequest checkoutRequest);
}
