package com.jackalcode.ecommerceapi.services.impl;

import com.jackalcode.ecommerceapi.dtos.requests.CheckoutRequest;
import com.jackalcode.ecommerceapi.dtos.responses.OrderResponse;
import com.jackalcode.ecommerceapi.entities.*;
import com.jackalcode.ecommerceapi.exceptions.CartEmptyException;
import com.jackalcode.ecommerceapi.exceptions.CartNotFoundException;
import com.jackalcode.ecommerceapi.exceptions.CustomerNotAuthorizedException;
import com.jackalcode.ecommerceapi.mappers.OrderMapper;
import com.jackalcode.ecommerceapi.repositories.CartRepository;
import com.jackalcode.ecommerceapi.repositories.OrderRepository;
import com.jackalcode.ecommerceapi.services.OrderService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Set;

@Service
@AllArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final OrderMapper orderMapper;
    private final AuthenticationService authenticationService;

    @Transactional
    @Override
    public OrderResponse createOrder(CheckoutRequest checkoutRequest) {

        //Retrieve cart from database
        var cart = cartRepository.findById(checkoutRequest.cartId())
                .orElseThrow(() -> new CartNotFoundException("Cart not found with id: " +
                        checkoutRequest.cartId()));

        if (cart.isEmpty()) {
            throw new CartEmptyException("Cart is empty");
        }


        var currentCustomer = authenticationService.getCurrentCustomer();

        if (!currentCustomer.getId().equals(cart.getCustomer().getId())) {
            throw new CustomerNotAuthorizedException("Customer not authorized to checkout order");
        }

        var order = generateOrder(cart);
        order.getOrderItems().forEach(System.out::println);
        orderRepository.save(order);

        cart.clearItems();

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
