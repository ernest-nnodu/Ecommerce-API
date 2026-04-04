package com.jackalcode.ecommerceapi.repositories;

import com.jackalcode.ecommerceapi.entities.Product;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    boolean existsByNameIgnoreCase(String name);

    @EntityGraph(attributePaths = {"category"})
    List<Product> findAll();
}