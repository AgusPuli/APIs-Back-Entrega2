package com.uade.tpo.ecommerce.controllers.products;

import com.uade.tpo.ecommerce.entity.Category;

import java.math.BigDecimal;

public record ProductResponse(Long id,
                              String name,
                              String description,
                              Category category,
                              BigDecimal price,
                              Integer stock
){}