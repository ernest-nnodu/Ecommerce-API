package com.jackalcode.ecommerce_store.cart;

import com.jackalcode.ecommerce_store.customer.Customer;
import com.jackalcode.ecommerce_store.security.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

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

    private Customer createCustomer() {

         return Customer.builder()
                 .firstName("John")
                 .lastName("Doe")
                 .email("john.doe@email.com")
                 .password("password")
                 .role(Role.USER)
                 .build();
    }
}
