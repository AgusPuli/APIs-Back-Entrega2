package com.uade.tpo.ecommerce.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Double amount;

    @Column(nullable = false, length = 50)
    private String method; // ejemplo: "CARD", "CASH", "TRANSFER"

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    // Relación con Order 1:1 (una orden un pago, no hay cuotas)
    @OneToOne
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Order order;
}
