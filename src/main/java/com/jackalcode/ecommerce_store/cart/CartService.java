package com.jackalcode.ecommerce_store.cart;

public interface CartService {

    CartItemResponse addItemToCart(AddToCartRequest addToCartRequest);

    CartResponse getCart();

    CartItemResponse updateCart(Long productId, UpdateCartRequest updateCartRequest);

    void removeItemFromCart(Long productId);

    void clearCart();
}
