package com.jackalcode.ecommerceapi.mappers;

import com.jackalcode.ecommerceapi.dtos.responses.CartItemResponse;
import com.jackalcode.ecommerceapi.dtos.responses.CartResponse;
import com.jackalcode.ecommerceapi.entities.Cart;
import com.jackalcode.ecommerceapi.entities.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CartMapper {

    CartResponse toCartResponse(Cart cart);

    @Mapping(target = "totalPrice", expression = "java(cartItem.getTotalPrice())")
    CartItemResponse toCartItemResponse(CartItem cartItem);
}
