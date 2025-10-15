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

    @Autowired private CartService service;
    @Autowired private CartMapper mapper;

    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    public CartResponses.CartResponse getByUser(@PathVariable Long userId) {
        Cart c = service.getByUser(userId);
        return mapper.toResponse(c);
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN') or #request.userId == authentication.principal.id")
    public CartResponses.CartResponse add(@RequestBody CartAddRequest request) {
        Cart c = service.addItem(request);
        return mapper.toResponse(c);
    }

    @DeleteMapping("/{userId}/item/{productId}")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    public CartResponses.CartResponse remove(@PathVariable Long userId, @PathVariable Long productId) {
        Cart c = service.removeItem(userId, productId);
        return mapper.toResponse(c);
    }

    @DeleteMapping("/{userId}/clear")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    public CartResponses.CartResponse clear(@PathVariable Long userId) {
        Cart c = service.clear(userId);
        return mapper.toResponse(c);
    }

    @PostMapping("/{userId}/checkout-preview")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    public CartResponses.CheckoutPreviewResponse checkoutPreview(@PathVariable Long userId) {
        Cart preview = service.checkoutPreview(userId); // ahora sí retorna Cart
        return mapper.toPreview(preview);
    }
}