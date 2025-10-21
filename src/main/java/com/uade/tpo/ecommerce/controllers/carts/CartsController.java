package com.uade.tpo.ecommerce.controllers.carts;

import com.uade.tpo.ecommerce.entity.Cart;
import com.uade.tpo.ecommerce.entity.User;
import com.uade.tpo.ecommerce.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/carts")
public class CartsController {

    @Autowired
    private CartService service;

    @Autowired
    private CartMapper mapper;

    // 🔹 Obtener carrito por ID (ADMIN o dueño)
    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    public CartResponses.CartResponse getByUser(@PathVariable Long userId) {
        Cart c = service.getByUser(userId);
        return mapper.toResponse(c);
    }

    // 🔹 Agregar ítem al carrito
    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN') or #request.userId == authentication.principal.id")
    public CartResponses.CartResponse add(@RequestBody CartAddRequest request) {
        Cart c = service.addItem(request);
        return mapper.toResponse(c);
    }


    // 🔹 Disminuir cantidad de un ítem del carrito
    @PutMapping("/{userId}/item/{productId}/decrease")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    public CartResponses.CartResponse decreaseItem(
            @PathVariable Long userId,
            @PathVariable Long productId
    ) {
        Cart c = service.decreaseItem(userId, productId);
        return mapper.toResponse(c);
    }


    // 🔹 Eliminar ítem del carrito
    @DeleteMapping("/{userId}/item/{productId}")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    public CartResponses.CartResponse remove(@PathVariable Long userId, @PathVariable Long productId) {
        Cart c = service.removeItem(userId, productId);
        return mapper.toResponse(c);
    }

    // 🔹 Vaciar carrito completo
    @DeleteMapping("/{userId}/clear")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    public CartResponses.CartResponse clear(@PathVariable Long userId) {
        Cart c = service.clear(userId);
        return mapper.toResponse(c);
    }

    // 🔹 Previsualizar checkout
    @PostMapping("/{userId}/checkout-preview")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    public CartResponses.CheckoutPreviewResponse checkoutPreview(@PathVariable Long userId) {
        Cart preview = service.checkoutPreview(userId);
        return mapper.toPreview(preview);
    }

    // ✅ NUEVO — Obtener carrito del usuario autenticado (para el front)
    @GetMapping("/cart")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public CartResponses.CartResponse getCart(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Cart c = service.getByUser(user.getId());
        return mapper.toResponse(c);
    }
}
