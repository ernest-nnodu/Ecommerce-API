package com.jackalcode.ecommerceapi.mappers;

import com.jackalcode.ecommerceapi.dtos.requests.ProductRequest;
import com.jackalcode.ecommerceapi.dtos.responses.ProductResponse;
import com.jackalcode.ecommerceapi.entities.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "categoryName", source = "category.name")
    ProductResponse toProductResponse(Product product);

    @Mapping(target = "category", ignore = true)
    Product toProduct(ProductRequest productRequest);

    @Mapping(target = "category", ignore = true)
    void updateProduct(ProductRequest productRequest, @MappingTarget Product existingProduct);
}
