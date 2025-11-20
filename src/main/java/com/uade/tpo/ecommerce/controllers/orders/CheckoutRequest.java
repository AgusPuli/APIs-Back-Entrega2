package com.uade.tpo.ecommerce.controllers.orders;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutRequest {
    // ⚠️ IMPORTANTE: El nombre de este campo debe coincidir con el JSON del frontend
    private List<CheckoutItem> items;
    private BigDecimal total;
    private String discountCode; // Si lo usas

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CheckoutItem {
        private Long productId;
        private Integer quantity;
        private BigDecimal price;
    }
}