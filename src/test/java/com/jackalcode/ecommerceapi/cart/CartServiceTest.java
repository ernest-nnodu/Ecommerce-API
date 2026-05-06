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
    @DisplayName("addItemToCart: when item already in cart, " +
            "increments quantity and returns updated response")
    void addItemToCart_whenItemExists_incrementsQuantity() {

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

    @Test
    @DisplayName("getCart: returns mapped CartResponse for a cart with items")
    void getCart_withItems_returnsMappedCartResponse() {

        var customer = Customer.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john@mail.com")
                .build();

        var cart = getCart(100L, customer);

        when(authenticationService.getCurrentCustomer()).thenReturn(customer);
        when(cartRepository.findByCustomerId(customer.getId())).thenReturn(cart);

        CartResponse resp = cartService.getCart();

        assertAll(
                () -> assertNotNull(resp),
                () -> assertEquals(100L, resp.id()),
                () -> assertNotNull(resp.items()),
                () -> assertEquals(2, resp.items().size()),
                // product checks for first item (order from Set is not guaranteed; map returned list ordering depends on mapper implementation)
                () -> assertTrue(resp.items().stream().anyMatch(i -> i.product().id().equals(10L) && i.product().name().equals("Phone"))),
                () -> assertTrue(resp.items().stream().anyMatch(i -> i.product().id().equals(20L) && i.product().name().equals("Book"))),
                // total price = 199.99*1 + 9.99*3 = 199.99 + 29.97 = 229.96
                () -> assertEquals(new BigDecimal("229.96"), resp.totalPrice())
        );

        verify(authenticationService).getCurrentCustomer();
        verify(cartRepository).findByCustomerId(customer.getId());
    }

    @Test
    @DisplayName("getCart: returns empty CartResponse when cart has no items")
    void getCart_returnsEmptyCart_whenNoItems() {

        var customer = Customer.builder()
                .id(2L)
                .firstName("Alice")
                .lastName("Smith")
                .email("alice@mail.com")
                .build();

        var cart = new Cart();
        cart.setId(200L);
        cart.setCustomer(customer);
        // no items added

        when(authenticationService.getCurrentCustomer()).thenReturn(customer);
        when(cartRepository.findByCustomerId(customer.getId())).thenReturn(cart);

        CartResponse resp = cartService.getCart();

        assertAll(
                () -> assertNotNull(resp),
                () -> assertEquals(200L, resp.id()),
                () -> assertNotNull(resp.items()),
                () -> assertTrue(resp.items().isEmpty(), "Expected no items"),
                () -> assertEquals(new BigDecimal("0"), resp.totalPrice())
        );

        verify(authenticationService).getCurrentCustomer();
        verify(cartRepository).findByCustomerId(customer.getId());
    }

    private Cart getCart(Long id, Customer customer) {
        var cart = new Cart();
        cart.setId(id);
        cart.setCustomer(customer);

        var p1 = new Product();
        p1.setId(10L);
        p1.setName("Phone");
        p1.setPrice(new BigDecimal("199.99"));

        var p2 = new Product();
        p2.setId(20L);
        p2.setName("Book");
        p2.setPrice(new BigDecimal("9.99"));

        var item1 = new CartItem();
        item1.setProduct(p1);
        item1.setQuantity(1);

        var item2 = new CartItem();
        item2.setProduct(p2);
        item2.setQuantity(3);

        //Set item id to allow adding to Set without being considered duplicate (equals/hashCode based on id)
        item1.setId(1L);
        item2.setId(2L);

        cart.addItem(item1);
        cart.addItem(item2);

        return cart;
    }
}
