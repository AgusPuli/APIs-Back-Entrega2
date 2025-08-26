package com.uade.tpo.ecommerce.entity.dto;

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
