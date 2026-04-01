package com.jackalcode.ecommerceapi.services;

import com.jackalcode.ecommerceapi.dtos.requests.AddToCartRequest;
import com.jackalcode.ecommerceapi.dtos.responses.CartItemResponse;

public interface CartService {

    CartItemResponse addItemToCart(Long cartId, AddToCartRequest addToCartRequest);
}
