package com.jackalcode.ecommerceapi.repository;

import com.jackalcode.ecommerceapi.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
}