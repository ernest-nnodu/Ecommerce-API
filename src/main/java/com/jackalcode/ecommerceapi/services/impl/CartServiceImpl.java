package com.jackalcode.ecommerceapi.services.impl;

import com.jackalcode.ecommerceapi.dtos.requests.AddToCartRequest;
import com.jackalcode.ecommerceapi.dtos.requests.UpdateCartRequest;
import com.jackalcode.ecommerceapi.dtos.responses.CartItemResponse;
import com.jackalcode.ecommerceapi.dtos.responses.CartResponse;
import com.jackalcode.ecommerceapi.entities.Cart;
import com.jackalcode.ecommerceapi.entities.CartItem;
import com.jackalcode.ecommerceapi.entities.Product;
import com.jackalcode.ecommerceapi.exceptions.CartNotFoundException;
import com.jackalcode.ecommerceapi.exceptions.ProductNotFoundException;
import com.jackalcode.ecommerceapi.exceptions.ProductNotInCartException;
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
    private final AuthenticationService authenticationService;

    @Override
    @Transactional
    public CartItemResponse addItemToCart(AddToCartRequest addToCartRequest) {

        var currentCustomer = authenticationService.getCurrentCustomer();
        var cart = cartRepository.findByCustomerId(currentCustomer.getId());

        var product = getProductEntity(addToCartRequest.productId());

        var item = cart.getCartItem(addToCartRequest.productId());

        //If item exist in the cart, increase its quantity by 1, else add the item to the cart
        if (!(item == null)) {
            item.setQuantity(item.getQuantity() + 1);
        } else {
            item = new CartItem();
            item.setProduct(product);
            item.setQuantity(1);
            cart.addItem(item);
        }

        //Persist cart to database
        cartRepository.save(cart);
        return cartMapper.toCartItemResponse(item);
    }

    @Override
    public CartResponse getCart() {

        var currentCustomer = authenticationService.getCurrentCustomer();
        var cart = cartRepository.findByCustomerId(currentCustomer.getId());

        return cartMapper.toCartResponse(cart);
    }

    @Override
    public CartItemResponse updateCart(Long productId, UpdateCartRequest updateCartRequest) {

        var currentCustomer = authenticationService.getCurrentCustomer();
        var cart = cartRepository.findByCustomerId(currentCustomer.getId());
        var item = cart.getCartItem(productId);

        if (item == null) {
            throw new ProductNotInCartException("Cart do not contain product with id:  " +
                    productId);
        } else {
            item.setQuantity(updateCartRequest.quantity());
        }

        cartRepository.save(cart);
        return cartMapper.toCartItemResponse(item);
    }

    @Override
    public void removeItemFromCart(Long productId) {

        var currentCustomer = authenticationService.getCurrentCustomer();
        var cart = cartRepository.findByCustomerId(currentCustomer.getId());
        var item = cart.getCartItem(productId);

        if (item == null) {
            throw new ProductNotInCartException("Cart do not contain product with id:  " +
                    productId);
        } else {
            cart.removeItem(item);
        }

        cartRepository.save(cart);
    }

    @Override
    public void clearCart() {

        var currentCustomer = authenticationService.getCurrentCustomer();
        var cart = cartRepository.findByCustomerId(currentCustomer.getId());
        cart.clearItems();
        cartRepository.save(cart);
    }

    private Product getProductEntity(Long productId) {
        return productRepository.findById(productId).orElseThrow(
                () -> new ProductNotFoundException("Product not found with id: " + productId)
        );
    }
}