package com.uade.tpo.ecommerce.service;

import com.uade.tpo.ecommerce.entity.Order;
import com.uade.tpo.ecommerce.entity.OrderStatus;
import com.uade.tpo.ecommerce.controllers.orders.CheckoutRequest; // 👈 Importante: Importar tu DTO
import org.springframework.data.domain.*;

public interface OrderService {
    // Mantenemos el viejo por si acaso, pero agregamos este NUEVO:
    Order createOrderFromPayload(String email, CheckoutRequest request);

    Order createFromCart(Long userId);

    Order getOrderById(Long orderId);

    Page<Order> listOrders(Pageable pageable);

    Page<Order> listByUser(Long userId, Pageable pageable);

    Order updateStatus(Long orderId, OrderStatus status);

    void delete(Long orderId);
}