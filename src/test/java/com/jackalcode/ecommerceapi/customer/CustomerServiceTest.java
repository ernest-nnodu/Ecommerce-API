package com.jackalcode.ecommerceapi.customer;

import com.jackalcode.ecommerceapi.cart.Cart;
import com.jackalcode.ecommerceapi.cart.CartRepository;
import com.jackalcode.ecommerceapi.exceptions.CustomerAlreadyExistException;
import com.jackalcode.ecommerceapi.exceptions.CustomerNotFoundException;
import com.jackalcode.ecommerceapi.security.AuthenticationService;
import com.jackalcode.ecommerceapi.security.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

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

        Customer customer = createCustomerEntity(1L, "John", "Doe",
                "john.doe@mail.com");

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
    @DisplayName("registerCustomer: when email already exists, " +
            "throws CustomerAlreadyExistException")
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

    @Test
    @DisplayName("getCustomers: returns list of mapped CustomerResponse objects")
    void getCustomers_returnsMappedCustomerResponses() {

        Customer c1 = createCustomerEntity(1L, "John", "Doe",
                "john.doe@mail.com");

        Customer c2 = createCustomerEntity(2L, "Jane", "Smith",
                "jane.smith@mail.com");

        CustomerResponse r1 = createCustomerResponse(
                1L, "John", "Doe", "john.doe@mail.com");
        CustomerResponse r2 = createCustomerResponse(
                2L, "Jane", "Smith", "jane.smith@mail.com");

        when(customerRepository.findAll()).thenReturn(List.of(c1, c2));
        when(customerMapper.toCustomerResponse(c1)).thenReturn(r1);
        when(customerMapper.toCustomerResponse(c2)).thenReturn(r2);

        List<CustomerResponse> results = customerService.getCustomers();

        assertAll(
                () -> assertNotNull(results),
                () -> assertEquals(2, results.size()),
                () -> assertEquals("john.doe@mail.com", results.get(0).email()),
                () -> assertEquals("jane.smith@mail.com", results.get(1).email())
        );

        verify(customerRepository).findAll();
        verify(customerMapper).toCustomerResponse(c1);
        verify(customerMapper).toCustomerResponse(c2);
    }

    @Test
    @DisplayName("getCustomers: returns empty list when repository has no customers")
    void getCustomers_returnsEmptyList_whenNoCustomers() {

        when(customerRepository.findAll()).thenReturn(List.of());

        List<CustomerResponse> results = customerService.getCustomers();

        assertAll(
                () -> assertNotNull(results),
                () -> assertTrue(results.isEmpty(), "Expected empty customer list")
        );

        verify(customerRepository).findAll();
        verify(customerMapper, never()).toCustomerResponse(any());
    }

    @Test
    @DisplayName("getCustomer: returns current authenticated customer response")
    void getCustomer_returnsCurrentAuthenticatedCustomer() {

        Customer current = createCustomerEntity(1L, "Alice", "Walker",
                "alice.walker@mail.com");
        CustomerResponse expected = createCustomerResponse(1L, "Alice", "Walker",
                "alice.walker@mail.com");

        when(authenticationService.getCurrentCustomer()).thenReturn(current);
        when(customerMapper.toCustomerResponse(current)).thenReturn(expected);

        CustomerResponse result = customerService.getCustomer();

        // Assert
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(1L, result.id()),
                () -> assertEquals("Alice", result.firstName()),
                () -> assertEquals("Walker", result.lastName()),
                () -> assertEquals("alice.walker@mail.com", result.email())
        );

        verify(authenticationService).getCurrentCustomer();
        verify(customerMapper).toCustomerResponse(current);
    }

    @Test
    @DisplayName("getCustomer: when no authenticated customer, throws CustomerNotFoundException")
    void getCustomer_whenNoAuthenticatedCustomer_throwsException() {

        when(authenticationService.getCurrentCustomer())
                .thenThrow(new CustomerNotFoundException("Customer not found"));

        assertThrows(CustomerNotFoundException.class,
                () -> customerService.getCustomer());

        verify(authenticationService).getCurrentCustomer();
        verifyNoInteractions(customerMapper);
    }

    private Customer createCustomerEntity(Long id, String firstName, String lastName,
                                          String email) {
        Customer customer = new Customer();
        customer.setId(id);
        customer.setFirstName(firstName);
        customer.setLastName(lastName);
        customer.setEmail(email);
        customer.setPassword("Password123?");
        customer.setRole(Role.USER);

        return customer;
    }

    private CustomerResponse createCustomerResponse(Long id, String firstName, String lastName,
            String email) {

        return new CustomerResponse(id, firstName, lastName, email);
    }
}

