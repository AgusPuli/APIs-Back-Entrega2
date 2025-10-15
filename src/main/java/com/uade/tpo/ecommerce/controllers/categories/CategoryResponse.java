package com.uade.tpo.ecommerce.controllers.categories;

import com.uade.tpo.ecommerce.entity.CategoryType;

public record CategoryResponse(Long id, CategoryType name, String description) {}