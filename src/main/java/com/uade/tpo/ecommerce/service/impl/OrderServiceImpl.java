package com.uade.tpo.ecommerce.service.impl;

import com.uade.tpo.ecommerce.controllers.orders.OrderItemRequest;
import com.uade.tpo.ecommerce.controllers.orders.OrderRequest;
import com.uade.tpo.ecommerce.entity.*;
import com.uade.tpo.ecommerce.exceptions.*;
import com.uade.tpo.ecommerce.repository.*;
import com.uade.tpo.ecommerce.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    @Autowired private OrderRepository orders;
    @Autowired private OrderItemRepository orderItems;
    @Autowired private UserRepository users;
    @Autowired private ProductRepository products;

    @Override
    public Order create(OrderRequest request) {
        if (request.getUserId() == null || request.getItems() == null || request.getItems().isEmpty()) {
            throw new IllegalArgumentException("userId e items son requeridos");
        }

        User user = users.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Usuario inexistente id=" + request.getUserId()));

        Order order = Order.builder()
                .user(user)
                .status(OrderStatus.PENDING)
                .total(0.0)
                .build();

        List<OrderItem> items = new ArrayList<>();
        double total = 0.0;

        for (OrderItemRequest ir : request.getItems()) {
            Product product = products.findById(ir.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("Producto inexistente id=" + ir.getProductId()));

            int qty = Optional.ofNullable(ir.getQuantity()).orElse(0);
            if (qty <= 0) throw new IllegalArgumentException("quantity inválida para productId=" + ir.getProductId());

            // (Opcional) validar stock
            if (product.getStock() != null && product.getStock() < qty) {
                throw new InsufficientStockException(product.getId());
            }

            double unitPrice = Optional.ofNullable(product.getPrice()).orElse(0.0);
            double subtotal = unitPrice * qty;

            OrderItem item = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(qty)
                    .unitPrice(unitPrice)
                    .subtotal(subtotal)
                    .build();

            items.add(item);
            total += subtotal;

            // (Opcional) descontar stock:
            if (product.getStock() != null) {
                product.setStock(product.getStock() - qty);
            }
        }

        order.setItems(items);
        order.setTotal(total);

        // Persist cascada por items (mappedBy=order + cascade=ALL):
        Order saved = orders.save(order);
        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public Order getOrderById(Long id) {
        return orders.findById(id).orElseThrow(() -> new OrderNotFoundException(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Order> listOrders(Pageable pageable) {
        return orders.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Order> listByUser(Long userId, Pageable pageable) {
        return orders.findByUserId(userId, pageable);
    }

    @Override
    public Order updateStatus(Long id, OrderStatus status) {
        Order o = orders.findById(id).orElseThrow(() -> new OrderNotFoundException(id));
        o.setStatus(status);
        return orders.save(o);
    }

    @Override
    public void delete(Long id) {
        Order o = orders.findById(id).orElseThrow(() -> new OrderNotFoundException(id));
        orders.delete(o);
    }
}
