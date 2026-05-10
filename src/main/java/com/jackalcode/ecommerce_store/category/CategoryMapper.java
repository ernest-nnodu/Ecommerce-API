package com.jackalcode.ecommerce_store.category;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    Category toCategory(CategoryRequest categoryRequest);
    void updateCategory(CategoryRequest categoryRequest, @MappingTarget Category category);
}
