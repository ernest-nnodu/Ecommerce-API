package com.jackalcode.ecommerce_store.customer;

import com.jackalcode.ecommerce_store.security.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("existsByEmail should return true when email exists in the database")
    void existsByEmail_whenEmailExists_returnsTrue() {

        var customer = createCustomerEntity();

        persistToDatabase(customer);

        var response = customerRepository.existsByEmail(customer.getEmail());

        assertTrue(response);
    }

    @Test
    @DisplayName("existsByEmail should return false when email does not exist in the database")
    void existsByEmail_whenEmailNotExists_returnsFalse() {

        var response = customerRepository.existsByEmail("fake@email.com");

        assertFalse(response);
    }

    private void persistToDatabase(Customer customer) {
        entityManager.persist(customer);
        entityManager.flush();
        entityManager.clear();
    }

    private Customer createCustomerEntity() {
        return Customer.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@email.com")
                .password("password")
                .role(Role.USER)
                .build();
    }
}
