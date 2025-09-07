package com.uade.tpo.ecommerce.service.impl;

import com.uade.tpo.ecommerce.entity.Discount;
import com.uade.tpo.ecommerce.entity.Order;
import com.uade.tpo.ecommerce.repository.DiscountRepository;
import com.uade.tpo.ecommerce.service.DiscountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Service
@Transactional
public class DiscountServiceImpl implements DiscountService {

    @Autowired private DiscountRepository discounts;

    @Override
    public Discount getByCodeOrThrow(String code) {
        return discounts.findByCodeIgnoreCase(code)
                .filter(d -> Boolean.TRUE.equals(d.getActive()))
                .orElseThrow(() -> new IllegalArgumentException("Cupón inexistente o inactivo."));
    }

    @Override
    public DiscountPreview preview(String code, BigDecimal subtotal) {
        try {
            Discount d = getByCodeOrThrow(code);
            if (d.getStartsAt() != null && LocalDateTime.now().isBefore(d.getStartsAt()))
                return error(code, subtotal, "El cupón todavía no está vigente.");
            if (d.getEndsAt() != null && LocalDateTime.now().isAfter(d.getEndsAt()))
                return error(code, subtotal, "El cupón está vencido.");

            BigDecimal discountAmount = subtotal
                    .multiply(d.getPercentage())
                    .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
            BigDecimal total = subtotal.subtract(discountAmount).max(BigDecimal.ZERO);

            return new DiscountPreview(d.getCode(), d.getPercentage(), subtotal, discountAmount, total, "OK");
        } catch (IllegalArgumentException ex) {
            return error(code, subtotal, ex.getMessage());
        }
    }

    @Override
    public void attachToOrder(Order order, Discount discount, BigDecimal subtotal) {
        // En este enfoque ya no guardamos un OrderDiscount aparte.
        // Solo seteamos los campos de la orden:
        BigDecimal discountAmount = subtotal
                .multiply(discount.getPercentage())
                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);

        order.setDiscountCode(discount.getCode());
        order.setDiscountPercent(discount.getPercentage());
        order.setDiscountAmount(discountAmount);
        order.setTotal(subtotal.subtract(discountAmount));
    }

    private DiscountPreview error(String code, BigDecimal subtotal, String message) {
        return new DiscountPreview(code, BigDecimal.ZERO, subtotal, BigDecimal.ZERO, subtotal, message);
    }
}
