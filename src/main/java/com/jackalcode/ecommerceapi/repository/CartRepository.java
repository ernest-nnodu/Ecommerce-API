package com.jackalcode.ecommerceapi.repository;

import com.jackalcode.ecommerceapi.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {
}