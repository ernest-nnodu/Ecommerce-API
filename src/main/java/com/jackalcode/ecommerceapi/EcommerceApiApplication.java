package com.jackalcode.ecommerceapi;

import com.jackalcode.ecommerceapi.entity.*;
import com.jackalcode.ecommerceapi.repository.CustomerRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.Instant;

@SpringBootApplication
public class EcommerceApiApplication {

    public static void main(String[] args) {

        var context = SpringApplication.run(EcommerceApiApplication.class, args);

        var userRepository = context.getBean(CustomerRepository.class);
        var customer = userRepository.findById(1L).orElseThrow();
        System.out.println(customer);
    }

}
