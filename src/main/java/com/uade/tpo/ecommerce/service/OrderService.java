package com.uade.tpo.ecommerce.service;

import com.uade.tpo.ecommerce.entity.Order;
import com.uade.tpo.ecommerce.entity.OrderStatus;
import com.uade.tpo.ecommerce.controllers.orders.OrderRequest;
import org.springframework.data.domain.*;

public interface OrderService {
    public Order create(OrderRequest request);

    public Order getOrderById(Long orderId);

    public Page<Order> listOrders(Pageable pageable);

    public Page<Order> listByUser(Long userId, Pageable pageable);

    public Order updateStatus(Long orderId, OrderStatus status);

    public void delete(Long orderId);
}
