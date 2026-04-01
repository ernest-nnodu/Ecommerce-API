package com.jackalcode.ecommerceapi.controllers;

import com.jackalcode.ecommerceapi.dtos.requests.AddToCartRequest;
import com.jackalcode.ecommerceapi.dtos.responses.CartItemResponse;
import com.jackalcode.ecommerceapi.services.CartService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/carts")
@AllArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping(path = "/{id}/items")
    public ResponseEntity<CartItemResponse> addItem(@PathVariable(name = "id") Long cartId,
                                                    @RequestBody AddToCartRequest addToCartRequest) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(cartService.addItemToCart(cartId, addToCartRequest));
    }
}
