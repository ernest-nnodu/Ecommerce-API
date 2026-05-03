package com.jackalcode.ecommerceapi.product;

import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public class ProductSpecifications {

    public static Specification<Product> hasName(String name) {
        return (root, query, cb) ->
                name == null ? null :
                        cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<Product> hasMinPrice(BigDecimal minPrice) {
        return (root, query, cb) ->
                minPrice == null ? null :
                        cb.greaterThanOrEqualTo(root.get("price"), minPrice);
    }

    public static Specification<Product> hasMaxPrice(BigDecimal maxPrice) {
        return (root, query, cb) ->
                maxPrice == null ? null :
                        cb.lessThanOrEqualTo(root.get("price"), maxPrice);
    }

    public static Specification<Product> hasCategoryId(Long categoryId) {
        return (root, query, cb) ->
                categoryId == null ? null :
                        cb.equal(root.get("category").get("id"), categoryId);
    }
}
