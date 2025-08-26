package com.uade.tpo.ecommerce.controllers;

import com.uade.tpo.ecommerce.entity.Cart;
import com.uade.tpo.ecommerce.entity.dto.CartAddRequest;
import com.uade.tpo.ecommerce.entity.dto.OrderRequest;
import com.uade.tpo.ecommerce.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/carts")
public class CartsController {

    @Autowired
    private CartService service;

    @GetMapping("/{userId}")
    public Cart getByUser(@PathVariable Long userId) {
        return service.getByUser(userId);
    }

    @PostMapping("/add")
    public Cart add(@RequestBody CartAddRequest request) {
        return service.addItem(request);
    }

    @DeleteMapping("/{userId}/item/{productId}")
    public Cart remove(@PathVariable Long userId, @PathVariable Long productId) {
        return service.removeItem(userId, productId);
    }

    @DeleteMapping("/{userId}/clear")
    public Cart clear(@PathVariable Long userId) {
        return service.clear(userId);
    }

    @PostMapping("/{userId}/checkout-preview")
    public OrderRequest checkoutPreview(@PathVariable Long userId) {
        return service.checkoutPreview(userId);
    }
}
