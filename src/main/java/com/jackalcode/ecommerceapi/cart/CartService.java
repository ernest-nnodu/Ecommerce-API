package com.jackalcode.ecommerceapi.cart;

import com.jackalcode.ecommerceapi.category.CartResponse;

public interface CartService {

    CartItemResponse addItemToCart(AddToCartRequest addToCartRequest);

    CartResponse getCart();

    CartItemResponse updateCart(Long productId, UpdateCartRequest updateCartRequest);

    void removeItemFromCart(Long productId);

    void clearCart();
}
