package com.jackalcode.ecommerceapi.repositories;

import com.jackalcode.ecommerceapi.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
}