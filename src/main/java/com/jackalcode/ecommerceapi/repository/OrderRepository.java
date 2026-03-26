package com.jackalcode.ecommerceapi.repository;

import com.jackalcode.ecommerceapi.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}