package com.jackalcode.ecommerceapi.mappers;

import com.jackalcode.ecommerceapi.dtos.responses.ProductResponse;
import com.jackalcode.ecommerceapi.entities.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "categoryName", source = "category.name")
    ProductResponse toProductResponse(Product product);
}
