package com.jackalcode.ecommerceapi.services;

import com.jackalcode.ecommerceapi.dtos.requests.CheckoutRequest;
import com.jackalcode.ecommerceapi.dtos.responses.CheckoutResponse;
import com.jackalcode.ecommerceapi.dtos.responses.OrderResponse;

import java.util.List;

public interface OrderService {

    CheckoutResponse createOrder(CheckoutRequest checkoutRequest);

    List<OrderResponse> getOrders();

    OrderResponse getOrder(Long orderId);
}
