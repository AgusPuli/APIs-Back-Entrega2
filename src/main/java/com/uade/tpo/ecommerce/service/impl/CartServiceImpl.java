package com.uade.tpo.ecommerce.service.impl;

import com.uade.tpo.ecommerce.controllers.carts.CartAddRequest;
import com.uade.tpo.ecommerce.controllers.carts.CartItemRequest;
import com.uade.tpo.ecommerce.controllers.orders.OrderItemRequest;
import com.uade.tpo.ecommerce.controllers.orders.OrderRequest;
import com.uade.tpo.ecommerce.entity.Cart;
import com.uade.tpo.ecommerce.entity.CartItem;
import com.uade.tpo.ecommerce.entity.Product;
import com.uade.tpo.ecommerce.entity.User;
import com.uade.tpo.ecommerce.exceptions.ProductNotFoundException;
import com.uade.tpo.ecommerce.repository.CartRepository;
import com.uade.tpo.ecommerce.repository.ProductRepository;
import com.uade.tpo.ecommerce.repository.UserRepository;
import com.uade.tpo.ecommerce.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
@Transactional
public class CartServiceImpl implements CartService {

    @Autowired private CartRepository carts;
    @Autowired private UserRepository users;
    @Autowired private ProductRepository products;

    @Override
    public Cart getByUser(Long userId) {
        return carts.findByUserId(userId).orElseGet(() -> {
            User user = users.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("Usuario no existe id=" + userId));

            Cart cart = Cart.builder()
                    .user(user)
                    .items(new ArrayList<>())
                    .build();

            return carts.save(cart);
        });
    }

    @Override
    public Cart addItem(CartAddRequest request) {
        if (request == null || request.getUserId() == null || request.getItem() == null) {
            throw new IllegalArgumentException("userId e item son requeridos");
        }

        Cart cart = getByUser(request.getUserId());

        CartItemRequest ir = request.getItem();
        if (ir.getProductId() == null) {
            throw new IllegalArgumentException("productId es requerido");
        }
        int qty = (ir.getQuantity() != null) ? ir.getQuantity() : 0;
        if (qty <= 0) {
            throw new IllegalArgumentException("quantity inválida (debe ser > 0)");
        }

        for (Product p : products.findAll()) {
            if (p.getStock() == 0){
                throw new IllegalArgumentException("No hay más stock disponible");
            }
        }

        Product product = products.findById(ir.getProductId())
                .orElseThrow(() -> new ProductNotFoundException(ir.getProductId()));

        // asegurar lista
        List<CartItem> items = (cart.getItems() != null) ? cart.getItems() : new ArrayList<>();

        // si ya existe el producto en el carrito, acumular cantidad
        for (CartItem it : items) {
            if (it.getProduct().getId().equals(product.getId())) {
                it.setQuantity(it.getQuantity() + qty);
                cart.setItems(items);
                return carts.save(cart);
            }
        }

        // crear item nuevo con builder
        CartItem newItem = CartItem.builder()
                .cart(cart)
                .product(product)
                .quantity(qty)
                .build();

        items.add(newItem);
        cart.setItems(items);

        return carts.save(cart);
    }

    @Override
    public Cart removeItem(Long userId, Long productId) {
        if (userId == null || productId == null) {
            throw new IllegalArgumentException("userId y productId son requeridos");
        }

        Cart cart = getByUser(userId);
        List<CartItem> items = (cart.getItems() != null) ? cart.getItems() : new ArrayList<>();

        // remover por productId (orphanRemoval = true se encarga del delete)
        Iterator<CartItem> it = items.iterator();
        while (it.hasNext()) {
            CartItem ci = it.next();
            if (ci.getProduct() != null && productId.equals(ci.getProduct().getId())) {
                it.remove();
            }
        }

        cart.setItems(items);
        return carts.save(cart);
    }

    @Override
    public Cart clear(Long userId) {
        if (userId == null) throw new IllegalArgumentException("userId es requerido");

        Cart cart = getByUser(userId);
        cart.setItems(new ArrayList<>()); // reset lista
        // limpiar descuento cuando vaciás el carrito
        cart.setDiscountCode(null);
        cart.setDiscountPercentage(null);

        return carts.save(cart);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderRequest checkoutPreview(Long userId) {
        if (userId == null) throw new IllegalArgumentException("userId es requerido");
        Cart cart = getByUser(userId);

        List<OrderItemRequest> orderItems = new ArrayList<>();
        if (cart.getItems() != null) {
            for (CartItem ci : cart.getItems()) {
                if (ci.getProduct() == null || ci.getProduct().getId() == null) continue;
                orderItems.add(
                        OrderItemRequest.builder()
                                .productId(ci.getProduct().getId())
                                .quantity(ci.getQuantity())
                                .build()
                );
            }
        }

        return OrderRequest.builder()
                .userId(userId)
                .items(orderItems)
                .build();
    }

    // ===================== Descuentos con userId =====================

    private BigDecimal priceOf(Product p) {
        // Product.price debe ser BigDecimal
        return (p != null && p.getPrice() != null) ? p.getPrice() : BigDecimal.ZERO;
    }

    private BigDecimal computeSubtotal(Cart cart) {
        BigDecimal subtotal = BigDecimal.ZERO;
        if (cart.getItems() != null) {
            for (CartItem ci : cart.getItems()) {
                Product p = ci.getProduct();
                int qty = (ci.getQuantity() != null) ? ci.getQuantity() : 0;
                if (p != null && qty > 0) {
                    subtotal = subtotal.add(priceOf(p).multiply(BigDecimal.valueOf(qty)));
                }
            }
        }
        return subtotal.setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal getCurrentCartSubtotal(Long userId) {
        if (userId == null) throw new IllegalArgumentException("userId es requerido");
        Cart cart = getByUser(userId);
        return computeSubtotal(cart);
    }

    @Override
    public void applyDiscount(Long userId, String code, BigDecimal percentage, BigDecimal ignored) {
        if (userId == null) throw new IllegalArgumentException("userId es requerido");
        if (code == null || code.isBlank()) throw new IllegalArgumentException("code es requerido");
        if (percentage == null) percentage = BigDecimal.ZERO;

        Cart cart = getByUser(userId);

        // Guardamos solo código y % en el carrito (el monto se recalcula cuando haga checkout)
        cart.setDiscountCode(code.trim().toUpperCase());
        cart.setDiscountPercentage(percentage.setScale(2, RoundingMode.HALF_UP));

        carts.save(cart);
    }

    @Override
    public void removeDiscount(Long userId) {
        if (userId == null) throw new IllegalArgumentException("userId es requerido");
        Cart cart = getByUser(userId);
        cart.setDiscountCode(null);
        cart.setDiscountPercentage(null);
        carts.save(cart);
    }
}
