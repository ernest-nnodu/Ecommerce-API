package com.jackalcode.ecommerceapi.services;

import com.jackalcode.ecommerceapi.dtos.responses.CustomerResponse;

import java.util.List;

public interface CustomerService {

    List<CustomerResponse>  getAllCustomers();

    CustomerResponse getCustomerById(Long id);
}
