package com.uade.tpo.ecommerce.controllers.carts;

import com.uade.tpo.ecommerce.entity.Cart;
import com.uade.tpo.ecommerce.entity.User;
import com.uade.tpo.ecommerce.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity; // Importante para respuestas HTTP correctas
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/carts")
@CrossOrigin(origins = "http://localhost:5173")
public class CartsController {

    @Autowired
    private CartService service;

    @Autowired
    private CartMapper mapper;

    // 🔹 Obtener carrito por ID
    @GetMapping("/{userId}")
    public CartResponses.CartResponse getByUser(@PathVariable Long userId) {
        Cart c = service.getByUser(userId);
        return mapper.toResponse(c);
    }

    // 🔹 Agregar ítem al carrito (POST - Botón "Agregar" en Productos)
    @PostMapping("/add")
    public ResponseEntity<CartResponses.CartResponse> add(@RequestBody CartAddRequest request) {
        // 🔒 Validación de seguridad para evitar Error 500
        if (request.getUserId() == null) {
            System.err.println("❌ Error: userId nulo en /carts/add");
            throw new IllegalArgumentException("El userId es obligatorio");
        }

        System.out.println("🛒 Adding item. User: " + request.getUserId() + " Product: " + request.getProductId());

        Cart c = service.addItem(request);
        return ResponseEntity.ok(mapper.toResponse(c));
    }

    // ✅ NUEVO ENDPOINT UNIFICADO (PUT - Botones "+" y "-" en Carrito)
    // Este es el que usa tu nuevo updateQuantity Thunk
    @PutMapping("/update-item")
    public ResponseEntity<CartResponses.CartResponse> updateItemQuantity(@RequestBody CartAddRequest request) {
        if (request.getUserId() == null) {
            throw new IllegalArgumentException("El userId es obligatorio para actualizar");
        }

        // Nota: Usamos addItem para gestionar la cantidad.
        // Si tu servicio soporta setear cantidad exacta, úsalo aquí.
        // De lo contrario, addItem sumará.
        Cart c = service.addItem(request);
        return ResponseEntity.ok(mapper.toResponse(c));
    }

    // 🔹 Disminuir cantidad (Legacy / Específico)
    @PutMapping("/{userId}/item/{productId}/decrease")
    public CartResponses.CartResponse decreaseItem(
            @PathVariable Long userId,
            @PathVariable Long productId
    ) {
        Cart c = service.decreaseItem(userId, productId);
        return mapper.toResponse(c);
    }

    // 🔹 Eliminar ítem del carrito
    @DeleteMapping("/{userId}/item/{productId}")
    public CartResponses.CartResponse remove(@PathVariable Long userId, @PathVariable Long productId) {
        Cart c = service.removeItem(userId, productId);
        return mapper.toResponse(c);
    }

    // 🔹 Vaciar carrito completo
    @DeleteMapping("/{userId}/clear")
    public CartResponses.CartResponse clear(@PathVariable Long userId) {
        Cart c = service.clear(userId);
        return mapper.toResponse(c);
    }

    // 🔹 Previsualizar checkout
    @PostMapping("/{userId}/checkout-preview")
    public CartResponses.CheckoutPreviewResponse checkoutPreview(@PathVariable Long userId) {
        Cart preview = service.checkoutPreview(userId);
        return mapper.toPreview(preview);
    }

    // 🔹 Obtener carrito del usuario autenticado (Token)
    @GetMapping("/cart")
    public CartResponses.CartResponse getCart(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new IllegalArgumentException("No autenticado");
        }
        User user = (User) authentication.getPrincipal();
        Cart c = service.getByUser(user.getId());
        return mapper.toResponse(c);
    }
}