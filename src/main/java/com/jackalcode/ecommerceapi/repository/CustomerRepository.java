package com.jackalcode.ecommerceapi.repository;

import com.jackalcode.ecommerceapi.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
