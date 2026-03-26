package com.jackalcode.ecommerceapi.repository;

import com.jackalcode.ecommerceapi.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
}