package com.jackalcode.ecommerce_store.customer;

import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface CustomerMapper {

    CustomerResponse toCustomerResponse(Customer customer);
    Customer toCustomer(RegisterCustomerRequest registerCustomerRequest);

    @BeanMapping(
            ignoreByDefault = true,
            nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
    )
    @Mapping(target = "firstName", source = "firstName")
    @Mapping(target = "lastName", source = "lastName")
    @Mapping(target = "email", source = "email")
    void updateCustomer(UpdateCustomerRequest updateCustomerRequest, @MappingTarget Customer customer);
}
