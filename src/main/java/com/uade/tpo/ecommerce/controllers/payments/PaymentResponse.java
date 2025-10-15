package com.uade.tpo.ecommerce.controllers.payments;
import com.uade.tpo.ecommerce.entity.OrderStatus;

public record PaymentResponse(
        String method,          // "CASH", "TRANSFER", etc.
        OrderStatus orderStatus,// estado actual del pedido (PAID, PENDING, CANCELED, etc.)
        Long orderId            // útil para el cliente
) {}

