package com.jackalcode.ecommerceapi.order;

import com.jackalcode.ecommerceapi.cart.CartRepository;
import com.jackalcode.ecommerceapi.payment.PaymentRepository;
import com.jackalcode.ecommerceapi.payment.PaymentService;
import com.jackalcode.ecommerceapi.security.AuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
}
