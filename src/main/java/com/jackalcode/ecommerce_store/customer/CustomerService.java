package com.jackalcode.ecommerce_store.customer;

import java.util.List;

public interface CustomerService {

    List<CustomerResponse> getCustomers();

    CustomerResponse getCustomer();

    CustomerResponse getCustomerById(Long customerId);

    CustomerResponse registerCustomer(RegisterCustomerRequest registerCustomerRequest);

    CustomerResponse updateCustomer(UpdateCustomerRequest updateCustomerRequest);
}
