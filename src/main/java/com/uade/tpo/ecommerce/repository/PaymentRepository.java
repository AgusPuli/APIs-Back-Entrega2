package com.uade.tpo.ecommerce.repository;

import com.uade.tpo.ecommerce.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    // Buscar pagos por el userId de la orden
    @Query("SELECT p FROM Payment p WHERE p.order.user.id = :userId")
    List<Payment> findByUserId(@Param("userId") Long userId);
}