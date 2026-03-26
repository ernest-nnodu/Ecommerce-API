package com.jackalcode.ecommerceapi;

import com.jackalcode.ecommerceapi.entity.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.Instant;

@SpringBootApplication
public class EcommerceApiApplication {

    public static void main(String[] args) {

        SpringApplication.run(EcommerceApiApplication.class, args);

        var customer = Customer.builder()
                .id(1L)
                .firstName("Jack")
                .lastName("Alice")
                .email("email")
                .password("password")
                .build();
        System.out.println(customer);

        var category = new Category();
        category.setId(1L);
        category.setName("Category");
        System.out.println(category);

        var product = Product.builder()
                .id(1L)
                .category(category)
                .name("Product")
                .quantityInStock(30L)
                .price(100.0)
                .build();
        System.out.println(product);

        Cart cart = new Cart();
        cart.setId(1L);
        cart.setCustomer(customer);
        System.out.println(cart);

        var cartItem = CartItem.builder()
                .id(1L)
                .product(product)
                .quantity(10)
                .build();

        cart.addItem(cartItem);
        System.out.println(cartItem);
        System.out.println(cart);

        OrderItem orderItem = new OrderItem();
        orderItem.setId(1L);
        orderItem.setProduct(product);
        orderItem.setPrice(product.getPrice());
        orderItem.setQuantity(10);

        Order order = new Order();
        order.setId(1L);
        order.setCustomer(customer);
        order.setDate(Instant.now());
        order.addOrderItem(orderItem);

        System.out.println(orderItem);
        System.out.println(order);

        Payment payment = new Payment();
        payment.setId(1L);
        payment.setAmount(105.15);
        payment.setDate(Instant.now());
        payment.setOrder(order);

        System.out.println(payment);

    }

}
