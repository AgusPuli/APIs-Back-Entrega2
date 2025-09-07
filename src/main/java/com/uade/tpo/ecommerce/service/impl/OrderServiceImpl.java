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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    @Autowired private OrderRepository orders;
    @Autowired private UserRepository users;
    @Autowired private ProductRepository products;
    @Autowired private CartRepository carts;

    // Checkout desde carrito
    @Transactional
    public Order createFromCart(Long userId) {
        Cart cart = carts.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("No existe carrito para userId=" + userId));

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new IllegalArgumentException("El carrito está vacío");
        }

        Order order = Order.builder()
                .user(cart.getUser())
                .status(OrderStatus.PENDING)
                .build();

        BigDecimal subtotal = BigDecimal.ZERO;
        List<OrderItem> items = new ArrayList<>();

        for (CartItem ci : cart.getItems()) {
            Product product = ci.getProduct();
            int qty = ci.getQuantity();

            BigDecimal unitPrice = Optional.ofNullable(product.getPrice()).orElse(BigDecimal.ZERO);
            BigDecimal lineSubtotal = unitPrice.multiply(BigDecimal.valueOf(qty));

            subtotal = subtotal.add(lineSubtotal);

            OrderItem item = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(qty)
                    .unitPrice(unitPrice)
                    .subtotal(lineSubtotal)
                    .build();

            items.add(item);
        }

        // aplicar descuento del carrito
        BigDecimal pct = Optional.ofNullable(cart.getDiscountPercentage()).orElse(BigDecimal.ZERO);
        if (pct.compareTo(BigDecimal.ZERO) < 0) pct = BigDecimal.ZERO;
        if (pct.compareTo(BigDecimal.valueOf(100)) > 0) pct = BigDecimal.valueOf(100);

        BigDecimal discountAmount = subtotal.multiply(pct)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        BigDecimal total = subtotal.subtract(discountAmount);

        order.setItems(items);
        order.setSubtotal(subtotal.setScale(2, RoundingMode.HALF_UP));
        order.setDiscountPercent(pct.setScale(2, RoundingMode.HALF_UP));
        order.setDiscountCode(cart.getDiscountCode());
        order.setDiscountAmount(discountAmount.setScale(2, RoundingMode.HALF_UP));
        order.setTotal(total.setScale(2, RoundingMode.HALF_UP));

        Order saved = orders.save(order);

        // limpiar carrito después del checkout
        carts.delete(cart);

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
