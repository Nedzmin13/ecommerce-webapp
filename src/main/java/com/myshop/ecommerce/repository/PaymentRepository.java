package com.myshop.ecommerce.repository;

import com.myshop.ecommerce.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    // Eventuali query specifiche per Payment se necessario
}