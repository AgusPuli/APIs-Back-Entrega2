package com.uade.tpo.ecommerce.service.impl;

import com.uade.tpo.ecommerce.entity.Cart;
import com.uade.tpo.ecommerce.entity.CartItem;
import com.uade.tpo.ecommerce.entity.Product;
import com.uade.tpo.ecommerce.entity.User;
import com.uade.tpo.ecommerce.controllers.carts.CartAddRequest;
import com.uade.tpo.ecommerce.controllers.carts.CartItemRequest;
import com.uade.tpo.ecommerce.controllers.orders.OrderItemRequest;
import com.uade.tpo.ecommerce.controllers.orders.OrderRequest;
import com.uade.tpo.ecommerce.exceptions.ProductNotFoundException;
import com.uade.tpo.ecommerce.repository.CartRepository;
import com.uade.tpo.ecommerce.repository.ProductRepository;
import com.uade.tpo.ecommerce.repository.UserRepository;
import com.uade.tpo.ecommerce.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
