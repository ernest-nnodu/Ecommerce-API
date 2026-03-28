package com.jackalcode.ecommerceapi.services.impl;

import com.jackalcode.ecommerceapi.dtos.requests.RegisterCustomerRequest;
import com.jackalcode.ecommerceapi.dtos.responses.CustomerResponse;
import com.jackalcode.ecommerceapi.entities.Customer;
import com.jackalcode.ecommerceapi.mappers.CustomerMapper;
import com.jackalcode.ecommerceapi.repositories.CustomerRepository;
import com.jackalcode.ecommerceapi.services.CustomerService;
import lombok.AllArgsConstructor;
import org.springframework.data.repository.Repository;
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

        Customer customer = customerRepository.findById(id).orElse(null);
        return customerMapper.toCustomerResponse(customer);
    }

    @Override
    public CustomerResponse registerCustomer(RegisterCustomerRequest registerCustomerRequest) {

        Customer customer = customerMapper.toCustomer(registerCustomerRequest);
        customerRepository.save(customer);
        return customerMapper.toCustomerResponse(customer);
    }
}
