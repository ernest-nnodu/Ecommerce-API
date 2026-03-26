package com.jackalcode.ecommerceapi;

import com.jackalcode.ecommerceapi.entity.Category;
import com.jackalcode.ecommerceapi.entity.Customer;
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

        var category = new Category(1, "Category 1");
        System.out.println(category);
    }

}
