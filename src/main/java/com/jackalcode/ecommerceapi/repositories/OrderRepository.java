package com.jackalcode.ecommerceapi.repositories;

import com.jackalcode.ecommerceapi.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findAllByCustomerId(Long id);
}