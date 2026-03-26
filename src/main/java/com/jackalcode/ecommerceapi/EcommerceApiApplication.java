package com.jackalcode.ecommerceapi;

import com.jackalcode.ecommerceapi.entity.*;
import com.jackalcode.ecommerceapi.repository.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EcommerceApiApplication {

    public static void main(String[] args) {

        var context = SpringApplication.run(EcommerceApiApplication.class, args);
        var service = context.getBean(TestService.class);

        service.getCustomer();
        service.getCategory();
        service.getProduct();
        service.getCart();
        service.getOrder();
        service.getPayment();
    }

}
