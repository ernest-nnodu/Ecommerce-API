package com.jackalcode.ecommerceapi;

import com.jackalcode.ecommerceapi.entity.*;
import com.jackalcode.ecommerceapi.repository.CartRepository;
import com.jackalcode.ecommerceapi.repository.CategoryRepository;
import com.jackalcode.ecommerceapi.repository.CustomerRepository;
import com.jackalcode.ecommerceapi.repository.ProductRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EcommerceApiApplication {

    public static void main(String[] args) {

        var context = SpringApplication.run(EcommerceApiApplication.class, args);

        var userRepository = context.getBean(CustomerRepository.class);
        var customer = userRepository.findById(1L).orElseThrow();
        System.out.println(customer);

        var categoryRepository = context.getBean(CategoryRepository.class);
        Category category = categoryRepository.findById(1L).orElseThrow();
        System.out.println(category);

        var productRepository = context.getBean(ProductRepository.class);
        Product product = productRepository.findById(1L).orElseThrow();
        System.out.println(product);

        var cartRepository = context.getBean(CartRepository.class);
        Cart cart = cartRepository.findById(1L).orElseThrow();
        System.out.println(cart);
    }

}
