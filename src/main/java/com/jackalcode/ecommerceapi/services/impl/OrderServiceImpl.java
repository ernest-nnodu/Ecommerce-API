package com.jackalcode.ecommerceapi.services.impl;

import com.jackalcode.ecommerceapi.dtos.requests.CheckoutRequest;
import com.jackalcode.ecommerceapi.dtos.responses.CheckoutResponse;
import com.jackalcode.ecommerceapi.dtos.responses.OrderResponse;
import com.jackalcode.ecommerceapi.entities.*;
import com.jackalcode.ecommerceapi.exceptions.*;
import com.jackalcode.ecommerceapi.mappers.OrderMapper;
import com.jackalcode.ecommerceapi.repositories.CartRepository;
import com.jackalcode.ecommerceapi.repositories.OrderRepository;
import com.jackalcode.ecommerceapi.services.OrderService;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final OrderMapper orderMapper;
    private final AuthenticationService authenticationService;

    @Value("${websiteUrl}")
    private String websiteUrl;

    @Transactional
    @Override
    public CheckoutResponse createOrder(CheckoutRequest checkoutRequest) {

        //Retrieve cart from database
        var cart = cartRepository.findById(checkoutRequest.cartId())
                .orElseThrow(() -> new CartNotFoundException("Cart not found with id: " +
                        checkoutRequest.cartId()));

        if (cart.isEmpty()) {
            throw new CartEmptyException("Cart is empty");
        }

        //Retrieve current logged in customer
        var currentCustomer = authenticationService.getCurrentCustomer();

        //Verify that current logged in customer is the owner of the cart
        if (!currentCustomer.getId().equals(cart.getCustomer().getId())) {
            throw new CustomerNotAuthorizedException("Customer not authorized to checkout order");
        }

        //Generate order from the cart and persist to database
        var order = generateOrder(cart);
        order.getOrderItems().forEach(System.out::println);
        orderRepository.save(order);

        //Create checkout session
        try {
            var builder = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl(websiteUrl + "/checkout-success")
                    .setCancelUrl(websiteUrl + "/checkout-cancel");

            order.getOrderItems().forEach(item -> {
                var lineItem = SessionCreateParams.LineItem.builder()
                        .setQuantity(Long.valueOf(item.getQuantity()))
                        .setPriceData(
                                SessionCreateParams.LineItem.PriceData.builder()
                                        .setCurrency("GBP")
                                        .setUnitAmountDecimal(item.getPrice()
                                                .multiply(BigDecimal.valueOf(100)))
                                        .setProductData(
                                                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                        .setName(item.getProduct().getName())
                                                        .build()
                                        ).build()
                        ).build();
                builder.addLineItem(lineItem);
            });

            var session = Session.create(builder.build());
            cart.clearItems();

            return new CheckoutResponse(order.getId(), session.getUrl());

        } catch (StripeException ex) {
            System.out.println(ex.getMessage());
            orderRepository.delete(order);
            throw new CheckoutException(ex.getMessage());
        }
    }

    @Override
    public List<OrderResponse> getOrders() {

        var currentCustomer = authenticationService.getCurrentCustomer();

        List<Order> orders = currentCustomer.getRole().equals(Role.ADMIN) ? orderRepository.findAll() :
         orderRepository.findAllByCustomerId(currentCustomer.getId());

        return orders.stream()
                .map(orderMapper::toOrderResponse)
                .toList();
    }

    @Override
    public OrderResponse getOrder(Long orderId) {

        var order = orderRepository.findById(orderId).orElseThrow(
                () -> new OrderNotFoundException("Order not found with Id: " + orderId));

        var currentCustomer = authenticationService.getCurrentCustomer();

        if (!currentCustomer.getId().equals(order.getCustomer().getId()) &&
                !currentCustomer.getRole().equals(Role.ADMIN)) {
            throw new CustomerNotAuthorizedException("Customer not authorized to access order");
        }

        return orderMapper.toOrderResponse(order);
    }

    private Order generateOrder(Cart cart) {
        var customer = cart.getCustomer();
        var order = new Order();
        order.setCustomer(customer);

        Set<CartItem> cartItems = cart.getCartItems();
        cartItems.forEach(cartItem -> {
            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getProduct().getPrice());
            order.addOrderItem(orderItem);
        });

        order.calculateTotalAmount();
        order.setStatus(OrderStatus.PENDING);
        order.setDate(Instant.now());

        return order;
    }
}
