package com.jackalcode.ecommerceapi.customer;

import com.jackalcode.ecommerceapi.cart.Cart;
import com.jackalcode.ecommerceapi.cart.CartRepository;
import com.jackalcode.ecommerceapi.exceptions.CustomerAlreadyExistException;
import com.jackalcode.ecommerceapi.security.AuthenticationService;
import com.jackalcode.ecommerceapi.security.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest {

    @Mock
    CustomerRepository customerRepository;

    @Mock
    CartRepository cartRepository;

    @Mock
    CustomerMapper customerMapper;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    AuthenticationService authenticationService;

    @InjectMocks
    CustomerServiceImpl customerService;

    @Test
    @DisplayName("registerCustomer: given valid request, " +
            "saves customer with hashed password and creates cart")
    void registerCustomer_withValidCredentials_returnsCustomer() {

        RegisterCustomerRequest request = new RegisterCustomerRequest(
                "John", "Doe", "john.doe@mail.com", "Password123?");

        Customer customer = createCustomerEntity();

        CustomerResponse customerResponse = new CustomerResponse(
                customer.getId(),
                customer.getFirstName(),
                customer.getLastName(),
                customer.getEmail()
        );

        Cart cart = new Cart();
        cart.setCustomer(customer);

        when(customerRepository.existsByEmail(request.email())).thenReturn(false);
        when(passwordEncoder.encode(request.password())).thenReturn("hashedPassword");
        when(customerMapper.toCustomer(request)).thenReturn(customer);
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);
        when(customerMapper.toCustomerResponse(customer)).thenReturn(customerResponse);

        CustomerResponse response = customerService.registerCustomer(request);

        assertAll(
                () -> assertNotNull(response),
                () -> assertEquals("John", response.firstName()),
                () -> assertEquals("Doe", response.lastName()),
                () -> assertEquals("john.doe@mail.com", response.email())
        );

        verify(customerRepository).save(any(Customer.class));
        verify(cartRepository).save(any(Cart.class));
        verify(passwordEncoder).encode("Password123?");
    }

    @Test
    void registerCustomer_withExistingEmail_throwsException() {
        RegisterCustomerRequest request = new RegisterCustomerRequest(
                "John", "Doe", "john.doe@mail.com", "Password123?");

        when(customerRepository.existsByEmail(request.email())).thenReturn(true);

        assertThrows(CustomerAlreadyExistException.class, () ->
            customerService.registerCustomer(request)
        );

        verify(customerRepository).existsByEmail(request.email());
        verifyNoInteractions(customerMapper, passwordEncoder, cartRepository);

    }

    private Customer createCustomerEntity() {
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setFirstName("John");
        customer.setLastName("Doe");
        customer.setEmail("john.doe@mail.com");
        customer.setPassword("Password123?");
        customer.setRole(Role.USER);
        return customer;

    }
}

