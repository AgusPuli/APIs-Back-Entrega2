package com.uade.tpo.ecommerce.controllers.discounts;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class DiscountRequest {
    private String code;                 // p.ej: "BACK2SCHOOL"
    private BigDecimal percentage;       // 0 < p <= 100
    private Boolean active;              // opcional, default true
    private LocalDateTime startsAt;      // opcional
    private LocalDateTime endsAt;        // opcional
}
