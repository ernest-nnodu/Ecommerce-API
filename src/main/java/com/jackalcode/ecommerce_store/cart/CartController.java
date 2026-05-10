package com.jackalcode.ecommerce_store.cart;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "customers/carts")
@AllArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping(path = "/items")
    public ResponseEntity<CartItemResponse> addItem(
            @Valid @RequestBody AddToCartRequest addToCartRequest) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(cartService.addItemToCart(addToCartRequest));
    }

    @GetMapping
    public ResponseEntity<CartResponse> getCart() {

        return ResponseEntity.ok(cartService.getCart());
    }

    @PutMapping(path = "/items/{id}")
    public ResponseEntity<CartItemResponse> updateCart(@PathVariable(name = "id") Long productId,
                                                       @Valid @RequestBody UpdateCartRequest updateCartRequest) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(cartService.updateCart(productId,updateCartRequest));
    }

    @DeleteMapping(path = "/items/{id}")
    public ResponseEntity<Void> removeItemFromCart(@PathVariable(name = "id") Long productId) {

        cartService.removeItemFromCart(productId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(path = "/items")
    public ResponseEntity<Void> clearCart() {

        cartService.clearCart();
        return ResponseEntity.noContent().build();
    }
}
