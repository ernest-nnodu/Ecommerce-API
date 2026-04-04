package com.jackalcode.ecommerceapi.services.impl;

import com.jackalcode.ecommerceapi.dtos.requests.RegisterCustomerRequest;
import com.jackalcode.ecommerceapi.dtos.requests.UpdateCustomerRequest;
import com.jackalcode.ecommerceapi.dtos.responses.CustomerResponse;
import com.jackalcode.ecommerceapi.entities.Cart;
import com.jackalcode.ecommerceapi.entities.Customer;
import com.jackalcode.ecommerceapi.exceptions.CustomerAlreadyExistException;
import com.jackalcode.ecommerceapi.exceptions.CustomerNotFoundException;
import com.jackalcode.ecommerceapi.mappers.CustomerMapper;
import com.jackalcode.ecommerceapi.repositories.CartRepository;
import com.jackalcode.ecommerceapi.repositories.CustomerRepository;
import com.jackalcode.ecommerceapi.services.CustomerService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CartRepository cartRepository;
    private final CustomerMapper customerMapper;

    @Override
    public List<CustomerResponse> getAllCustomers() {
        return customerRepository.findAll().stream()
                .map(customerMapper::toCustomerResponse)
                .toList();
    }

    @Override
    public CustomerResponse getCustomerById(Long id) {

        Customer customer = getCustomerEntity(id);

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

        //Create new customer, assign a cart to new customer, and then save to database
        Customer customer = customerMapper.toCustomer(registerCustomerRequest);
        customerRepository.save(customer);
        Cart cart = new Cart();
        cart.setCustomer(customer);
        cartRepository.save(cart);
        return customerMapper.toCustomerResponse(customer);
    }

    @Override
    @Transactional
    public CustomerResponse updateCustomer(Long id, UpdateCustomerRequest updateCustomerRequest) {

        Customer existingCustomer = getCustomerEntity(id);

        //If customer email need updating, check if new email already exist in the database
        if (!existingCustomer.getEmail().equals(updateCustomerRequest.email()) &&
        customerRepository.existsByEmail(updateCustomerRequest.email())) {
            throw new CustomerAlreadyExistException("Customer already exist with email: " +
                    updateCustomerRequest.email());
        }

        customerMapper.updateCustomer(updateCustomerRequest, existingCustomer);
        return customerMapper.toCustomerResponse(customerRepository.save(existingCustomer));
    }

    private Customer getCustomerEntity(Long id) {

        return customerRepository.findById(id).orElseThrow(
                () -> new CustomerNotFoundException("Customer not found with id: " + id)
        );
    }
}
