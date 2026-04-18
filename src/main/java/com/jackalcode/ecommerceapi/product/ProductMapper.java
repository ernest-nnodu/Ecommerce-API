package com.jackalcode.ecommerceapi.product;

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
