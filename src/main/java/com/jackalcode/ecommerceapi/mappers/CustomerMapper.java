package com.jackalcode.ecommerceapi.mappers;

import com.jackalcode.ecommerceapi.dtos.requests.RegisterCustomerRequest;
import com.jackalcode.ecommerceapi.dtos.responses.CustomerResponse;
import com.jackalcode.ecommerceapi.entities.Customer;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CustomerMapper {

    CustomerResponse toCustomerResponse(Customer customer);
    CustomerResponse toCustomer(RegisterCustomerRequest registerCustomerRequest);
}
