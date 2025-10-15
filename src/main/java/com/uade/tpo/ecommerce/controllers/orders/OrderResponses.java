package com.uade.tpo.ecommerce.controllers.orders;

import com.uade.tpo.ecommerce.entity.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

public final class OrderResponses {

    public record OrderSummaryResponse(
            Long id,
            OrderStatus status,
            Instant createdAt,
            BigDecimal total
    ) {}

    public record OrderDetailResponse(
            Long id,
            OrderStatus status,
            Instant createdAt,
            BigDecimal total,
            List<OrderItemResponse> items
    ) {}

    public record OrderItemResponse(
            Long productId,
            String name,
            BigDecimal unitPrice,
            Integer quantity
    ) {}

    private OrderResponses() {}
}
