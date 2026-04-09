package com.jackalcode.ecommerceapi.mappers;

import com.jackalcode.ecommerceapi.dtos.responses.OrderResponse;
import com.jackalcode.ecommerceapi.entities.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(target = "items", source = "orderItems")
    OrderResponse toOrderResponse(Order order);
}
