package com.jackalcode.ecommerce_store.customer;

import com.jackalcode.ecommerce_store.security.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.junit.jupiter.api.Assertions.*;

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

    @Test
    @DisplayName("findByEmail should return customer when email exists in the database")
    void findByEmail_whenEmailExists_returnsCustomer() {
        var customer = createCustomerEntity();
        persistToDatabase(customer);

        var response = customerRepository.findByEmail(customer.getEmail());

        assertAll(
                () -> assertTrue(response.isPresent()),
                () -> assertEquals(customer.getEmail(), response.get().getEmail()),
                () -> assertEquals(customer.getFirstName(), response.get().getFirstName()),
                () -> assertEquals(customer.getLastName(), response.get().getLastName()),
                () -> assertEquals(customer.getRole(), response.get().getRole())
        );
    }

    @Test
    @DisplayName("findByEmail should return empty optional when email does not exist in the database")
    void findByEmail_whenEmailNotExists_returnsEmptyOptional() {

        var response = customerRepository.findByEmail("fake@email.com");

        assertFalse(response.isPresent());
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
