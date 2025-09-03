package com.uade.tpo.ecommerce.controllers.carts;

import com.uade.tpo.ecommerce.entity.Cart;
import com.uade.tpo.ecommerce.controllers.orders.OrderRequest;
import com.uade.tpo.ecommerce.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/carts")
public class CartsController {

    @Autowired
    private CartService service;

    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    public Cart getByUser(@PathVariable Long userId) {
        return service.getByUser(userId);
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN') or #request.userId == authentication.principal.id")
    public Cart add(@RequestBody CartAddRequest request) {
        return service.addItem(request);
    }

    @DeleteMapping("/{userId}/item/{productId}")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    public Cart remove(@PathVariable Long userId, @PathVariable Long productId) {
        return service.removeItem(userId, productId);
    }

    @DeleteMapping("/{userId}/clear")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    public Cart clear(@PathVariable Long userId) {
        return service.clear(userId);
    }

    @PostMapping("/{userId}/checkout-preview")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    public OrderRequest checkoutPreview(@PathVariable Long userId) {
        return service.checkoutPreview(userId);
    }

}