package com.jackalcode.ecommerceapi.services.impl;

import com.jackalcode.ecommerceapi.dtos.requests.RegisterCustomerRequest;
import com.jackalcode.ecommerceapi.dtos.requests.UpdateCustomerRequest;
import com.jackalcode.ecommerceapi.dtos.responses.CustomerResponse;
import com.jackalcode.ecommerceapi.entities.Customer;
import com.jackalcode.ecommerceapi.exceptions.CustomerAlreadyExistException;
import com.jackalcode.ecommerceapi.exceptions.CustomerNotFoundException;
import com.jackalcode.ecommerceapi.mappers.CustomerMapper;
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
    private final CustomerMapper customerMapper;

    @Override
    public List<CustomerResponse> getAllCustomers() {
        return customerRepository.findAll().stream()
                .map(customerMapper::toCustomerResponse)
                .toList();
    }

    @Override
    public CustomerResponse getCustomerById(Long id) {

        Customer customer = getCustomer(id);

        return customerMapper.toCustomerResponse(customer);
    }

    @Override
    @Transactional
    public CustomerResponse registerCustomer(RegisterCustomerRequest registerCustomerRequest) {

        if (customerRepository.existsByEmail(registerCustomerRequest.email())) {
            throw new CustomerAlreadyExistException("Customer already exist with email: " +
                    registerCustomerRequest.email());
        }

        Customer customer = customerMapper.toCustomer(registerCustomerRequest);
        customerRepository.save(customer);
        return customerMapper.toCustomerResponse(customer);
    }

    @Override
    @Transactional
    public CustomerResponse updateCustomer(Long id, UpdateCustomerRequest updateCustomerRequest) {

        Customer customer = getCustomer(id);

        customerMapper.updateCustomer(updateCustomerRequest, customer);
        return customerMapper.toCustomerResponse(customerRepository.save(customer));
    }

    private Customer getCustomer(Long id) {

        return customerRepository.findById(id).orElseThrow(
                () -> new CustomerNotFoundException("Customer not found with id: " + id)
        );
    }
}
