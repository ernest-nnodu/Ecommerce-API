package com.jackalcode.ecommerce_store.customer;

import com.jackalcode.ecommerce_store.cart.Cart;
import com.jackalcode.ecommerce_store.cart.CartRepository;
import com.jackalcode.ecommerce_store.exceptions.CustomerAlreadyExistException;
import com.jackalcode.ecommerce_store.exceptions.CustomerNotFoundException;
import com.jackalcode.ecommerce_store.security.AuthenticationService;
import com.jackalcode.ecommerce_store.security.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest {

    @Mock
    CustomerRepository customerRepository;

    @Mock
    CartRepository cartRepository;

    CustomerMapper customerMapper = Mappers.getMapper(CustomerMapper.class);

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    AuthenticationService authenticationService;

    CustomerServiceImpl customerService;

    @BeforeEach
    void setUp() {
        customerService = new CustomerServiceImpl(
                customerRepository,
                cartRepository,
                customerMapper, // real mapper
                passwordEncoder,
                authenticationService
        );
    }

    @Test
    @DisplayName("registerCustomer: given valid request, " +
            "saves customer with hashed password and creates cart")
    void registerCustomer_withValidCredentials_returnsCustomer() {

        RegisterCustomerRequest request = new RegisterCustomerRequest(
                "John", "Doe", "john.doe@mail.com", "Password123?");

        Customer customer = createCustomerEntity(1L, "John", "Doe",
                "john.doe@mail.com");

        Cart cart = new Cart();
        cart.setCustomer(customer);

        when(customerRepository.existsByEmail(request.email())).thenReturn(false);
        when(passwordEncoder.encode(request.password())).thenReturn("hashedPassword");
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

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
        verifyNoInteractions(passwordEncoder, cartRepository);

    }

    @Test
    @DisplayName("getCustomers: returns list of mapped CustomerResponse objects")
    void getCustomers_returnsMappedCustomerResponses() {

        Customer c1 = createCustomerEntity(1L, "John", "Doe",
                "john.doe@mail.com");

        Customer c2 = createCustomerEntity(2L, "Jane", "Smith",
                "jane.smith@mail.com");

        when(customerRepository.findAll()).thenReturn(List.of(c1, c2));

        List<CustomerResponse> results = customerService.getCustomers();

        assertAll(
                () -> assertNotNull(results),
                () -> assertEquals(2, results.size()),
                () -> assertEquals("john.doe@mail.com", results.get(0).email()),
                () -> assertEquals("jane.smith@mail.com", results.get(1).email())
        );

        verify(customerRepository).findAll();
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
    }

    @Test
    @DisplayName("getCustomer: returns current authenticated customer response")
    void getCustomer_returnsCurrentAuthenticatedCustomer() {

        Customer current = createCustomerEntity(1L, "Alice", "Walker",
                "alice.walker@mail.com");

        when(authenticationService.getCurrentCustomer()).thenReturn(current);

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
    }

    @Test
    @DisplayName("getCustomer: when no authenticated customer, throws CustomerNotFoundException")
    void getCustomer_whenNoAuthenticatedCustomer_throwsException() {

        when(authenticationService.getCurrentCustomer())
                .thenThrow(new CustomerNotFoundException("Customer not found"));

        assertThrows(CustomerNotFoundException.class,
                () -> customerService.getCustomer());

        verify(authenticationService).getCurrentCustomer();
    }

    @Test
    @DisplayName("getCustomerById: returns mapped CustomerResponse when customer exists")
    void getCustomerById_whenCustomerExists_returnsMappedResponse() {

        Long customerId = 1L;
        Customer customer = createCustomerEntity(customerId, "Tom", "Brown",
                "tom.brown@mail.com");

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        CustomerResponse result = customerService.getCustomerById(customerId);

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(customerId, result.id()),
                () -> assertEquals("Tom", result.firstName()),
                () -> assertEquals("Brown", result.lastName()),
                () -> assertEquals("tom.brown@mail.com", result.email())
        );

        verify(customerRepository).findById(customerId);
    }

    @Test
    @DisplayName("getCustomerById: throws CustomerNotFoundException when customer does not exist")
    void getCustomerById_whenCustomerNotFound_throwsException() {

        Long missingId = 999L;
        when(customerRepository.findById(missingId)).thenReturn(Optional.empty());

        assertThrows(CustomerNotFoundException.class,
                () -> customerService.getCustomerById(missingId));

        verify(customerRepository).findById(missingId);
    }

    @Test
    @DisplayName("updateCustomer: updates current customer when valid and returns updated customer")
    void updateCustomer_withValidRequest_updatesAndReturnsCustomer() {

        Customer current = createCustomerEntity(1L, "OldFirst", "OldLast",
                "old.email@mail.com");
        UpdateCustomerRequest updateRequest = new UpdateCustomerRequest("NewFirst",
                "NewLast", "new.email@mail.com");

        // authenticationService provides the current customer
        when(authenticationService.getCurrentCustomer()).thenReturn(current);

        // email check: new email does not exist in repo
        when(customerRepository.existsByEmail(updateRequest.email())).thenReturn(false);

        // simulate save returning the persisted entity with updated fields
        when(customerRepository.save(any(Customer.class))).thenAnswer(
                invocation -> invocation.getArgument(0));

        CustomerResponse result = customerService.updateCustomer(updateRequest);

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(1L, result.id()),
                () -> assertEquals("NewFirst", result.firstName()),
                () -> assertEquals("NewLast", result.lastName()),
                () -> assertEquals("new.email@mail.com", result.email())
        );

        // Verify interactions and that saved entity contains updated fields
        verify(authenticationService).getCurrentCustomer();

        ArgumentCaptor<Customer> captor = ArgumentCaptor.forClass(Customer.class);
        verify(customerRepository).save(captor.capture());
        Customer saved = captor.getValue();

        assertAll(
                () -> assertEquals("NewFirst", saved.getFirstName()),
                () -> assertEquals("NewLast", saved.getLastName()),
                () -> assertEquals("new.email@mail.com", saved.getEmail())
        );
    }

    @Test
    @DisplayName("updateCustomer: when requested email already exists, " +
            "throws CustomerAlreadyExistException")
    void updateCustomer_whenEmailExists_throwsException() {

        Customer current = createCustomerEntity(1L, "OldFirst", "OldLast",
                "old.email@mail.com");
        UpdateCustomerRequest updateRequest = new UpdateCustomerRequest("NewFirst",
                "NewLast", "existing.email@mail.com");

        when(authenticationService.getCurrentCustomer()).thenReturn(current);

        when(customerRepository.existsByEmail(updateRequest.email())).thenReturn(true);

        assertThrows(CustomerAlreadyExistException.class,
                () -> customerService.updateCustomer(updateRequest));

        verify(authenticationService).getCurrentCustomer();
        verify(customerRepository).existsByEmail(updateRequest.email());
        verify(customerRepository, never()).save(any(Customer.class));
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
}

