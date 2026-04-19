package com.jackalcode.ecommerceapi.order;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "/customers/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<CheckoutResponse> checkout(@Valid @RequestBody CheckoutRequest checkoutRequest) {

        return ResponseEntity.ok(orderService.createOrder(checkoutRequest));
    }

    @PostMapping(path = "/checkout-webhook")
    public ResponseEntity<Void> checkoutWebhook(@RequestHeader Map<String, String> headers,
                                                @RequestBody String payload) {

        orderService.handleWebhookEvent(new WebhookRequest(headers, payload));
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getOrders() {

        return ResponseEntity.ok(orderService.getOrders());
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable(name = "id") Long orderId) {

        return ResponseEntity.ok(orderService.getOrder(orderId));
    }
}
