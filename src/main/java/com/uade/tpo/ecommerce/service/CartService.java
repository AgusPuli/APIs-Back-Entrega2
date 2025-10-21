package com.uade.tpo.ecommerce.service;

import com.uade.tpo.ecommerce.controllers.carts.CartAddRequest;
import com.uade.tpo.ecommerce.controllers.carts.CartItemRequest;
import com.uade.tpo.ecommerce.controllers.orders.OrderRequest;
import com.uade.tpo.ecommerce.entity.Cart;

import java.math.BigDecimal;

public interface CartService {

    Cart getByUser(Long userId);

    Cart addItem(CartAddRequest request);

    Cart decreaseItem(Long userId, Long productId);


    Cart removeItem(Long userId, Long productId);

    Cart clear(Long userId);

    // Previsualización de orden (armada desde el carrito)
    Cart checkoutPreview(Long userId);

    // === nuevos/clave con userId ===
    BigDecimal getCurrentCartSubtotal(Long userId);

    void applyDiscount(Long userId, String code, BigDecimal percentage, BigDecimal discountAmount);

    void removeDiscount(Long userId);
}
