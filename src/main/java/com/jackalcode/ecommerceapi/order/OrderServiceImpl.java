package com.jackalcode.ecommerceapi.order;

import com.jackalcode.ecommerceapi.cart.Cart;
import com.jackalcode.ecommerceapi.cart.CartItem;
import com.jackalcode.ecommerceapi.dtos.requests.WebhookRequest;
import com.jackalcode.ecommerceapi.dtos.responses.PaymentResponse;
import com.jackalcode.ecommerceapi.dtos.responses.PaymentStatus;
import com.jackalcode.ecommerceapi.entities.*;
import com.jackalcode.ecommerceapi.exceptions.*;
import com.jackalcode.ecommerceapi.cart.CartRepository;
import com.jackalcode.ecommerceapi.services.PaymentService;
import com.jackalcode.ecommerceapi.services.impl.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final PaymentService paymentService;

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
            CheckoutSession checkoutSession = paymentService.createCheckoutSession(order);
            cart.clearItems();
            return new CheckoutResponse(order.getId(), checkoutSession.sessionUrl());

        } catch (RuntimeException ex) {

            //Delete order if checkout unsuccessful
            orderRepository.delete(order);
            throw new CheckoutException(ex.getMessage());
        }
    }

    @Transactional
    public void handleWebhookEvent(WebhookRequest webhookRequest) {

        PaymentResponse paymentResponse = paymentService.processWebhookRequest(webhookRequest);

        if (paymentResponse != null) {
            var order = orderRepository.findById(paymentResponse.orderId()).orElseThrow(
                    () -> new OrderNotFoundException("Order not found with id: " + paymentResponse.orderId())
            );

            if (paymentResponse.status().equals(PaymentStatus.SUCCESS)) {
                order.setStatus(OrderStatus.PAID);
            } else {
                order.setStatus(OrderStatus.FAILED);
            }

            orderRepository.save(order);
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
