package com.uade.tpo.ecommerce.service;
import com.uade.tpo.ecommerce.entity.Cart;
import com.uade.tpo.ecommerce.entity.dto.CartAddRequest;
import com.uade.tpo.ecommerce.entity.dto.OrderRequest;


public interface CartService {
    public Cart getByUser(Long userId);

    public Cart addItem(CartAddRequest request);

    public Cart removeItem(Long userId, Long productId);

    public Cart clear(Long userId);

    // Convierte el carrito en orden
    OrderRequest checkoutPreview(Long userId);
}
