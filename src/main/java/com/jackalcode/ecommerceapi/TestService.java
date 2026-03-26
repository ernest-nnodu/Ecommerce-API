package com.jackalcode.ecommerceapi;

import com.jackalcode.ecommerceapi.entity.*;
import com.jackalcode.ecommerceapi.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class TestService {

    private final CustomerRepository customerRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;

    public void getCustomer() {
        var customer = customerRepository.findById(1L).orElseThrow();
        System.out.println(customer);
    }

    public void getCategory() {
        Category category = categoryRepository.findById(1L).orElseThrow();
        System.out.println(category);
    }

    public void getProduct() {
        Product product = productRepository.findById(1L).orElseThrow();
        System.out.println(product);
    }

    public void getCart() {
        Cart cart = cartRepository.findById(1L).orElseThrow();
        System.out.println(cart);
    }

    @Transactional
    public void getOrder() {
        Order order = orderRepository.findById(1L).orElseThrow();
        System.out.println(order);
    }

    public void getPayment() {
        Payment payment = paymentRepository.findById(2L).orElseThrow();
        System.out.println(payment);
    }
}
