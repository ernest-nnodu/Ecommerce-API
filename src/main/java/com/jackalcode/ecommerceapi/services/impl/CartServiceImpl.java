package com.jackalcode.ecommerceapi.services.impl;

import com.jackalcode.ecommerceapi.dtos.requests.AddToCartRequest;
import com.jackalcode.ecommerceapi.dtos.responses.CartItemResponse;
import com.jackalcode.ecommerceapi.entities.CartItem;
import com.jackalcode.ecommerceapi.exceptions.CartNotFoundException;
import com.jackalcode.ecommerceapi.exceptions.ProductNotFoundException;
import com.jackalcode.ecommerceapi.mappers.CartMapper;
import com.jackalcode.ecommerceapi.repositories.CartRepository;
import com.jackalcode.ecommerceapi.repositories.ProductRepository;
import com.jackalcode.ecommerceapi.services.CartService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final CartMapper cartMapper;

    @Override
    @Transactional
    public CartItemResponse addItemToCart(Long cartId, AddToCartRequest addToCartRequest) {

        var cart = cartRepository.findById(cartId).orElseThrow(
                () -> new CartNotFoundException("Cart not found with id: " + cartId)
        );

        var product = productRepository.findById(addToCartRequest.productId()).orElseThrow(
                () -> new ProductNotFoundException("Product not found with id: " + addToCartRequest.productId())
        );

        var item = cart.getCartItems().stream()
                .filter(cartItem -> cartItem.getProduct().getId().equals(product.getId()))
                .findFirst().orElse(null);

        if (item == null) {
            item = new CartItem();
            item.setProduct(product);
            item.setQuantity(1);
            cart.addItem(item);
        } else {
            item.setQuantity(item.getQuantity() + 1);
        }

        cartRepository.save(cart);
        return cartMapper.toCartItemResponse(item);
    }
}