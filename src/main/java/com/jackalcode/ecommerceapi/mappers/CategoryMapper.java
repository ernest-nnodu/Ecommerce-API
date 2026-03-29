package com.jackalcode.ecommerceapi.mappers;

import com.jackalcode.ecommerceapi.dtos.requests.CreateCategoryRequest;
import com.jackalcode.ecommerceapi.entities.Category;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    Category toCategory(CreateCategoryRequest createCategoryRequest);
}
