package com.uade.tpo.ecommerce.service;

import com.uade.tpo.ecommerce.controllers.carts.DiscountPreviewResponse;
import com.uade.tpo.ecommerce.entity.Discount;
import com.uade.tpo.ecommerce.entity.Order;

import java.math.BigDecimal;

public interface DiscountService {
    Discount getByCodeOrThrow(String code);

    DiscountPreview  preview(String code, BigDecimal subtotal);

    void attachToOrder(Order order, Discount discount, BigDecimal subtotal);

    // Tipo interno del service (NO es el del controller)
    record DiscountPreview(
            String code,
            BigDecimal percentage,
            BigDecimal subtotal,
            BigDecimal discountAmount,
            BigDecimal total,
            String message
    ) {}

}
