package com.jackalcode.ecommerceapi.repositories;

import com.jackalcode.ecommerceapi.entities.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    Cart findByCustomerId(Long customerId);
}