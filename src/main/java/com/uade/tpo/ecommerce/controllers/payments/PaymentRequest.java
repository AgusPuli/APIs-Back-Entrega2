package com.uade.tpo.ecommerce.controllers.payments;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {
    private Long orderId;
    private Double amount;
    private String method; // ej: "CARD", "PIX", "CASH"
}
