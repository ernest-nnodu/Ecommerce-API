package com.jackalcode.ecommerce_store.cart;

import com.jackalcode.ecommerce_store.category.Category;
import com.jackalcode.ecommerce_store.customer.Customer;
import com.jackalcode.ecommerce_store.product.Product;
import com.jackalcode.ecommerce_store.security.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class CartRepositoryTest {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("findByCustomerId should return cart when cart exists")
     void findByCustomerId_whenCartExists_returnsCart() {

         var customer = createCustomer();
         var cart = new Cart();
         cart.setCustomer(customer);

         entityManager.persist(customer);
         entityManager.persist(cart);
         entityManager.flush();
         entityManager.clear();

         var foundCart = cartRepository.findByCustomerId(customer.getId());

         assertNotNull(foundCart);
         assertEquals(customer.getId(), foundCart.getCustomer().getId());

    }

    @Test
    @DisplayName("findByCustomerId should return null when cart does not exist")
    void findByCustomerId_whenCartDoesNotExist_returnsNull() {

        var customer = createCustomer();
        entityManager.persist(customer);
        entityManager.flush();
        entityManager.clear();

        var foundCart = cartRepository.findByCustomerId(customer.getId());

        assertNull(foundCart);
    }

    @Test
    @DisplayName("save should persist cart items with the cart")
    void save_whenCartHasItems_persistsCartItems() {
        var customer = createCustomer();
        var product = createProduct("Headphones");
        var cart = new Cart();
        cart.setCustomer(customer);

        var cartItem = new CartItem();
        cartItem.setProduct(product);
        cartItem.setQuantity(2);
        cart.addItem(cartItem);

        persistToDatabase(customer);
        persistToDatabase(product.getCategory());
        persistToDatabase(product);

        var savedCart = cartRepository.save(cart);
        entityManager.flush();
        entityManager.clear();

        var foundCart = cartRepository.findById(savedCart.getId());

        assertAll(
                () -> assertTrue(foundCart.isPresent()),
                () -> assertEquals(customer.getId(), foundCart.get().getCustomer().getId()),
                () -> assertEquals(1, foundCart.get().getCartItems().size()),
                () -> assertTrue(foundCart.get().getCartItems().stream()
                        .anyMatch(item -> item.getProduct().getId().equals(product.getId())
                                && item.getQuantity() == 2))
        );
    }

    private void persistToDatabase(Object entity) {
        entityManager.persist(entity);
        entityManager.flush();
        entityManager.clear();
    }

    private Customer createCustomer() {

         return Customer.builder()
                 .firstName("John")
                 .lastName("Doe")
                 .email("john.doe@email.com")
                 .password("password")
                 .role(Role.USER)
                 .build();
    }

    private Product createProduct(String name) {
        var category = new Category();
        category.setName("Electronics");

        var product = new Product();
        product.setName(name);
        product.setPrice(new BigDecimal("19.99"));
        product.setQuantityInStock(10L);
        product.setCategory(category);

        return product;
    }
}
