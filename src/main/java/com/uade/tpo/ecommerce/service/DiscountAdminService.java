package com.uade.tpo.ecommerce.service;

import com.uade.tpo.ecommerce.controllers.discounts.DiscountRequest;
import com.uade.tpo.ecommerce.entity.Discount;
import com.uade.tpo.ecommerce.exceptions.DiscountDuplicateException;

import java.util.List;
import java.util.Optional;

public interface DiscountAdminService {

    List<Discount> getDiscounts();

    Optional<Discount> getById(Long id);

    Discount create(DiscountRequest request) throws DiscountDuplicateException;
}
