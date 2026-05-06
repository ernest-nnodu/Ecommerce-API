package com.jackalcode.ecommerceapi.cart;

import com.jackalcode.ecommerceapi.customer.Customer;
import com.jackalcode.ecommerceapi.product.Product;
import com.jackalcode.ecommerceapi.product.ProductRepository;
import com.jackalcode.ecommerceapi.security.AuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CartServiceTest {

    private CartService cartService;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private AuthenticationService authenticationService;

    private final CartMapper cartMapper = Mappers.getMapper(CartMapper.class);

    @BeforeEach
    void setup() {
        cartService = new CartServiceImpl(
                cartRepository, productRepository, cartMapper,  authenticationService);
    }

    @Test
    @DisplayName("addItemToCart: when item not in cart, creates item with quantity 1 and returns response")
    void addItemToCart_createsNewItemAndReturnsResponse() {

        Long productId = 10L;
        var customer = Customer.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john@mail.com")
                .build();

        var cart = new Cart();
        cart.setId(1L);
        cart.setCustomer(customer);

        var product = new Product();
        product.setId(productId);
        product.setName("Phone");
        product.setPrice(new BigDecimal("199.99"));

        when(authenticationService.getCurrentCustomer()).thenReturn(customer);
        when(cartRepository.findByCustomerId(customer.getId())).thenReturn(cart);
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(cartRepository.save(any(Cart.class))).thenAnswer(
                invocation -> invocation.getArgument(0));

        var request = new AddToCartRequest(productId);

        CartItemResponse response = cartService.addItemToCart(request);

        assertAll(
                () -> assertNotNull(response),
                () -> assertNotNull(response.product()),
                () -> assertEquals(productId, response.product().id()),
                () -> assertEquals("Phone", response.product().name()),
                () -> assertEquals(new BigDecimal("199.99"), response.product().price()),
                () -> assertEquals(1, response.quantity()),
                () -> assertEquals(new BigDecimal("199.99"), response.totalPrice())
        );

        verify(authenticationService).getCurrentCustomer();
        verify(cartRepository).findByCustomerId(customer.getId());
        verify(productRepository).findById(productId);
        verify(cartRepository).save(any(Cart.class));
    }

    @Test
    @DisplayName("addItemToCart: when item already in cart, increments quantity and returns updated response")
    void addItemToCart_incrementsQuantityWhenItemExists() {

        Long productId = 20L;
        var customer = Customer.builder()
                .id(2L)
                .firstName("Alice")
                .lastName("Smith")
                .email("alice@mail.com")
                .build();

        var cart = new Cart();
        cart.setId(2L);
        cart.setCustomer(customer);

        var product = new Product();
        product.setId(productId);
        product.setName("Book");
        product.setPrice(new BigDecimal("9.99"));

        // create existing CartItem with quantity 2 and add to cart
        var existingItem = new CartItem();
        existingItem.setProduct(product);
        existingItem.setQuantity(2);
        cart.addItem(existingItem);

        when(authenticationService.getCurrentCustomer()).thenReturn(customer);
        when(cartRepository.findByCustomerId(customer.getId())).thenReturn(cart);
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(cartRepository.save(any(Cart.class))).thenAnswer(
                invocation -> invocation.getArgument(0));

        var request = new AddToCartRequest(productId);


        CartItemResponse response = cartService.addItemToCart(request);

        // Assert: quantity should have been incremented from 2 -> 3
        assertAll(
                () -> assertNotNull(response),
                () -> assertEquals(productId, response.product().id()),
                () -> assertEquals(3, response.quantity()),
                () -> assertEquals(new BigDecimal("29.97"), response.totalPrice()) // 9.99 * 3
        );

        verify(authenticationService).getCurrentCustomer();
        verify(cartRepository).findByCustomerId(customer.getId());
        verify(productRepository).findById(productId);
        verify(cartRepository).save(any(Cart.class));
    }
}
