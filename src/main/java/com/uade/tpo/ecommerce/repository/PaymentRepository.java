package com.uade.tpo.ecommerce.repository;

import com.uade.tpo.ecommerce.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> { }
