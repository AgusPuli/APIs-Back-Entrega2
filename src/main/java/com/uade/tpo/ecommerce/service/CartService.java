package com.uade.tpo.ecommerce.service;
import com.uade.tpo.ecommerce.entity.Cart;
import com.uade.tpo.ecommerce.controllers.carts.CartAddRequest;
import com.uade.tpo.ecommerce.controllers.orders.OrderRequest;


public interface CartService {
    public Cart getByUser(Long userId);

    public Cart addItem(CartAddRequest request);

    public Cart removeItem(Long userId, Long productId);

    public Cart clear(Long userId);

    // Convierte el carrito en orden
    public OrderRequest checkoutPreview(Long userId);
}
