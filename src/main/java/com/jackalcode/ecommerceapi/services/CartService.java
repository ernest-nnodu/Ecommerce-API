package com.jackalcode.ecommerceapi.services;

import com.jackalcode.ecommerceapi.dtos.requests.AddToCartRequest;
import com.jackalcode.ecommerceapi.dtos.requests.UpdateCartRequest;
import com.jackalcode.ecommerceapi.dtos.responses.CartItemResponse;
import com.jackalcode.ecommerceapi.dtos.responses.CartResponse;

public interface CartService {

    CartItemResponse addItemToCart(AddToCartRequest addToCartRequest);

    CartResponse getCart();

    CartItemResponse updateCart(Long productId, UpdateCartRequest updateCartRequest);

    void removeItemFromCart(Long productId);

    void clearCart();
}
