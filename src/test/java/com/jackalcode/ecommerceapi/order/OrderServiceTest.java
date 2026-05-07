package com.jackalcode.ecommerceapi.order;

import com.jackalcode.ecommerceapi.cart.Cart;
import com.jackalcode.ecommerceapi.cart.CartItem;
import com.jackalcode.ecommerceapi.cart.CartRepository;
import com.jackalcode.ecommerceapi.customer.Customer;
import com.jackalcode.ecommerceapi.exceptions.CartNotFoundException;
import com.jackalcode.ecommerceapi.exceptions.CheckoutException;
import com.jackalcode.ecommerceapi.payment.PaymentRepository;
import com.jackalcode.ecommerceapi.payment.PaymentService;
import com.jackalcode.ecommerceapi.product.Product;
import com.jackalcode.ecommerceapi.security.AuthenticationService;
import com.jackalcode.ecommerceapi.security.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    private OrderService orderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private PaymentService paymentService;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private AuthenticationService authenticationService;

    private final OrderMapper orderMapper = Mappers.getMapper(OrderMapper.class);

    @BeforeEach
    void setUp() {
        orderService = new OrderServiceImpl(
                orderRepository,
                cartRepository,
                orderMapper,
                authenticationService,
                paymentService,
                paymentRepository);
    }

    @Test
    @DisplayName("createOrder: when valid request, creates order and returns checkout response")
    void createOrder_whenValidRequest_createsOrderAndReturnsCheckoutResponse() {

        Long cartId = 1L;
        var customer = getCustomer();

        var cart = new Cart();
        cart.setId(cartId);
        cart.setCustomer(customer);

        var product = new Product();
        product.setId(100L);
        product.setName("Gizmo");
        product.setPrice(new BigDecimal("49.99"));

        var item = new CartItem();
        item.setProduct(product);
        item.setQuantity(2);
        item.setId(1L);
        cart.addItem(item);

        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));
        when(authenticationService.getCurrentCustomer()).thenReturn(customer);

        // simulate orderRepository.save assigning an id
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order o = invocation.getArgument(0);
            o.setId(5L);
            return o;
        });

        when(paymentService.createCheckoutSession(any(Order.class)))
                .thenReturn(new CheckoutSession("https://checkout.example/session/abc123"));

        var request = new CheckoutRequest(cartId);

        CheckoutResponse response = orderService.createOrder(request);

        assertNotNull(response);
        assertEquals(5L, response.orderId());
        assertEquals("https://checkout.example/session/abc123", response.paymentUrl());

        // Order saved with proper status and total amount
        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(orderCaptor.capture());
        Order savedOrder = orderCaptor.getValue();

        assertAll(
                () -> assertEquals(OrderStatus.PENDING, savedOrder.getStatus()),
                () -> assertNotNull(savedOrder.getDate()),
                () -> assertEquals(BigDecimal.valueOf(99.98), savedOrder.getTotalAmount())
        );

        // Cart should have been cleared
        assertTrue(cart.isEmpty(), "Expected cart to be cleared after successful checkout");

        verify(paymentService).createCheckoutSession(any(Order.class));
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    @DisplayName("createOrder: when payment creation fails, delete order and throw CheckoutException")
    void createOrder_whenPaymentFails_deletesOrderAndThrowsCheckoutException() {

        Long cartId = 2L;
        var customer = getCustomer();

        var cart = new Cart();
        cart.setId(cartId);
        cart.setCustomer(customer);

        var product = new Product();
        product.setId(2L);
        product.setPrice(new BigDecimal("10.00"));

        var item = new CartItem();
        item.setProduct(product);
        item.setQuantity(1);
        item.setId(2L);
        cart.addItem(item);

        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));
        when(authenticationService.getCurrentCustomer()).thenReturn(customer);

        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order o = invocation.getArgument(0);
            o.setId(7L);
            return o;
        });

        when(paymentService.createCheckoutSession(any(Order.class))).thenThrow(new RuntimeException("Stripe API error"));

        var request = new CheckoutRequest(cartId);

        assertThrows(CheckoutException.class, () -> orderService.createOrder(request));

        // Verify that the order was saved then deleted on failure
        ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(captor.capture());
        Order saved = captor.getValue();
        verify(orderRepository).delete(saved);

        // Cart should NOT be cleared when checkout fails (clear happens only on success)
        assertFalse(cart.isEmpty(), "Expected cart to remain populated after failed checkout");

        verify(paymentService).createCheckoutSession(any(Order.class));
    }

    @Test
    @DisplayName("createOrder: when cart not found, throws CartNotFoundException")
    void createOrder_whenCartNotFound_throwsCartNotFoundException() {

        Long missingCartId = 999L;
        when(cartRepository.findById(missingCartId)).thenReturn(Optional.empty());

        var request = new CheckoutRequest(missingCartId);

        assertThrows(CartNotFoundException.class, () -> orderService.createOrder(request));

        verify(cartRepository).findById(missingCartId);
        verify(orderRepository, never()).save(any());
        verify(paymentService, never()).createCheckoutSession(any());
    }

    private Customer getCustomer() {
        var customer = new Customer();
        customer.setId(7L);
        customer.setFirstName("Buyer");
        customer.setLastName("One");
        customer.setEmail("buyer@example.com");
        customer.setRole(Role.USER);
        return customer;
    }
}
