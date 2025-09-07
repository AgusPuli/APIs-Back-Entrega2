package com.uade.tpo.ecommerce.controllers.payments;

import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {
    private Long orderId;
    private BigDecimal amount;
    private String method; // ej: "CARD", "PIX", "CASH"
}
