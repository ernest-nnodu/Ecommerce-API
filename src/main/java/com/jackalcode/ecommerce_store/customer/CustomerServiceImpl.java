package com.jackalcode.ecommerce_store.customer;

import com.jackalcode.ecommerce_store.cart.Cart;
import com.jackalcode.ecommerce_store.security.Role;
import com.jackalcode.ecommerce_store.exceptions.CustomerAlreadyExistException;
import com.jackalcode.ecommerce_store.exceptions.CustomerNotFoundException;
import com.jackalcode.ecommerce_store.cart.CartRepository;
import com.jackalcode.ecommerce_store.security.AuthenticationService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CartRepository cartRepository;
    private final CustomerMapper customerMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationService authenticationService;

    @Override
    public List<CustomerResponse> getCustomers() {
        return customerRepository.findAll().stream()
                .map(customerMapper::toCustomerResponse)
                .toList();
    }

    @Override
    public CustomerResponse getCustomer() {

        Customer currentCustomer = authenticationService.getCurrentCustomer();

        return customerMapper.toCustomerResponse(currentCustomer);
    }

    @Override
    public CustomerResponse getCustomerById(Long customerId) {

        var customer = customerRepository.findById(customerId).orElseThrow(
                () -> new CustomerNotFoundException(customerId.toString())
        );

        return customerMapper.toCustomerResponse(customer);
    }

    @Override
    @Transactional
    public CustomerResponse registerCustomer(RegisterCustomerRequest registerCustomerRequest) {

        //Check if customer with same email already exist in the database
        if (customerRepository.existsByEmail(registerCustomerRequest.email())) {
            throw new CustomerAlreadyExistException("Customer already exist with email: " +
                    registerCustomerRequest.email());
        }

        //Create new customer, hash customer password, and then save to database
        Customer customer = customerMapper.toCustomer(registerCustomerRequest);
        customer.setPassword(passwordEncoder.encode(customer.getPassword()));
        customer.setRole(Role.USER);
        customerRepository.save(customer);

        //Assign a cart to customer
        Cart cart = new Cart();
        cart.setCustomer(customer);
        cartRepository.save(cart);

        return customerMapper.toCustomerResponse(customer);
    }

    @Override
    @Transactional
    public CustomerResponse updateCustomer(UpdateCustomerRequest updateCustomerRequest) {

        Customer currentCustomer = authenticationService.getCurrentCustomer();

        //If customer email need updating, check if new email already exist in the database
        if (!currentCustomer.getEmail().equals(updateCustomerRequest.email()) &&
        customerRepository.existsByEmail(updateCustomerRequest.email())) {
            throw new CustomerAlreadyExistException("Customer already exist with email: " +
                    updateCustomerRequest.email());
        }

        customerMapper.updateCustomer(updateCustomerRequest, currentCustomer);
        return customerMapper.toCustomerResponse(customerRepository.save(currentCustomer));
    }
}
