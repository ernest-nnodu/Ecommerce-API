package com.jackalcode.ecommerceapi.services;

import com.jackalcode.ecommerceapi.dtos.requests.AddToCartRequest;
import com.jackalcode.ecommerceapi.dtos.requests.UpdateCartRequest;
import com.jackalcode.ecommerceapi.dtos.responses.CartItemResponse;
import com.jackalcode.ecommerceapi.dtos.responses.CartResponse;

public interface CartService {

    CartItemResponse addItemToCart(Long cartId, AddToCartRequest addToCartRequest);

    CartResponse getCart(Long cartId);

    CartItemResponse updateCart(Long cartId, Long productId, UpdateCartRequest updateCartRequest);

    void removeItemFromCart(Long cartId, Long productId);
}
