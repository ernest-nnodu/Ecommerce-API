package com.jackalcode.ecommerceapi.controllers;

import com.jackalcode.ecommerceapi.dtos.requests.CheckoutRequest;
import com.jackalcode.ecommerceapi.dtos.responses.OrderResponse;
import com.jackalcode.ecommerceapi.services.OrderService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping(path = "/checkout")
    public ResponseEntity<OrderResponse> checkout(@Valid @RequestBody CheckoutRequest checkoutRequest) {

        return ResponseEntity.ok(orderService.createOrder(checkoutRequest));
    }

    @GetMapping(path = "/orders")
    public ResponseEntity<List<OrderResponse>> getOrders() {

        return ResponseEntity.ok(orderService.getOrders());
    }

    @GetMapping(path = "orders/{id}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable(name = "id") Long orderId) {

        return ResponseEntity.ok(orderService.getOrder(orderId));
    }
}
