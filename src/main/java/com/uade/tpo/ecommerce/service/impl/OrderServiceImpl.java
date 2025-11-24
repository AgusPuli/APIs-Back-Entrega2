package com.uade.tpo.ecommerce.service.impl;

import com.uade.tpo.ecommerce.controllers.orders.CheckoutRequest;
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

    // ✅ MÉTODO 1: Crear orden desde el Frontend (Redux Payload)
    @Override
    @Transactional
    public Order createOrderFromPayload(String email, CheckoutRequest request) {
        // 1. Buscar usuario autenticado
        User user = users.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + email));

        // 2. Crear estructura básica de la orden (con valores iniciales para evitar nulos)
        Order order = Order.builder()
                .user(user)
                .status(OrderStatus.PENDING)
                .total(request.getTotal())
                .discountAmount(BigDecimal.ZERO) // Evita error not-null
                .discountPercent(BigDecimal.ZERO) // Evita error not-null
                .discountCode(request.getDiscountCode()) // Si el request lo trae (o null)
                .build();

        List<OrderItem> items = new ArrayList<>();
        BigDecimal calculatedSubtotal = BigDecimal.ZERO;

        // 3. Procesar cada item del JSON (Lógica restaurada)
        for (CheckoutRequest.CheckoutItem itemReq : request.getItems()) {
            Product product = products.findById(itemReq.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado ID: " + itemReq.getProductId()));

            // 4. Validar Stock
            if (product.getStock() < itemReq.getQuantity()) {
                throw new IllegalArgumentException("No hay suficiente stock para: " + product.getName());
            }

            // 5. Descontar Stock
            product.setStock(product.getStock() - itemReq.getQuantity());
            products.save(product);

            // 6. Calcular subtotal de la línea
            BigDecimal lineSubtotal = itemReq.getPrice().multiply(BigDecimal.valueOf(itemReq.getQuantity()));

            // 7. Sumar al acumulador
            calculatedSubtotal = calculatedSubtotal.add(lineSubtotal);

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(itemReq.getQuantity())
                    .unitPrice(itemReq.getPrice())
                    .subtotal(lineSubtotal)
                    .build();

            items.add(orderItem);
        }

        order.setItems(items);

        // 8. Asignar el subtotal calculado
        order.setSubtotal(calculatedSubtotal);

        // 9. Guardar la orden
        Order savedOrder = orders.save(order);

        // 10. Limpieza opcional: Si el usuario tenía un carrito en BD, lo borramos para sincronizar
        carts.findByUserId(user.getId()).ifPresent(cart -> carts.delete(cart));

        return savedOrder;
    }

    // --- MÉTODO 2: Crear desde Carrito BD (Legacy) ---
    @Override
    @Transactional
    public Order createFromCart(Long userId) {
        Cart cart = carts.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("No existe carrito para userId=" + userId));

        if (cart.getUser().getId() == null) {
            throw new IllegalArgumentException("El ID no puede ser null");
        }

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

            if (product.getStock() < qty) {
                throw new IllegalArgumentException("Sin stock para " + product.getName());
            }
            product.setStock(product.getStock() - qty);
            products.save(product);

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