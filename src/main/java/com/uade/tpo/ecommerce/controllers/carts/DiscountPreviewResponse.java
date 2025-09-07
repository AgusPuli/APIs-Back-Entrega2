package com.uade.tpo.ecommerce.controllers.carts;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class DiscountPreviewResponse {
    private String code;
    private BigDecimal percentage;     // ej: 10.00
    private BigDecimal subtotal;
    private BigDecimal discountAmount; // subtotal * (percentage/100)
    private BigDecimal total;          // subtotal - discountAmount
    private String message;            // "OK" o error legible
}