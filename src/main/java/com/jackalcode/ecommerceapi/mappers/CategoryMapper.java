package com.jackalcode.ecommerceapi.mappers;

import com.jackalcode.ecommerceapi.dtos.requests.CategoryRequest;
import com.jackalcode.ecommerceapi.entities.Category;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    Category toCategory(CategoryRequest categoryRequest);
    void updateCategory(CategoryRequest categoryRequest, @MappingTarget Category category);
}
