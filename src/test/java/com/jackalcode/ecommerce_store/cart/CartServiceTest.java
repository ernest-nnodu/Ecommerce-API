package com.jackalcode.ecommerce_store.cart;

import com.jackalcode.ecommerce_store.customer.Customer;
import com.jackalcode.ecommerce_store.exceptions.ProductNotInCartException;
import com.jackalcode.ecommerce_store.product.Product;
import com.jackalcode.ecommerce_store.product.ProductRepository;
import com.jackalcode.ecommerce_store.security.AuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
        var customer = getCustomer();

        var cart = getEmptyCart(1L, customer);

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
        var customer = getCustomer();

        var cart = getEmptyCart(2L, customer);

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
    @DisplayName("getCartWithItems: returns mapped CartResponse for a cart with items")
    void getCart_withItems_returnsMappedCartResponse() {

        var customer = getCustomer();

        var cart = getCartWithItems(customer);

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
    @DisplayName("getCartWithItems: returns empty CartResponse when cart has no items")
    void getCart_returnsEmptyCart_whenNoItems() {

        var customer = getCustomer();

        var cart = getEmptyCart(200L, customer);
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

    @Test
    @DisplayName("updateCart: updates item quantity when item exists and returns updated response")
    void updateCart_whenItemExists_updatesQuantityAndReturnsResponse() {
        
        Long productId = 20L;
        var customer = getCustomer();

        var cart = getEmptyCart(2L, customer);

        var product = new Product();
        product.setId(productId);
        product.setName("Book");
        product.setPrice(new BigDecimal("9.99"));

        // create existing CartItem with quantity 2 and add to cart
        var existingItem = new CartItem();
        existingItem.setId(3L);
        existingItem.setProduct(product);
        existingItem.setQuantity(2);
        cart.addItem(existingItem);

        when(authenticationService.getCurrentCustomer()).thenReturn(customer);
        when(cartRepository.findByCustomerId(customer.getId())).thenReturn(cart);
        when(cartRepository.save(any(Cart.class))).thenAnswer(
                invocation -> invocation.getArgument(0));

        var request = new UpdateCartRequest(5);

        CartItemResponse response = cartService.updateCart(productId, request);

        // Assert: quantity updated to 5 and total price = 9.99 * 5 = 49.95
        assertAll(
                () -> assertNotNull(response),
                () -> assertEquals(productId, response.product().id()),
                () -> assertEquals(5, response.quantity()),
                () -> assertEquals(new BigDecimal("49.95"), response.totalPrice())
        );

        verify(authenticationService).getCurrentCustomer();
        verify(cartRepository).findByCustomerId(customer.getId());
        verify(cartRepository).save(any(Cart.class));
    }

    @Test
    @DisplayName("updateCart: when item not in cart, throws ProductNotInCartException")
    void updateCart_whenItemNotInCart_throwsException() {

        Long productId = 99L;
        var customer = getCustomer();

        var cart = getEmptyCart(3L, customer);
        // no items added to cart

        when(authenticationService.getCurrentCustomer()).thenReturn(customer);
        when(cartRepository.findByCustomerId(customer.getId())).thenReturn(cart);

        var request = new UpdateCartRequest(2);

        assertThrows(ProductNotInCartException.class,
                () -> cartService.updateCart(productId, request));

        verify(authenticationService).getCurrentCustomer();
        verify(cartRepository).findByCustomerId(customer.getId());
        verify(cartRepository, never()).save(any());
    }

    private Customer getCustomer() {
        return Customer.builder()
                .id(2L)
                .firstName("Alice")
                .lastName("Smith")
                .email("alice@mail.com")
                .build();
    }

    @Test
    @DisplayName("removeItemFromCart: removes existing item and saves updated cart")
    void removeItemFromCart_whenItemExists_removesExistingItemAndSavesCart() {

        var customer = getCustomer();
        var cart = getCartWithItems(customer); // contains items for product ids 10 and 20

        when(authenticationService.getCurrentCustomer()).thenReturn(customer);
        when(cartRepository.findByCustomerId(customer.getId())).thenReturn(cart);
        when(cartRepository.save(any(Cart.class))).thenAnswer(
                invocation -> invocation.getArgument(0));

        cartService.removeItemFromCart(10L);

        // Assert - capture savedCart cart, and verify item removed
        ArgumentCaptor<Cart> captor = ArgumentCaptor.forClass(Cart.class);
        verify(cartRepository).save(captor.capture());
        Cart savedCart = captor.getValue();

        // The item with productId 10 should be removed; only productId 20 remains
        assertNull(savedCart.getCartItem(10L), "Expected productId 10 to be removed from cart");
        assertNotNull(savedCart.getCartItem(20L), "Expected productId 20 to remain in cart");
        assertEquals(1, savedCart.getCartItems().size(), "Expected one item to remain in the cart");

        verify(authenticationService).getCurrentCustomer();
        verify(cartRepository).findByCustomerId(customer.getId());
        verify(cartRepository).save(savedCart);
    }

    @Test
    @DisplayName("removeItemFromCart: when item not in cart, throws ProductNotInCartException")
    void removeItemFromCart_whenItemNotInCart_throwsException() {

        var customer = getCustomer();
        var cart = getEmptyCart(3L, customer);
        // no items added to the cart

        when(authenticationService.getCurrentCustomer()).thenReturn(customer);
        when(cartRepository.findByCustomerId(customer.getId())).thenReturn(cart);

        assertThrows(
                ProductNotInCartException.class, () -> cartService.removeItemFromCart(99L));

        verify(authenticationService).getCurrentCustomer();
        verify(cartRepository).findByCustomerId(customer.getId());
        verify(cartRepository, never()).save(any());
    }

    @Test
    @DisplayName("clearCart: clears items from a populated cart and saves it")
    void clearCart_withItems_clearsAndSavesCart() {

        var customer = getCustomer();
        var cart = getCartWithItems(customer); // contains two items

        when(authenticationService.getCurrentCustomer()).thenReturn(customer);
        when(cartRepository.findByCustomerId(customer.getId())).thenReturn(cart);
        when(cartRepository.save(any(Cart.class))).thenAnswer(
                invocation -> invocation.getArgument(0));

        cartService.clearCart();

        // Assert - capture saved cart and verify it's empty
        ArgumentCaptor<Cart> captor = ArgumentCaptor.forClass(Cart.class);
        verify(cartRepository).save(captor.capture());
        Cart savedCart = captor.getValue();

        assertAll(
                () -> assertNotNull(savedCart),
                () -> assertTrue(savedCart.getCartItems().isEmpty(), "Expected no cart items after clear"),
                () -> assertNull(savedCart.getCartItem(10L), "No item should remain for productId 10"),
                () -> assertNull(savedCart.getCartItem(20L), "No item should remain for productId 20")
        );

        verify(authenticationService).getCurrentCustomer();
        verify(cartRepository).findByCustomerId(customer.getId());
        verify(cartRepository).save(savedCart);
    }

    @Test
    @DisplayName("clearCart: when cart is already empty, still saves empty cart")
    void clearCart_whenAlreadyEmpty_savesEmptyCart() {

        var customer = getCustomer();
        var cart = getEmptyCart(3L, customer);
        // No items added to the cart

        when(authenticationService.getCurrentCustomer()).thenReturn(customer);
        when(cartRepository.findByCustomerId(customer.getId())).thenReturn(cart);
        when(cartRepository.save(any(Cart.class))).thenAnswer(
                invocation -> invocation.getArgument(0));

        cartService.clearCart();

        // Assert - saved cart remains empty
        ArgumentCaptor<Cart> captor = ArgumentCaptor.forClass(Cart.class);
        verify(cartRepository).save(captor.capture());
        Cart saved = captor.getValue();

        assertAll(
                () -> assertNotNull(saved),
                () -> assertTrue(saved.getCartItems().isEmpty(), "Expected cart to remain empty"),
                () -> assertEquals(3L, saved.getId())
        );

        verify(authenticationService).getCurrentCustomer();
        verify(cartRepository).findByCustomerId(customer.getId());
    }

    private Cart getEmptyCart(long id, Customer customer) {
        var cart = new Cart();
        cart.setId(id);
        cart.setCustomer(customer);
        return cart;
    }

    private Cart getCartWithItems(Customer customer) {
        var cart = getEmptyCart(100L, customer);

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
