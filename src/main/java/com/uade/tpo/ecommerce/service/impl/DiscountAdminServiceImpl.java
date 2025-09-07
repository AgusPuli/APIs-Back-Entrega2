package com.uade.tpo.ecommerce.service.impl;

import com.uade.tpo.ecommerce.controllers.discounts.DiscountRequest;
import com.uade.tpo.ecommerce.entity.Discount;
import com.uade.tpo.ecommerce.exceptions.DiscountDuplicateException;
import com.uade.tpo.ecommerce.repository.DiscountRepository;
import com.uade.tpo.ecommerce.service.DiscountAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service // igual que CategoryServiceImpl
@Transactional
public class DiscountAdminServiceImpl implements DiscountAdminService {

    @Autowired
    private DiscountRepository discounts;

    @Override
    public List<Discount> getDiscounts() {
        return discounts.findAll();
    }

    @Override
    public Optional<Discount> getById(Long id) {
        return discounts.findById(id);
    }

    @Override
    public Discount create(DiscountRequest req) throws DiscountDuplicateException {
        // Validaciones mínimas (mismo espíritu que Category)
        if (req.getCode() == null || req.getCode().trim().isEmpty()) {
            throw new IllegalArgumentException("code es obligatorio");
        }
        if (req.getPercentage() == null) {
            throw new IllegalArgumentException("percentage es obligatorio");
        }
        BigDecimal p = req.getPercentage();
        if (p.compareTo(BigDecimal.ZERO) <= 0 || p.compareTo(new BigDecimal("100")) > 0) {
            throw new IllegalArgumentException("percentage debe ser > 0 y <= 100");
        }

        LocalDateTime starts = req.getStartsAt();
        LocalDateTime ends   = req.getEndsAt();
        if (starts != null && ends != null && ends.isBefore(starts)) {
            throw new IllegalArgumentException("endsAt no puede ser anterior a startsAt");
        }

        // Normalización de código
        String normalized = req.getCode().trim().toUpperCase();

        // Construcción de la entidad (siguiendo tu entity Discount)
        Discount d = Discount.builder()
                .code(normalized)
                .percentage(p)
                .active(req.getActive() == null ? Boolean.TRUE : req.getActive())
                .startsAt(starts)
                .endsAt(ends)
                .build();

        try {
            return discounts.save(d);
        } catch (DataIntegrityViolationException e) {
            // unique constraint por code
            throw new DiscountDuplicateException();
        }
    }
}
