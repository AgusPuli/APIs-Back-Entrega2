package com.uade.tpo.ecommerce.controllers.carts;

import com.uade.tpo.ecommerce.entity.User;
import com.uade.tpo.ecommerce.repository.UserRepository;
import com.uade.tpo.ecommerce.service.CartService;
import com.uade.tpo.ecommerce.service.DiscountService;
import com.uade.tpo.ecommerce.service.DiscountService.DiscountPreview;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/cart/discounts")
public class CartDiscountsController {

    @Autowired private CartService cartService;
    @Autowired private DiscountService discountService;
    @Autowired private UserRepository userRepository;

    // ---------- Helper para obtener userId desde el token ----------
    private Long currentUserId(Authentication auth) {
        if (auth == null || !(auth.getPrincipal() instanceof UserDetails ud))
            throw new IllegalArgumentException("No autenticado");

        String username = ud.getUsername(); // puede ser email/username
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        return user.getId();
    }

    // ---------- PREVIEW: calcula el descuento, no lo guarda ----------
    @PostMapping("/preview")
    public ResponseEntity<DiscountPreviewResponse> preview(
            @RequestBody ApplyDiscountRequest req,
            Authentication auth) {

        Long userId = currentUserId(auth);
        BigDecimal subtotal = cartService.getCurrentCartSubtotal(userId);

        DiscountPreview p = discountService.preview(req.getCode(), subtotal);

        DiscountPreviewResponse body = DiscountPreviewResponse.builder()
                .code(p.code())
                .percentage(p.percentage())
                .subtotal(p.subtotal())
                .discountAmount(p.discountAmount())
                .total(p.total())
                .message(p.message())
                .build();

        return "OK".equals(p.message())
                ? ResponseEntity.ok(body)
                : ResponseEntity.badRequest().body(body);
    }

    // ---------- APPLY: valida y guarda en el carrito ----------
    @PostMapping("/apply")
    public ResponseEntity<DiscountPreviewResponse> apply(
            @RequestBody ApplyDiscountRequest req,
            Authentication auth) {

        Long userId = currentUserId(auth);
        BigDecimal subtotal = cartService.getCurrentCartSubtotal(userId);

        DiscountPreview p = discountService.preview(req.getCode(), subtotal);

        DiscountPreviewResponse body = DiscountPreviewResponse.builder()
                .code(p.code())
                .percentage(p.percentage())
                .subtotal(p.subtotal())
                .discountAmount(p.discountAmount())
                .total(p.total())
                .message(p.message())
                .build();

        if (!"OK".equals(p.message())) {
            return ResponseEntity.badRequest().body(body);
        }

        cartService.applyDiscount(userId, p.code(), p.percentage(), p.discountAmount());
        return ResponseEntity.ok(body);
    }

    // ---------- REMOVE: quita el descuento del carrito ----------
    @DeleteMapping
    public ResponseEntity<Void> remove(Authentication auth) {
        Long userId = currentUserId(auth);
        cartService.removeDiscount(userId);
        return ResponseEntity.noContent().build();
    }
}
