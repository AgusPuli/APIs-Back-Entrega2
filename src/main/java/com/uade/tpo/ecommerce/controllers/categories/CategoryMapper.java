package com.uade.tpo.ecommerce.controllers.categories;

import com.uade.tpo.ecommerce.entity.Category;
import org.springframework.stereotype.Component;


@Component
public class CategoryMapper {

    public CategoryResponse toResponse(Category c) {
        if (c == null) return null;
        return new CategoryResponse(c.getId(), c.getName(), c.getDescription());
    }
}
