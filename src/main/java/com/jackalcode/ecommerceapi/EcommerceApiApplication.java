package com.jackalcode.ecommerceapi;

import com.jackalcode.ecommerceapi.entity.Cart;
import com.jackalcode.ecommerceapi.entity.Category;
import com.jackalcode.ecommerceapi.entity.Customer;
import com.jackalcode.ecommerceapi.entity.Product;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

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
    }

}
