package com.jackalcode.ecommerceapi.services;

import com.jackalcode.ecommerceapi.dtos.requests.RegisterCustomerRequest;
import com.jackalcode.ecommerceapi.dtos.requests.UpdateCustomerRequest;
import com.jackalcode.ecommerceapi.dtos.responses.CustomerResponse;

import java.util.List;

public interface CustomerService {

    List<CustomerResponse> getCustomers();

    CustomerResponse getCustomer();

    CustomerResponse getCustomerById(Long customerId);

    CustomerResponse registerCustomer(RegisterCustomerRequest registerCustomerRequest);

    CustomerResponse updateCustomer(UpdateCustomerRequest updateCustomerRequest);
}
