package com.uade.tpo.ecommerce.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "discounts",
        uniqueConstraints = @UniqueConstraint(name = "uk_discounts_code", columnNames = "code"))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Discount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Código que ingresa el usuario (UNIQUE)
    @Column(nullable = false, length = 64)
    private String code;

    // Porcentaje total a descontar (ej: 10 = 10%)
    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal percentage;  // usa BigDecimal para evitar errores de float

    @Column(nullable = false)
    private Boolean active = true;

    private LocalDateTime startsAt;
    private LocalDateTime endsAt;

    @PrePersist @PreUpdate
    private void normalize() {
        if (code != null) code = code.trim().toUpperCase();
    }

}
