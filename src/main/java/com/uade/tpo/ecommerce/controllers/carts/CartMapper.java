package com.uade.tpo.ecommerce.controllers.carts;

import com.uade.tpo.ecommerce.entity.Cart;
import com.uade.tpo.ecommerce.entity.CartItem;
import com.uade.tpo.ecommerce.entity.Product;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import static com.uade.tpo.ecommerce.controllers.carts.CartResponses.*;

@Component
public class CartMapper {

    public CartResponse toResponse(Cart c) {
        if (c == null) return null;

        List<CartItemResponse> items = items(c);
        BigDecimal subtotal = items.stream()
                .map(CartItemResponse::subtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal discountPct = nz(c.getDiscountPercentage()); // 0–100
        BigDecimal discountAmt = calcDiscount(subtotal, discountPct);
        BigDecimal total       = subtotal.subtract(discountAmt).max(BigDecimal.ZERO);

        return new CartResponse(
                c.getId(),
                c.getUser() != null ? c.getUser().getId() : null,
                items,
                c.getDiscountCode(),
                discountPct,
                subtotal,
                discountAmt,
                total
        );
    }

    public CheckoutPreviewResponse toPreview(Cart c) {
        if (c == null) return null;

        List<CartItemResponse> items = items(c);
        BigDecimal subtotal = items.stream()
                .map(CartItemResponse::subtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal discountPct = nz(c.getDiscountPercentage());
        BigDecimal discountAmt = calcDiscount(subtotal, discountPct);
        BigDecimal total       = subtotal.subtract(discountAmt).max(BigDecimal.ZERO);

        return new CheckoutPreviewResponse(
                c.getId(),
                c.getUser() != null ? c.getUser().getId() : null,
                items,
                c.getDiscountCode(),
                discountPct,
                subtotal,
                discountAmt,
                total
        );
    }

    // ---------- helpers ----------

    private List<CartItemResponse> items(Cart c) {
        if (c.getItems() == null) return List.of();
        return c.getItems().stream().map(this::toItem).toList();
    }

    private CartItemResponse toItem(CartItem i) {
        Product p = i.getProduct();
        Long productId = (p != null) ? p.getId() : null;
        String name    = (p != null) ? p.getName() : null;
        BigDecimal price = (p != null && p.getPrice() != null) ? p.getPrice() : BigDecimal.ZERO;

        int qty = i.getQuantity() == null ? 0 : i.getQuantity();
        BigDecimal subtotal = price.multiply(BigDecimal.valueOf(qty));

        return new CartItemResponse(productId, name, price, qty, subtotal);
    }

    private BigDecimal nz(BigDecimal v) { return v == null ? BigDecimal.ZERO : v; }

    private BigDecimal calcDiscount(BigDecimal subtotal, BigDecimal pct0to100) {
        if (subtotal.signum() <= 0 || pct0to100.signum() <= 0) return BigDecimal.ZERO;
        BigDecimal factor = pct0to100.divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP);
        return subtotal.multiply(factor).setScale(2, RoundingMode.HALF_UP);
    }
}
