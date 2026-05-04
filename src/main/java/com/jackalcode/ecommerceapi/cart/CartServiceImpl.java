package com.jackalcode.ecommerceapi.cart;

import com.jackalcode.ecommerceapi.product.Product;
import com.jackalcode.ecommerceapi.exceptions.ProductNotFoundException;
import com.jackalcode.ecommerceapi.exceptions.ProductNotInCartException;
import com.jackalcode.ecommerceapi.product.ProductRepository;
import com.jackalcode.ecommerceapi.security.AuthenticationService;
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