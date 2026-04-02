package com.jackalcode.ecommerceapi.controllers;

import com.jackalcode.ecommerceapi.dtos.requests.AddToCartRequest;
import com.jackalcode.ecommerceapi.dtos.requests.UpdateCartRequest;
import com.jackalcode.ecommerceapi.dtos.responses.CartItemResponse;
import com.jackalcode.ecommerceapi.dtos.responses.CartResponse;
import com.jackalcode.ecommerceapi.services.CartService;
import jakarta.validation.Valid;
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
                                                    @Valid @RequestBody AddToCartRequest addToCartRequest) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(cartService.addItemToCart(cartId, addToCartRequest));
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<CartResponse> getCart(@PathVariable(name = "id") Long cartId) {

        return ResponseEntity.ok(cartService.getCart(cartId));
    }

    @PutMapping(path = "/{cartId}/items/{productId}")
    public ResponseEntity<CartItemResponse> updateCart(@PathVariable Long cartId,
                                                       @PathVariable Long productId,
                                                       @Valid @RequestBody UpdateCartRequest updateCartRequest) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(cartService.updateCart(cartId, productId,updateCartRequest));
    }

    @DeleteMapping(path = "/{cartId}/items/{productId}")
    public ResponseEntity<Void> removeItemFromCart(@PathVariable Long cartId, @PathVariable Long productId) {

        cartService.removeItemFromCart(cartId, productId);
        return ResponseEntity.noContent().build();
    }
}
