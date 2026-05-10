package com.jackalcode.ecommerce_store.order;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(target = "items", source = "orderItems")
    @Mapping(target = "customerId", expression = "java(order.getCustomer().getId())")
    OrderResponse toOrderResponse(Order order);
}
