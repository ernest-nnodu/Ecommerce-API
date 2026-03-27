package com.jackalcode.ecommerceapi.repositories;

import com.jackalcode.ecommerceapi.entities.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
}