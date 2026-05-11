package com.jackalcode.ecommerce_store.product;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    boolean existsByNameIgnoreCase(String name);

    @Override
    @EntityGraph(attributePaths = {"category"})
    List<Product> findAll();

    @Override
    @EntityGraph(attributePaths = {"category"})
    List<Product> findAll(Specification<Product> spec);
}