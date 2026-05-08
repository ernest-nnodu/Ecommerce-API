package com.jackalcode.ecommerceapi.order;

import com.jackalcode.ecommerceapi.cart.Cart;
import com.jackalcode.ecommerceapi.cart.CartItem;
import com.jackalcode.ecommerceapi.cart.CartRepository;
import com.jackalcode.ecommerceapi.customer.Customer;
import com.jackalcode.ecommerceapi.exceptions.CartNotFoundException;
import com.jackalcode.ecommerceapi.exceptions.CheckoutException;
import com.jackalcode.ecommerceapi.exceptions.CustomerNotAuthorizedException;
import com.jackalcode.ecommerceapi.exceptions.OrderNotFoundException;
import com.jackalcode.ecommerceapi.payment.*;
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
import java.util.List;
import java.util.Map;
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

    @Test
    @DisplayName("getOrders: returns all orders when current customer is admin")
    void getOrders_withAdmin_returnsAllOrders() {

        var admin = new Customer();
        admin.setId(1L);
        admin.setRole(Role.ADMIN);

        var customerA = new Customer();
        customerA.setId(10L);

        var customerB = new Customer();
        customerB.setId(20L);

        var orderA = new Order();
        orderA.setId(100L);
        orderA.setCustomer(customerA);

        var orderB = new Order();
        orderB.setId(200L);
        orderB.setCustomer(customerB);

        when(authenticationService.getCurrentCustomer()).thenReturn(admin);
        when(orderRepository.findAll()).thenReturn(List.of(orderA, orderB));

        List<OrderResponse> results = orderService.getOrders();

        assertAll(
                () -> assertNotNull(results),
                () -> assertEquals(2, results.size()),
                () -> assertTrue(results.stream().anyMatch(r -> r.id().equals(100L) && r.customerId().equals(10L))),
                () -> assertTrue(results.stream().anyMatch(r -> r.id().equals(200L) && r.customerId().equals(20L)))
        );

        verify(authenticationService).getCurrentCustomer();
        verify(orderRepository).findAll();
        verify(orderRepository, never()).findAllByCustomerId(anyLong());
    }

    @Test
    @DisplayName("getOrders: returns only current customer's orders when not admin")
    void getOrders_whenNotAdmin_returnsOnlyUsersOrders() {

        var customer = getCustomer();

        var customerOrder = new Order();
        customerOrder.setId(300L);
        customerOrder.setCustomer(customer);

        when(authenticationService.getCurrentCustomer()).thenReturn(customer);
        when(orderRepository.findAllByCustomerId(customer.getId())).thenReturn(List.of(customerOrder));

        List<OrderResponse> results = orderService.getOrders();

        assertAll(
                () -> assertNotNull(results),
                () -> assertEquals(1, results.size()),
                () -> assertEquals(300L, results.getFirst().id()),
                () -> assertEquals(7L, results.getFirst().customerId()));

        verify(authenticationService).getCurrentCustomer();
        verify(orderRepository).findAllByCustomerId(7L);
        verify(orderRepository, never()).findAll();
    }

    @Test
    @DisplayName("getOrder: returns order when current customer is the owner")
    void getOrder_whenOwner_returnsOrderResponse() {

        Long orderId = 5L;
        var owner = getCustomer();

        var order = new Order();
        order.setId(orderId);
        order.setCustomer(owner);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(authenticationService.getCurrentCustomer()).thenReturn(owner);

        OrderResponse resp = orderService.getOrder(orderId);

        assertAll(
                () -> assertNotNull(resp),
                () -> assertEquals(orderId, resp.id()),
                () -> assertEquals(7L, resp.customerId())
        );

        verify(orderRepository).findById(orderId);
        verify(authenticationService).getCurrentCustomer();
    }

    @Test
    @DisplayName("getOrder: returns order for admin regardless of owner")
    void getOrder_whenAdmin_returnsOrderResponse() {

        Long orderId = 600L;
        var owner = getCustomer();

        var admin = new Customer();
        admin.setId(1L);
        admin.setRole(Role.ADMIN);

        var order = new Order();
        order.setId(orderId);
        order.setCustomer(owner);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(authenticationService.getCurrentCustomer()).thenReturn(admin);

        OrderResponse resp = orderService.getOrder(orderId);

        assertAll(
                () -> assertNotNull(resp),
                () -> assertEquals(orderId, resp.id()),
                () -> assertEquals(7L, resp.customerId())
        );

        verify(orderRepository).findById(orderId);
        verify(authenticationService).getCurrentCustomer();
    }

    @Test
    @DisplayName("getOrder: throws CustomerNotAuthorizedException when not owner and not admin")
    void getOrder_whenNotAuthorized_throwsException() {

        Long orderId = 700L;
        var owner = getCustomer();

        var other = new Customer();
        other.setId(302L);
        other.setRole(Role.USER);

        var order = new Order();
        order.setId(orderId);
        order.setCustomer(owner);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(authenticationService.getCurrentCustomer()).thenReturn(other);

        assertThrows(CustomerNotAuthorizedException.class,
                () -> orderService.getOrder(orderId));

        verify(orderRepository).findById(orderId);
        verify(authenticationService).getCurrentCustomer();
    }

    @Test
    @DisplayName("getOrder: throws OrderNotFoundException when order does not exist")
    void getOrder_whenOrderMissing_throwsOrderNotFoundException() {

        Long missingId = 99L;
        when(orderRepository.findById(missingId)).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class,
                () -> orderService.getOrder(missingId));

        verify(orderRepository).findById(missingId);
        verify(authenticationService, never()).getCurrentCustomer();
    }

    @Test
    @DisplayName("handleWebhookEvent: when payment is successful, saves Payment and marks Order as PAID")
    void handleWebhookEvent_whenPaymentSuccess_updatesOrderAndSavesPayment() {
        Long orderId = 9L;

        // existing order in DB (initially PENDING)
        var order = new Order();
        order.setId(orderId);
        order.setStatus(OrderStatus.PENDING);
        order.setTotalAmount(BigDecimal.valueOf(123.45));

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(paymentService.processWebhookRequest(any(WebhookRequest.class)))
                .thenReturn(new PaymentResponse(orderId, PaymentStatus.SUCCESS));

        orderService.handleWebhookEvent(
                new WebhookRequest(Map.of("Stripe-Signature", "sig"), "payload"));

        // capture saved payment and order
        ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);
        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);

        verify(paymentRepository).save(paymentCaptor.capture());
        verify(orderRepository, atLeastOnce()).save(orderCaptor.capture()); // save may be called elsewhere; atLeastOnce is safe

        Payment savedPayment = paymentCaptor.getValue();
        Order savedOrder = orderCaptor.getValue();

        assertAll(
                () -> assertEquals(PaymentStatus.SUCCESS, savedPayment.getStatus()),
                () -> assertEquals(order.getTotalAmount(), savedPayment.getAmount()),
                () -> assertSame(order, savedPayment.getOrder()), // payment references the same order instance
                () -> assertEquals(OrderStatus.PAID, savedOrder.getStatus())
        );
    }

    @Test
    @DisplayName("handleWebhookEvent: when payment fails, saves Payment and marks Order as FAILED")
    void handleWebhookEvent_whenPaymentFailed_updatesOrderToFailedAndSavesPayment() {
        Long orderId = 9L;

        var order = new Order();
        order.setId(orderId);
        order.setStatus(OrderStatus.PENDING);
        order.setTotalAmount(BigDecimal.valueOf(50.00));

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(paymentService.processWebhookRequest(any(WebhookRequest.class)))
                .thenReturn(new PaymentResponse(orderId, PaymentStatus.FAILED));

        orderService.handleWebhookEvent(
                new WebhookRequest(Map.of("Stripe-Signature", "sig"), "payload"));

        ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);
        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);

        verify(paymentRepository).save(paymentCaptor.capture());
        verify(orderRepository, atLeastOnce()).save(orderCaptor.capture());

        Payment savedPayment = paymentCaptor.getValue();
        Order savedOrder = orderCaptor.getValue();

        assertAll(
                () -> assertEquals(PaymentStatus.FAILED, savedPayment.getStatus()),
                () -> assertEquals(order.getTotalAmount(), savedPayment.getAmount()),
                () -> assertEquals(OrderStatus.FAILED, savedOrder.getStatus())
        );
    }

    @Test
    @DisplayName("handleWebhookEvent: when paymentService returns null, does nothing")
    void handleWebhookEvent_whenPaymentResponseNull_doesNothing() {
        when(paymentService.processWebhookRequest(any(WebhookRequest.class))).thenReturn(null);

        orderService.handleWebhookEvent(
                new WebhookRequest(Map.of("Stripe-Signature", "sig"), "payload"));

        verifyNoInteractions(paymentRepository);
        verify(orderRepository, never()).save(any());
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
