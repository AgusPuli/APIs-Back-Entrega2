package com.uade.tpo.ecommerce.controllers.carts;

import java.math.BigDecimal;
import java.util.List;

public final class CartResponses {

    public record CartItemResponse(
            Long productId,
            String name,
            BigDecimal price,
            Integer quantity,
            BigDecimal subtotal,
            Integer stock // ✅ Nuevo campo: Stock disponible
    ) {}

    public record CartResponse(
            Long cartId,
            Long userId,
            List<CartItemResponse> items,
            String discountCode,
            BigDecimal discountPercentage,
            BigDecimal subtotal,
            BigDecimal discountAmount,
            BigDecimal total
    ) {}

    public record CheckoutPreviewResponse(
            Long cartId,
            Long userId,
            List<CartItemResponse> items,
            String discountCode,
            BigDecimal discountPercentage,
            BigDecimal subtotal,
            BigDecimal discountAmount,
            BigDecimal total
    ) {}

    private CartResponses() {}
}